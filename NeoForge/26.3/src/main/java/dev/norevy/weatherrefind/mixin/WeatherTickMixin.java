package dev.norevy.weatherrefind.mixin;
import dev.norevy.weatherrefind.WeatherEngine;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ClientLevel.class)
public abstract class WeatherTickMixin {
 @Inject(method="tickWeatherEffects",at=@At("HEAD"),cancellable=true)
 private void weatherrefind$cancelWeatherEffects(CallbackInfo ci) {
  if (WeatherEngine.suppressVanillaWeather()) ci.cancel();
 }
}
