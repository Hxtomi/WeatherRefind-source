package dev.norevy.weatherrefind.mixin;
import dev.norevy.weatherrefind.WeatherEngine;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(WeatherEffectRenderer.class)
public abstract class RainRendererMixin {
    @Inject(method="extractRenderState",at=@At("HEAD"),cancellable=true)
    private void weatherrefind$cancelExtraction(ClientLevel level, float delta, Vec3 camera, WeatherRenderState state, CallbackInfo ci) {
        if (WeatherEngine.suppressVanillaWeather()) {
            state.rainColumns.clear();
            state.snowColumns.clear();
            ci.cancel();
        }
    }
}
