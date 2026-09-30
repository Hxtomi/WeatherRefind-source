package dev.norevy.weatherrefind;
/** Immutable client snapshot: render/tick code never accesses an unloaded loader config. */
public record WeatherSettings(
    WeatherMode weatherMode,
    boolean disableSnowFade,
    boolean customParticlesEnabled,
    int particleRange,
    int rainParticleCount,
    int snowParticleCount,
    int particleTickDelay,
    double rainDensity,
    double snowDensity,
    boolean disableVanillaWeather,
    double rainSoundVolume,
    int rainMinHeight,
    int rainMaxHeight,
    int rainMinLifetime,
    int rainMaxLifetime,
    double rainGravity,
    double rainQuadSizeMin,
    double rainQuadSizeMax,
    double rainAlpha,
    int snowMinHeight,
    int snowMaxHeight,
    int snowMinLifetime,
    int snowMaxLifetime,
    double snowGravity,
    double snowQuadSizeMin,
    double snowQuadSizeMax,
    double snowAlpha
) {
    public static WeatherSettings defaults() {
        return new WeatherSettings(WeatherMode.AUTO, false, true, 32, 19, 16, 1, 0.8, 0.9, true, 0.5, 6, 32, 40, 80, 2.0, 0.2, 0.3, 0.7, -10, 16, 60, 90, 0.04, 0.08, 0.12, 0.8);
    }
}
