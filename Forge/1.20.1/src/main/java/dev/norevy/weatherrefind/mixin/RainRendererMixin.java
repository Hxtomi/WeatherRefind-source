package dev.norevy.weatherrefind.mixin;
import dev.norevy.weatherrefind.WeatherEngine;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LevelRenderer.class)
public abstract class RainRendererMixin {
    @Inject(method="tickRain",at=@At("HEAD"),cancellable=true)
    private void weatherrefind$cancelTickRain(CallbackInfo ci) {
        if (WeatherEngine.suppressVanillaWeather()) ci.cancel();
    }
    @Inject(method="renderSnowAndRain",at=@At("HEAD"),cancellable=true)
    private void weatherrefind$cancelVanillaWeather(CallbackInfo ci) {
        if (WeatherEngine.suppressVanillaWeather()) ci.cancel();
    }
}
