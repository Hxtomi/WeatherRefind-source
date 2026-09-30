package dev.norevy.weatherrefind;

import net.minecraftforge.common.ForgeConfigSpec;

public class WeatherRefindConfig {

    public static final ForgeConfigSpec SPEC;
    public static volatile boolean ready;
    public static volatile WeatherSettings current=WeatherSettings.defaults();
    public static final ForgeConfigSpec.EnumValue<WeatherMode> WEATHER_MODE;
    private static final int CURRENT_CONFIG_VERSION = 3;

    public static final ForgeConfigSpec.IntValue CONFIG_VERSION;

    public static final ForgeConfigSpec.BooleanValue DISABLE_SNOW_FADE;
    public static final ForgeConfigSpec.BooleanValue CUSTOM_PARTICLES_ENABLED;
    public static final ForgeConfigSpec.IntValue PARTICLE_RANGE;
    public static final ForgeConfigSpec.IntValue RAIN_PARTICLE_COUNT;
    public static final ForgeConfigSpec.IntValue SNOW_PARTICLE_COUNT;
    public static final ForgeConfigSpec.IntValue PARTICLE_TICK_DELAY;
    public static final ForgeConfigSpec.DoubleValue RAIN_DENSITY;
    public static final ForgeConfigSpec.DoubleValue SNOW_DENSITY;
    public static final ForgeConfigSpec.BooleanValue DISABLE_VANILLA_WEATHER;
    public static final ForgeConfigSpec.DoubleValue RAIN_SOUND_VOLUME;

    public static final ForgeConfigSpec.IntValue RAIN_MIN_HEIGHT;
    public static final ForgeConfigSpec.IntValue RAIN_MAX_HEIGHT;
    public static final ForgeConfigSpec.IntValue RAIN_MIN_LIFETIME;
    public static final ForgeConfigSpec.IntValue RAIN_MAX_LIFETIME;
    public static final ForgeConfigSpec.DoubleValue RAIN_GRAVITY;
    public static final ForgeConfigSpec.DoubleValue RAIN_QUAD_SIZE_MIN;
    public static final ForgeConfigSpec.DoubleValue RAIN_QUAD_SIZE_MAX;
    public static final ForgeConfigSpec.DoubleValue RAIN_ALPHA;

    public static final ForgeConfigSpec.IntValue SNOW_MIN_HEIGHT;
    public static final ForgeConfigSpec.IntValue SNOW_MAX_HEIGHT;
    public static final ForgeConfigSpec.IntValue SNOW_MIN_LIFETIME;
    public static final ForgeConfigSpec.IntValue SNOW_MAX_LIFETIME;
    public static final ForgeConfigSpec.DoubleValue SNOW_GRAVITY;
    public static final ForgeConfigSpec.DoubleValue SNOW_QUAD_SIZE_MIN;
    public static final ForgeConfigSpec.DoubleValue SNOW_QUAD_SIZE_MAX;
    public static final ForgeConfigSpec.DoubleValue SNOW_ALPHA;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Internal version tracker \u2014 do not change manually").push("internal");

        CONFIG_VERSION = builder
                .comment("Config format version. Existing settings are preserved when upgrading.")
                .defineInRange("configVersion", CURRENT_CONFIG_VERSION, 0, 100);

        builder.pop();

        builder.comment("General Settings").push("general");
        WEATHER_MODE = builder.comment("AUTO uses vanilla weather with Iris/Oculus shaders; CUSTOM always uses WR; VANILLA restores vanilla weather and sounds.").defineEnum("weatherMode", WeatherMode.AUTO);

        DISABLE_SNOW_FADE = builder
                .comment("Turn this on if your snow gets dark with shaders")
                .define("disableSnowFade", false);

        CUSTOM_PARTICLES_ENABLED = builder
                .comment("Enable custom weather particles")
                .define("customParticlesEnabled", true);

        PARTICLE_RANGE = builder
                .comment("Horizontal range around the player to spawn particles (blocks)")
                .defineInRange("particleRange", 32, 4, 64);

        RAIN_PARTICLE_COUNT = builder
                .comment("Number of rain particles to spawn per cycle")
                .defineInRange("rainParticleCount", 19, 1, 128);

        SNOW_PARTICLE_COUNT = builder
                .comment("Number of snow particles to spawn per cycle")
                .defineInRange("snowParticleCount", 16, 1, 128);

        PARTICLE_TICK_DELAY = builder
                .comment("Delay in ticks between particle spawn cycles (0 = every tick, 1 = every other tick)")
                .defineInRange("particleTickDelay", 1, 0, 20);

        RAIN_DENSITY = builder
                .comment("Rain spawn probability per attempt (0.0 to 1.0, lower = fewer rain particles)")
                .defineInRange("rainDensity", 0.8D, 0.0D, 1.0D);

        SNOW_DENSITY = builder
                .comment("Snow spawn probability per attempt (0.0 to 1.0, lower = fewer snow particles)")
                .defineInRange("snowDensity", 0.9D, 0.0D, 1.0D);

        DISABLE_VANILLA_WEATHER = builder
                .comment("Disable vanilla weather rendering (rain streaks AND snow). Recommended: true when using custom particles.")
                .define("disableVanillaWeather", true);

        RAIN_SOUND_VOLUME = builder
                .comment("Rain sound volume multiplier (0.0 = muted, 0.5 = half, 1.0 = vanilla). Only works when disableVanillaWeather is true.")
                .defineInRange("rainSoundVolume", 0.5D, 0.0D, 1.0D);

        builder.pop();

        builder.comment("Advanced Rain Settings \u2014 changing these may break visual quality, values are interdependent").push("advanced_rain");

        RAIN_MIN_HEIGHT = builder
                .comment("Minimum rain spawn height above the player (blocks)")
                .defineInRange("rainMinHeight", 6, 0, 128);

        RAIN_MAX_HEIGHT = builder
                .comment("Maximum rain spawn height above the player (blocks)")
                .defineInRange("rainMaxHeight", 32, 1, 256);

        RAIN_MIN_LIFETIME = builder
                .comment("Minimum rain particle lifetime (ticks, 20 ticks = 1 second)")
                .defineInRange("rainMinLifetime", 40, 1, 200);

        RAIN_MAX_LIFETIME = builder
                .comment("Maximum rain particle lifetime (ticks, 20 ticks = 1 second)")
                .defineInRange("rainMaxLifetime", 80, 1, 200);

        RAIN_GRAVITY = builder
                .comment("Rain particle gravity (higher = falls faster)")
                .defineInRange("rainGravity", 2.0D, 0.0D, 5.0D);

        RAIN_QUAD_SIZE_MIN = builder
                .comment("Minimum rain particle size")
                .defineInRange("rainQuadSizeMin", 0.2D, 0.01D, 2.0D);

        RAIN_QUAD_SIZE_MAX = builder
                .comment("Maximum rain particle size")
                .defineInRange("rainQuadSizeMax", 0.3D, 0.01D, 2.0D);

        RAIN_ALPHA = builder
                .comment("Rain particle opacity (0.0 = invisible, 1.0 = fully opaque)")
                .defineInRange("rainAlpha", 0.7D, 0.0D, 1.0D);

        builder.pop();

        builder.comment("Advanced Snow Settings \u2014 changing these may break visual quality, values are interdependent").push("advanced_snow");

        SNOW_MIN_HEIGHT = builder
                .comment("Minimum snow spawn height relative to the player (blocks, negative = below player)")
                .defineInRange("snowMinHeight", -10, -128, 128);

        SNOW_MAX_HEIGHT = builder
                .comment("Maximum snow spawn height above the player (blocks)")
                .defineInRange("snowMaxHeight", 16, 1, 256);

        SNOW_MIN_LIFETIME = builder
                .comment("Minimum snow particle lifetime (ticks, 20 ticks = 1 second)")
                .defineInRange("snowMinLifetime", 60, 1, 400);

        SNOW_MAX_LIFETIME = builder
                .comment("Maximum snow particle lifetime (ticks, 20 ticks = 1 second)")
                .defineInRange("snowMaxLifetime", 90, 1, 400);

        SNOW_GRAVITY = builder
                .comment("Snow particle gravity (higher = falls faster)")
                .defineInRange("snowGravity", 0.04D, 0.0D, 5.0D);

        SNOW_QUAD_SIZE_MIN = builder
                .comment("Minimum snow particle size")
                .defineInRange("snowQuadSizeMin", 0.08D, 0.01D, 2.0D);

        SNOW_QUAD_SIZE_MAX = builder
                .comment("Maximum snow particle size")
                .defineInRange("snowQuadSizeMax", 0.12D, 0.01D, 2.0D);

        SNOW_ALPHA = builder
                .comment("Snow particle target opacity (0.0 = invisible, 1.0 = fully opaque, fade-in over 5 ticks)")
                .defineInRange("snowAlpha", 0.8D, 0.0D, 1.0D);

        builder.pop();

        SPEC = builder.build();
    }

    public static void resetIfOutdated() {
        if (CONFIG_VERSION.get() < CURRENT_CONFIG_VERSION) CONFIG_VERSION.set(CURRENT_CONFIG_VERSION);
        refresh();
    }
    public static void refresh() {
        current=new WeatherSettings(WEATHER_MODE.get(), DISABLE_SNOW_FADE.get(), CUSTOM_PARTICLES_ENABLED.get(), PARTICLE_RANGE.get(), RAIN_PARTICLE_COUNT.get(), SNOW_PARTICLE_COUNT.get(), PARTICLE_TICK_DELAY.get(), RAIN_DENSITY.get(), SNOW_DENSITY.get(), DISABLE_VANILLA_WEATHER.get(), RAIN_SOUND_VOLUME.get(), RAIN_MIN_HEIGHT.get(), RAIN_MAX_HEIGHT.get(), RAIN_MIN_LIFETIME.get(), RAIN_MAX_LIFETIME.get(), RAIN_GRAVITY.get(), RAIN_QUAD_SIZE_MIN.get(), RAIN_QUAD_SIZE_MAX.get(), RAIN_ALPHA.get(), SNOW_MIN_HEIGHT.get(), SNOW_MAX_HEIGHT.get(), SNOW_MIN_LIFETIME.get(), SNOW_MAX_LIFETIME.get(), SNOW_GRAVITY.get(), SNOW_QUAD_SIZE_MIN.get(), SNOW_QUAD_SIZE_MAX.get(), SNOW_ALPHA.get());
        ready=true;
    }
    public static void setMode(WeatherMode mode) {
        if (!ready) return;
        WEATHER_MODE.set(mode); CUSTOM_PARTICLES_ENABLED.set(true); DISABLE_VANILLA_WEATHER.set(true);
        refresh(); SPEC.save();
    }
}
