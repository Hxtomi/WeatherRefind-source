package dev.norevy.weatherrefind;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherRefind implements ModInitializer {

    public static final String MOD_ID = "weatherrefind";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[WeatherRefind] Initializing WeatherRefind for 26.3...");
        WeatherRefindConfig.load();
        ModParticles.register();
        LOGGER.info("[WeatherRefind] Initialization complete.");
    }
}
