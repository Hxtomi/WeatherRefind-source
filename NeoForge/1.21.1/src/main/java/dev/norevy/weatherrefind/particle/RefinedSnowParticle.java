package dev.norevy.weatherrefind.particle;

import dev.norevy.weatherrefind.WeatherRefindConfig;
import dev.norevy.weatherrefind.WeatherEngine;
import dev.norevy.weatherrefind.WeatherMath;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class RefinedSnowParticle extends TextureSheetParticle {

    private static int aliveCount = 0;
    private final int weatherGeneration=WeatherEngine.generation();
    private static final int FADE_IN_TICKS = 5;

    private final SpriteSet spriteSet;
    private final float targetAlpha;
    private final boolean disableFade;

    public RefinedSnowParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        ++aliveCount;
        this.spriteSet = spriteSet;
        int minLife = WeatherRefindConfig.current.snowMinLifetime();
        int maxLife = WeatherRefindConfig.current.snowMaxLifetime();
        this.lifetime = minLife + this.random.nextInt(Math.max(1, maxLife - minLife + 1));
        this.gravity = (float) WeatherRefindConfig.current.snowGravity();
        this.hasPhysics = true;
        double sizeMin = WeatherRefindConfig.current.snowQuadSizeMin();
        double sizeMax = WeatherRefindConfig.current.snowQuadSizeMax();
        this.quadSize = (float) (sizeMin + this.random.nextFloat() * Math.max(0.0D, sizeMax - sizeMin));

        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.targetAlpha = (float) WeatherRefindConfig.current.snowAlpha();
        this.disableFade = WeatherRefindConfig.current.disableSnowFade();
        this.alpha = this.disableFade ? this.targetAlpha : 0.0F;

        this.xd = (this.random.nextFloat() - 0.5F) * 0.02F;
        this.yd = -0.01F;
        this.zd = (this.random.nextFloat() - 0.5F) * 0.02F;

        this.setSprite(spriteSet.get(this.random));
    }

    @Override
    public void tick() {
        if (!WeatherEngine.isCustomWeather() || weatherGeneration != WeatherEngine.generation()) { remove(); return; }
        super.tick();
        if (this.removed || this.onGround) { this.remove(); return; }
        this.xd += (this.random.nextFloat()-0.5F)*0.005F;
        this.zd += (this.random.nextFloat()-0.5F)*0.005F;
        if (!this.disableFade) this.alpha=WeatherMath.alpha(this.age,this.lifetime,this.targetAlpha,FADE_IN_TICKS,10);
    }

    @Override
    public float getQuadSize(float partialTick) {
        return WeatherEngine.isCustomWeather() && weatherGeneration == WeatherEngine.generation() ? super.getQuadSize(partialTick) : 0.0F;
    }

    @Override
    public void remove() {
        if (!this.removed && weatherGeneration == WeatherEngine.generation()) {
            aliveCount = Math.max(0, aliveCount - 1);
        }
        super.remove();
    }

    public static int getAliveCount() {
        return aliveCount;
    }

    public static void resetAliveCount() {
        aliveCount = 0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        private static SpriteSet cachedSpriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
            cachedSpriteSet = spriteSet;
            WeatherEngine.invalidate();
        }

        /** Returns the cached SpriteSet for direct particle instantiation (bypasses registry). */
        public static SpriteSet getCachedSpriteSet() {
            return cachedSpriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new RefinedSnowParticle(level, x, y, z, this.spriteSet);
        }
    }
}
