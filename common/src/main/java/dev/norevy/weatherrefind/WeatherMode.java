package dev.norevy.weatherrefind;
public enum WeatherMode {
    AUTO, CUSTOM, VANILLA;
    public WeatherMode next() { return values()[(ordinal() + 1) % values().length]; }
}
