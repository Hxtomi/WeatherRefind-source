package dev.norevy.weatherrefind;
import dev.norevy.weatherrefind.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
public final class WeatherRefindClient {
    @Mod.EventBusSubscriber(modid=WeatherRefind.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
    public static final class ModBus {
        @SubscribeEvent public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.RAIN.get(), RefinedRainParticle.Provider::new);
            event.registerSpriteSet(ModParticles.SNOW.get(), RefinedSnowParticle.Provider::new);
        }
    }
    @Mod.EventBusSubscriber(modid=WeatherRefind.MOD_ID,bus=Mod.EventBusSubscriber.Bus.FORGE,value=Dist.CLIENT)
    public static final class ForgeBus {
        @SubscribeEvent public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) WeatherEngine.tick(Minecraft.getInstance());
        }
    }
}
