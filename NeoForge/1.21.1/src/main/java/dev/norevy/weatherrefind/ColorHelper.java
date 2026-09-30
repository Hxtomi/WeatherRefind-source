package dev.norevy.weatherrefind;

public class ColorHelper {

    public static int getRainColor(float thunderLevel) {
        int baseR = 200;
        int baseG = 210;
        int baseB = 255;

        int thunderR = 150;
        int thunderG = 155;
        int thunderB = 180;

        int r = (int) (baseR + (thunderR - baseR) * thunderLevel);
        int g = (int) (baseG + (thunderG - baseG) * thunderLevel);
        int b = (int) (baseB + (thunderB - baseB) * thunderLevel);

        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getSnowColor() {
        return 0xFFFFFFFF;
    }

    public static float getRed(int color) {
        return ((color >> 16) & 0xFF) / 255.0F;
    }

    public static float getGreen(int color) {
        return ((color >> 8) & 0xFF) / 255.0F;
    }

    public static float getBlue(int color) {
        return (color & 0xFF) / 255.0F;
    }

    public static float getAlpha(int color) {
        return ((color >> 24) & 0xFF) / 255.0F;
    }
}
