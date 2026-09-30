package dev.norevy.weatherrefind;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("weatherrefind")
public class WeatherRefind {

    public static final String MOD_ID = "weatherrefind";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WeatherRefind() {
        if (FMLEnvironment.dist != Dist.CLIENT) return;
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModParticles.PARTICLE_TYPES.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WeatherRefindConfig.SPEC);

        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);
        modBus.addListener(this::onConfigUnload);
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
