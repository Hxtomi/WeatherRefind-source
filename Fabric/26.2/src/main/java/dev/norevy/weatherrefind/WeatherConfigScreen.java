package dev.norevy.weatherrefind;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
public final class WeatherConfigScreen extends Screen {
    private final Screen parent;
    public WeatherConfigScreen(Screen parent) { super(Component.literal("Weather Refind")); this.parent=parent; }
    private Component modeLabel() { return Component.translatable("weatherrefind.mode", Component.translatable("weatherrefind.mode."+WeatherRefindConfig.current.weatherMode().name().toLowerCase(java.util.Locale.ROOT))); }
    @Override protected void init() {
        addRenderableWidget(Button.builder(modeLabel(), button -> {
            WeatherRefindConfig.setMode(WeatherRefindConfig.current.weatherMode().next());
            WeatherEngine.updateMode();
            button.setMessage(modeLabel());
        }).bounds(width/2-100,height/2-30,200,20).build()).active=WeatherRefindConfig.ready;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(width/2-100,height/2+60,200,20).build());
    }
    @Override public void onClose() { minecraft.gui.setScreen(parent); }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics,int mouseX,int mouseY,float delta) {
        
        super.extractRenderState(graphics,mouseX,mouseY,delta);
        graphics.centeredText(font,title,width/2,height/2-65,0xffffffff);
        graphics.centeredText(font,Component.translatable("weatherrefind.auto.info"),width/2,height/2+5,0xffaaaaaa);
        graphics.centeredText(font,Component.translatable("weatherrefind.vanilla.info"),width/2,height/2+20,0xffaaaaaa);
    }
}
