package dev.norevy.weatherrefind;
import dev.norevy.weatherrefind.particle.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
public final class WeatherRefindClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(ModParticles.RAIN, RefinedRainParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.SNOW, RefinedSnowParticle.Provider::new);
        ClientTickEvents.END_CLIENT_TICK.register(WeatherEngine::tick);
    }
}
