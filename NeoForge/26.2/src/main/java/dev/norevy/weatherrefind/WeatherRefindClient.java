package dev.norevy.weatherrefind;
import dev.norevy.weatherrefind.particle.*;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
public final class WeatherRefindClient {
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.RAIN.get(), RefinedRainParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SNOW.get(), RefinedSnowParticle.Provider::new);
    }
    public static void onClientTick(ClientTickEvent.Post event) {
        WeatherEngine.tick(Minecraft.getInstance());
    }
}
