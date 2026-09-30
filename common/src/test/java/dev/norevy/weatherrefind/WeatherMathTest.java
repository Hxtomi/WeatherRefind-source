package dev.norevy.weatherrefind;

/** Run with javac/java, no Minecraft instance or external test framework required. */
public final class WeatherMathTest {
    private static int checks;
    private static void require(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        for (WeatherMode mode : WeatherMode.values()) {
            require(!WeatherMath.useCustom(false, mode, true, false), "Unloaded config must leave vanilla enabled");
            require(!WeatherMath.useCustom(true, mode, false, false), "Legacy disable must leave vanilla enabled");
        }
        require(WeatherMath.useCustom(true, WeatherMode.AUTO, true, false), "AUTO without shaders uses WR");
        require(!WeatherMath.useCustom(true, WeatherMode.AUTO, true, true), "AUTO with shaders uses vanilla");
        require(WeatherMath.useCustom(true, WeatherMode.CUSTOM, true, true), "Explicit custom mode overrides AUTO");
        require(!WeatherMath.useCustom(true, WeatherMode.VANILLA, true, false), "Vanilla mode disables WR");
        require(!WeatherMath.audibleSurface(0, 124, 0), "No surface rain at bedrock");
        require(!WeatherMath.audibleSurface(0, 9, 0), "Distant roof must not sound at listener");
        require(WeatherMath.audibleSurface(0, 3, 0), "Nearby roof rain is audible");
        require(WeatherMath.audibleSurface(8, 0, 0), "Nearby cave entrance rain is audible");
        require(!WeatherMath.audibleSurface(13, 0, 0), "Distant outside rain is inaudible");
        int cavePlayer=-60, surface=64;
        require(WeatherMath.spawnBottom(cavePlayer,6,surface)>=cavePlayer+32, "Deep cave spawn interval must be empty");
        require(WeatherMath.spawnBottom(64,6,67)==70, "Shelter preserves bounded outside spawn height");
        for (int lifetime : new int[]{40,80,200}) {
            float previous=1;
            for (int age=lifetime-10; age<=lifetime; age++) {
                float current=WeatherMath.alpha(age,lifetime,0.7F,5,10);
                require(current<=previous, "Fade-out must never restore opacity");
                require(current>=0 && current<=0.7F, "Opacity remains bounded");
                previous=current;
            }
            require(previous==0,"Particle ends fully transparent");
        }
        System.out.println("Weather regression checks passed: "+checks);
    }
}
