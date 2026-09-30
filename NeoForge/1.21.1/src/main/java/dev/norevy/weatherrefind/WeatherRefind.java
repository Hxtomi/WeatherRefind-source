package dev.norevy.weatherrefind;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod("weatherrefind")
public class WeatherRefind {

    public static final String MOD_ID = "weatherrefind";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WeatherRefind(IEventBus modBus, ModContainer modContainer) {
        if (FMLEnvironment.dist != Dist.CLIENT) return;
        ModParticles.PARTICLE_TYPES.register(modBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, WeatherRefindConfig.SPEC);

        modBus.addListener(WeatherRefindClient::registerParticleProviders);
        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);
        modBus.addListener(this::onConfigUnload);
        NeoForge.EVENT_BUS.addListener(WeatherRefindClient::onClientTick);
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == WeatherRefindConfig.SPEC) {
            WeatherRefindConfig.resetIfOutdated();
        }
    }
    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == WeatherRefindConfig.SPEC) WeatherRefindConfig.refresh();
    }
    private void onConfigUnload(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == WeatherRefindConfig.SPEC) WeatherRefindConfig.ready=false;
    }
}
