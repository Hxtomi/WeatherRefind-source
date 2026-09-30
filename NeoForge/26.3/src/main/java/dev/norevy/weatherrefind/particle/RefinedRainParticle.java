package dev.norevy.weatherrefind.particle;

import dev.norevy.weatherrefind.WeatherRefindConfig;
import dev.norevy.weatherrefind.WeatherEngine;
import dev.norevy.weatherrefind.WeatherMath;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class RefinedRainParticle extends SingleQuadParticle {

    private static int aliveCount = 0;
    private final int weatherGeneration=WeatherEngine.generation();

    private final SpriteSet sprites;
    private final float targetAlpha;
    private final int fadeInTicks;

    public RefinedRainParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, int fadeInTicks) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        this.fadeInTicks = Math.max(0, fadeInTicks);
        this.lifetime = WeatherRefindConfig.current.rainMinLifetime() + this.random.nextInt(Math.max(1, WeatherRefindConfig.current.rainMaxLifetime() - WeatherRefindConfig.current.rainMinLifetime() + 1));
        this.gravity = (float) WeatherRefindConfig.current.rainGravity();
        this.hasPhysics = true;
        this.quadSize = (float) (WeatherRefindConfig.current.rainQuadSizeMin() + this.random.nextFloat() * Math.max(0.0, WeatherRefindConfig.current.rainQuadSizeMax() - WeatherRefindConfig.current.rainQuadSizeMin()));
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.targetAlpha = (float) WeatherRefindConfig.current.rainAlpha();
        this.alpha = this.fadeInTicks > 0 ? 0.0F : this.targetAlpha;
        this.setSprite(sprites.get(this.random));
        ++aliveCount;
    }

    @Override
    public void tick() {
        if (!WeatherEngine.isCustomWeather() || weatherGeneration != WeatherEngine.generation()) { remove(); return; }
        super.tick();
        if (this.removed || this.onGround) { this.remove(); return; }
        this.alpha=WeatherMath.alpha(this.age,this.lifetime,this.targetAlpha,this.fadeInTicks,10);
    }

    @Override
    public float getQuadSize(float partialTick) {
        return WeatherEngine.isCustomWeather() && weatherGeneration == WeatherEngine.generation() ? super.getQuadSize(partialTick) : 0.0F;
    }

    @Override
    public void remove() {
        if (!this.removed && weatherGeneration == WeatherEngine.generation()) {
            aliveCount = Math.max(0, aliveCount - 1);
        }
        super.remove();
    }

    public static int getAliveCount() {
        return aliveCount;
    }

    public static void resetAliveCount() {
        aliveCount = 0;
    }

    @Override
    public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
        return SingleQuadParticle.FacingCameraMode.LOOKAT_Y;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private static SpriteSet cachedSpriteSet;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
            cachedSpriteSet = sprites;
            WeatherEngine.invalidate();
        }

        public static SpriteSet getCachedSpriteSet() {
            return cachedSpriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed,
                                       RandomSource random) {
            return new RefinedRainParticle(level, x, y, z, this.sprites, Mth.ceil(ySpeed));
        }
    }
}
