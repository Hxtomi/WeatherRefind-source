package dev.norevy.weatherrefind;

import dev.norevy.weatherrefind.particle.RefinedRainParticle;
import dev.norevy.weatherrefind.particle.RefinedSnowParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

/** Shared client engine. All work is bounded; no chunk loads, cover scans or particle-engine clears. */
public final class WeatherEngine {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final BlockPos.MutableBlockPos POS = new BlockPos.MutableBlockPos();
    private static final int[] SOUND_X = {0, 4, -4, 0, 0, 8, -8, 0, 0};
    private static final int[] SOUND_Z = {0, 0, 0, 4, -4, 0, 0, 8, -8};
    private static ClientLevel lastLevel;
    private static boolean custom, suppressVanilla;
    private static volatile int generation;
    private static int particleCooldown, soundCooldown;
    private static float intensity;
    // Bounded diagnostic counters, also used by the external regression harness.
    public static int lastColumnChecks, lastRainSpawns, lastSnowSpawns, lastSoundPlays;

    private WeatherEngine() {}
    public static int generation() { return generation; }
    public static boolean isCustomWeather() { return custom; }
    public static boolean suppressVanillaWeather() { return suppressVanilla; }
    public static void invalidate() {
        generation++;
        particleCooldown = soundCooldown = 0;
        intensity = 0.0F;
        RefinedRainParticle.resetAliveCount();
        RefinedSnowParticle.resetAliveCount();
    }
    public static void updateMode() {
        WeatherSettings s = WeatherRefindConfig.current;
        boolean selected = WeatherMath.useCustom(WeatherRefindConfig.ready, s.weatherMode(),
                s.customParticlesEnabled(), s.weatherMode() == WeatherMode.AUTO && ShaderSupport.active());
        // Missing sprites must leave vanilla visible, including during a resource reload.
        selected &= RefinedRainParticle.Provider.getCachedSpriteSet() != null
                && RefinedSnowParticle.Provider.getCachedSpriteSet() != null;
        if (custom != selected) { custom = selected; invalidate(); }
        suppressVanilla = custom && s.disableVanillaWeather();
    }
    public static void tick(Minecraft mc) {
        lastColumnChecks = lastRainSpawns = lastSnowSpawns = lastSoundPlays = 0;
        ClientLevel level = mc.level;
        if (level != lastLevel) { lastLevel = level; invalidate(); }
        updateMode();
        if (!custom || level == null || mc.player == null || mc.isPaused()) return;
        if (mc.player.isUnderWater()) return;
        WeatherSettings s = WeatherRefindConfig.current;
        float target = level.getRainLevel(1.0F);
        intensity += Mth.clamp(target - intensity, -0.02F, 0.02F);
        if (intensity <= 0.0F) return;
        if (suppressVanilla && s.rainSoundVolume() > 0 && target > 0) tickSound(mc, level, s, target);
        if (particleCooldown > 0) { particleCooldown--; return; }
        particleCooldown = s.particleTickDelay();

        SpriteSet rain = RefinedRainParticle.Provider.getCachedSpriteSet();
        SpriteSet snow = RefinedSnowParticle.Provider.getCachedSpriteSet();
        int px = Mth.floor(mc.player.getX()), py = Mth.floor(mc.player.getY()), pz = Mth.floor(mc.player.getZ());
        int rainCount = (int) (s.rainParticleCount() * intensity + RANDOM.nextFloat());
        int snowCount = (int) (s.snowParticleCount() * intensity + RANDOM.nextFloat());
        int attempts = Math.max(rainCount, snowCount);
        for (int i = 0; i < attempts; i++) {
            double densitySample = RANDOM.nextDouble();
            if (densitySample >= Math.max(s.rainDensity(), s.snowDensity())) continue;
            int x = px + RANDOM.nextInt(s.particleRange()) - RANDOM.nextInt(s.particleRange());
            int z = pz + RANDOM.nextInt(s.particleRange()) - RANDOM.nextInt(s.particleRange());
            if (!level.hasChunk(x >> 4, z >> 4)) continue;
            lastColumnChecks++;
            int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            // Never move the upper bound to a distant roof/surface when underground.
            int maximum = py + Math.max(s.rainMaxHeight(), s.snowMaxHeight());
            if (surface >= maximum) continue;
            POS.set(x, Math.max(surface, py), z);
            Biome.Precipitation precipitation = PlatformWeather.precipitation(level, POS);
            boolean isRain = precipitation == Biome.Precipitation.RAIN;
            boolean isSnow = precipitation == Biome.Precipitation.SNOW;
            if ((!isRain && !isSnow) || i >= (isRain ? rainCount : snowCount)) continue;
            if (densitySample >= (isRain ? s.rainDensity() : s.snowDensity())) continue;
            int min = WeatherMath.spawnBottom(py, isRain ? s.rainMinHeight() : s.snowMinHeight(), surface);
            int max = py + (isRain ? s.rainMaxHeight() : s.snowMaxHeight());
            if (min >= max) continue;
            int y = min + RANDOM.nextInt(max - min);
            POS.set(x, y, z);
            if (level.getFluidState(POS).is(FluidTags.WATER)) continue;
            // Temperature can change with altitude; use the precipitation at the actual spawn point.
            if (PlatformWeather.precipitation(level, POS) != precipitation) continue;
            double sx = x + RANDOM.nextDouble(), sy = y + RANDOM.nextDouble(), sz = z + RANDOM.nextDouble();
            if (isRain) {
                mc.particleEngine.add(new RefinedRainParticle(level, sx, sy, sz, rain, y - py < 15 ? 5 : 0));
                lastRainSpawns++;
            } else {
                mc.particleEngine.add(new RefinedSnowParticle(level, sx, sy, sz, snow));
                lastSnowSpawns++;
            }
        }
    }
    private static void tickSound(Minecraft mc, ClientLevel level, WeatherSettings s, float rain) {
        if (soundCooldown-- > 0) return;
        soundCooldown = 4;
        int px = Mth.floor(mc.player.getX()), py = Mth.floor(mc.player.getY()), pz = Mth.floor(mc.player.getZ());
        double best = Double.POSITIVE_INFINITY, sx = 0, sy = 0, sz = 0;
        for (int i = 0; i < SOUND_X.length; i++) {
            int x = px + SOUND_X[i], z = pz + SOUND_Z[i];
            if (!level.hasChunk(x >> 4, z >> 4)) continue;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            double dx = x + 0.5 - mc.player.getX(), dy = y - mc.player.getY(), dz = z + 0.5 - mc.player.getZ();
            if (!WeatherMath.audibleSurface(dx, dy, dz)) continue;
            POS.set(x, y, z);
            if (PlatformWeather.precipitation(level, POS) != Biome.Precipitation.RAIN) continue;
            double distance = dx * dx + dy * dy + dz * dz;
            if (distance < best) { best = distance; sx = x + 0.5; sy = y + 0.1; sz = z + 0.5; }
        }
        if (!Double.isFinite(best)) return;
        boolean covered = level.getHeight(Heightmap.Types.MOTION_BLOCKING, px, pz) > py + 1;
        float volume = (covered ? 0.1F : 0.2F) * (float) s.rainSoundVolume() * rain;
        // Sound belongs to nearby exposed precipitation, never to the listener's cave position.
        level.playLocalSound(sx, sy, sz, covered ? SoundEvents.WEATHER_RAIN_ABOVE : SoundEvents.WEATHER_RAIN,
                SoundSource.WEATHER, volume, covered ? 0.5F : 1.0F, false);
        lastSoundPlays++;
    }
}
