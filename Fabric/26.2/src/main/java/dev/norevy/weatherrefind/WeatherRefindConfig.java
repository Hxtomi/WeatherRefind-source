package dev.norevy.weatherrefind;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WeatherRefindConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("weatherrefind.json");
    private static final int CURRENT_CONFIG_VERSION = 3;

    public static volatile boolean ready;
    public static volatile WeatherSettings current=WeatherSettings.defaults();
    public static WeatherMode weatherMode=WeatherMode.AUTO;
    public static boolean disableSnowFade = false;
    public static boolean customParticlesEnabled = true;
    public static int particleRange = 32;
    public static int rainParticleCount = 19;
    public static int snowParticleCount = 16;
    public static int particleTickDelay = 1;
    public static double rainDensity = 0.8;
    public static double snowDensity = 0.9;
    public static boolean disableVanillaWeather = true;
    public static double rainSoundVolume = 0.5;

    public static int rainMinHeight = 6;
    public static int rainMaxHeight = 32;
    public static int rainMinLifetime = 40;
    public static int rainMaxLifetime = 80;
    public static double rainGravity = 2.0;
    public static double rainQuadSizeMin = 0.2;
    public static double rainQuadSizeMax = 0.3;
    public static double rainAlpha = 0.7;

    public static int snowMinHeight = -10;
    public static int snowMaxHeight = 16;
    public static int snowMinLifetime = 60;
    public static int snowMaxLifetime = 90;
    public static double snowGravity = 0.04;
    public static double snowQuadSizeMin = 0.08;
    public static double snowQuadSizeMax = 0.12;
    public static double snowAlpha = 0.8;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                ConfigData data = GSON.fromJson(json, ConfigData.class);
                if (data != null) {
                    weatherMode = data.weatherMode == null ? WeatherMode.AUTO : data.weatherMode;
                    disableSnowFade = data.disableSnowFade;
                    customParticlesEnabled = data.customParticlesEnabled;
                    particleRange = clamp(data.particleRange, 4, 64);
                    rainParticleCount = clamp(data.rainParticleCount, 1, 128);
                    snowParticleCount = clamp(data.snowParticleCount, 1, 128);
                    particleTickDelay = clamp(data.particleTickDelay, 0, 20);
                    rainDensity = clampDouble(data.rainDensity, 0.0, 1.0);
                    snowDensity = clampDouble(data.snowDensity, 0.0, 1.0);
                    disableVanillaWeather = data.disableVanillaWeather;
                    rainSoundVolume = clampDouble(data.rainSoundVolume, 0.0, 1.0);

                    rainMinHeight = clamp(data.rainMinHeight, 0, 128);
                    rainMaxHeight = clamp(data.rainMaxHeight, 1, 256);
                    rainMinLifetime = clamp(data.rainMinLifetime, 1, 200);
                    rainMaxLifetime = clamp(data.rainMaxLifetime, 1, 200);
                    rainGravity = clampDouble(data.rainGravity, 0.0, 5.0);
                    rainQuadSizeMin = clampDouble(data.rainQuadSizeMin, 0.01, 2.0);
                    rainQuadSizeMax = clampDouble(data.rainQuadSizeMax, 0.01, 2.0);
                    rainAlpha = clampDouble(data.rainAlpha, 0.0, 1.0);

                    snowMinHeight = clamp(data.snowMinHeight, -128, 128);
                    snowMaxHeight = clamp(data.snowMaxHeight, 1, 256);
                    snowMinLifetime = clamp(data.snowMinLifetime, 1, 400);
                    snowMaxLifetime = clamp(data.snowMaxLifetime, 1, 400);
                    snowGravity = clampDouble(data.snowGravity, 0.0, 5.0);
                    snowQuadSizeMin = clampDouble(data.snowQuadSizeMin, 0.01, 2.0);
                    snowQuadSizeMax = clampDouble(data.snowQuadSizeMax, 0.01, 2.0);
                    snowAlpha = clampDouble(data.snowAlpha, 0.0, 1.0);
                }
            } catch (IOException | JsonParseException e) {
                WeatherRefind.LOGGER.error("[WeatherRefind] Failed to read config {}", CONFIG_PATH, e);
                refresh();
                return; // Preserve malformed user files; do not overwrite them.
            }
        }
        save();
    }

    public static void save() {
        refresh();
        ConfigData data = new ConfigData();
        data.configVersion = CURRENT_CONFIG_VERSION;
        data.weatherMode = weatherMode;
        data.disableSnowFade = disableSnowFade;
        data.customParticlesEnabled = customParticlesEnabled;
        data.particleRange = particleRange;
        data.rainParticleCount = rainParticleCount;
        data.snowParticleCount = snowParticleCount;
        data.particleTickDelay = particleTickDelay;
        data.rainDensity = rainDensity;
        data.snowDensity = snowDensity;
        data.disableVanillaWeather = disableVanillaWeather;
        data.rainSoundVolume = rainSoundVolume;

        data.rainMinHeight = rainMinHeight;
        data.rainMaxHeight = rainMaxHeight;
        data.rainMinLifetime = rainMinLifetime;
        data.rainMaxLifetime = rainMaxLifetime;
        data.rainGravity = rainGravity;
        data.rainQuadSizeMin = rainQuadSizeMin;
        data.rainQuadSizeMax = rainQuadSizeMax;
        data.rainAlpha = rainAlpha;

        data.snowMinHeight = snowMinHeight;
        data.snowMaxHeight = snowMaxHeight;
        data.snowMinLifetime = snowMinLifetime;
        data.snowMaxLifetime = snowMaxLifetime;
        data.snowGravity = snowGravity;
        data.snowQuadSizeMin = snowQuadSizeMin;
        data.snowQuadSizeMax = snowQuadSizeMax;
        data.snowAlpha = snowAlpha;

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            WeatherRefind.LOGGER.error("[WeatherRefind] Failed to save config {}", CONFIG_PATH, e);
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double clampDouble(double value, double min, double max) {
        return Double.isFinite(value) ? Math.max(min, Math.min(max, value)) : min;
    }

    public static void refresh() {
        current=new WeatherSettings(weatherMode, disableSnowFade, customParticlesEnabled, particleRange, rainParticleCount, snowParticleCount, particleTickDelay, rainDensity, snowDensity, disableVanillaWeather, rainSoundVolume, rainMinHeight, rainMaxHeight, rainMinLifetime, rainMaxLifetime, rainGravity, rainQuadSizeMin, rainQuadSizeMax, rainAlpha, snowMinHeight, snowMaxHeight, snowMinLifetime, snowMaxLifetime, snowGravity, snowQuadSizeMin, snowQuadSizeMax, snowAlpha);
        ready=true;
    }
    public static void setMode(WeatherMode mode) {
        weatherMode=mode; customParticlesEnabled=true; disableVanillaWeather=true; save();
    }

    private static class ConfigData {
        int configVersion = 0;
        WeatherMode weatherMode = WeatherMode.AUTO;
        boolean disableSnowFade = false;
        boolean customParticlesEnabled = true;
        int particleRange = 32;
        int rainParticleCount = 19;
        int snowParticleCount = 16;
        int particleTickDelay = 1;
        double rainDensity = 0.8;
        double snowDensity = 0.9;
        boolean disableVanillaWeather = true;
        double rainSoundVolume = 0.5;

        int rainMinHeight = 6;
        int rainMaxHeight = 32;
        int rainMinLifetime = 40;
        int rainMaxLifetime = 80;
        double rainGravity = 2.0;
        double rainQuadSizeMin = 0.2;
        double rainQuadSizeMax = 0.3;
        double rainAlpha = 0.7;

        int snowMinHeight = -10;
        int snowMaxHeight = 16;
        int snowMinLifetime = 60;
        int snowMaxLifetime = 90;
        double snowGravity = 0.04;
        double snowQuadSizeMin = 0.08;
        double snowQuadSizeMax = 0.12;
        double snowAlpha = 0.8;
    }
}
