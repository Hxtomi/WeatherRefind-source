package dev.norevy.weatherrefind;
/** Shared, loader-independent weather rules. */
public final class WeatherMath {
    private WeatherMath() {}
    public static boolean useCustom(boolean ready, WeatherMode mode, boolean enabled, boolean shaders) {
        return ready && enabled && mode != WeatherMode.VANILLA && (mode != WeatherMode.AUTO || !shaders);
    }
    public static int spawnBottom(int playerY, int minHeight, int surfaceY) {
        return Math.max(playerY + minHeight, surfaceY);
    }
    public static boolean audibleSurface(double dx, double dy, double dz) {
        return Math.abs(dy) <= 8.0 && dx * dx + dy * dy + dz * dz <= 144.0;
    }
    public static float alpha(int age, int lifetime, float target, int fadeIn, int fadeOut) {
        float in = fadeIn <= 0 ? 1.0F : Math.min(1.0F, (float) age / fadeIn);
        float out = fadeOut <= 0 ? 1.0F : Math.min(1.0F, (float) (lifetime - age) / fadeOut);
        return target * Math.max(0.0F, Math.min(in, out));
    }
}
