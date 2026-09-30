package dev.norevy.weatherrefind.mixin;
import dev.norevy.weatherrefind.WeatherConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(OptionsScreen.class)
public abstract class WeatherOptionsMixin extends Screen {
    protected WeatherOptionsMixin(Component title) { super(title); }
    @Inject(method="init",at=@At("TAIL"))
    private void weatherrefind$addOptions(CallbackInfo ci) {
        addRenderableWidget(Button.builder(Component.literal("WR"), button -> minecraft.gui.setScreen(new WeatherConfigScreen((Screen)(Object)this)))
            .bounds(width-34,6,28,20).tooltip(Tooltip.create(Component.literal("Weather Refind"))).build());
    }
}
