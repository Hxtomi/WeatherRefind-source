package dev.norevy.weatherrefind.mixin;
import dev.norevy.weatherrefind.WeatherEngine;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(WeatherEffectRenderer.class)
public abstract class RainRendererMixin {
    @Inject(method="tickRainParticles",at=@At("HEAD"),cancellable=true)
    private void weatherrefind$cancelTickRain(CallbackInfo ci) {
        if (WeatherEngine.suppressVanillaWeather()) ci.cancel();
    }
    @Inject(method="extractRenderState",at=@At("HEAD"),cancellable=true)
    private void weatherrefind$cancelExtraction(Level level, int ticks, float delta, Vec3 camera, WeatherRenderState state, CallbackInfo ci) {
        if (WeatherEngine.suppressVanillaWeather()) {
            state.rainColumns.clear();
            state.snowColumns.clear();
            ci.cancel();
        }
    }
}
