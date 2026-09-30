package dev.norevy.weatherrefind.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.norevy.weatherrefind.WeatherRefindConfig;
import dev.norevy.weatherrefind.WeatherEngine;
import dev.norevy.weatherrefind.WeatherMath;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RefinedSnowParticle extends TextureSheetParticle {

    private static int aliveCount = 0;
    private final int weatherGeneration=WeatherEngine.generation();
    private static final Vector3f[] VERTICES = {
            new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()
    };
    private final SpriteSet spriteSet;
    private final float targetAlpha;
    private static final int FADE_IN_TICKS = 5;
    private final boolean disableFade;

    public RefinedSnowParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        ++aliveCount;
        this.spriteSet = spriteSet;
        int minLife = WeatherRefindConfig.current.snowMinLifetime();
        int maxLife = WeatherRefindConfig.current.snowMaxLifetime();
        this.lifetime = minLife + this.random.nextInt(Math.max(1, maxLife - minLife + 1));
        this.gravity = (float) WeatherRefindConfig.current.snowGravity();
        this.hasPhysics = true;
        double sizeMin = WeatherRefindConfig.current.snowQuadSizeMin();
        double sizeMax = WeatherRefindConfig.current.snowQuadSizeMax();
        this.quadSize = (float)(sizeMin + this.random.nextFloat() * Math.max(0, sizeMax - sizeMin));

        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.targetAlpha = (float) WeatherRefindConfig.current.snowAlpha();
        this.disableFade = WeatherRefindConfig.current.disableSnowFade();
        this.alpha = this.disableFade ? this.targetAlpha : 0.0F;

        this.xd = (this.random.nextFloat() - 0.5F) * 0.02F;
        this.yd = -0.01F;
        this.zd = (this.random.nextFloat() - 0.5F) * 0.02F;

        this.setSprite(spriteSet.get(this.random));
    }

    @Override
    public void tick() {
        if (!WeatherEngine.isCustomWeather() || weatherGeneration != WeatherEngine.generation()) { remove(); return; }
        super.tick();
        if (this.removed || this.onGround) { this.remove(); return; }
        this.xd += (this.random.nextFloat()-0.5F)*0.005F;
        this.zd += (this.random.nextFloat()-0.5F)*0.005F;
        if (!this.disableFade) this.alpha=WeatherMath.alpha(this.age,this.lifetime,this.targetAlpha,FADE_IN_TICKS,10);
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
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp((double)partialTick, this.xo, this.x) - vec3.x());
        float g = (float)(Mth.lerp((double)partialTick, this.yo, this.y) - vec3.y());
        float h = (float)(Mth.lerp((double)partialTick, this.zo, this.z) - vec3.z());

        Quaternionf cameraRot = camera.rotation();

        VERTICES[0].set(-1.0F, -1.0F, 0.0F);
        VERTICES[1].set(-1.0F, 1.0F, 0.0F);
        VERTICES[2].set(1.0F, 1.0F, 0.0F);
        VERTICES[3].set(1.0F, -1.0F, 0.0F);
        float f4 = this.getQuadSize(partialTick);

        for (int i = 0; i < 4; ++i) {
            Vector3f v = VERTICES[i];
            v.rotate(cameraRot);
            v.mul(f4);
            v.add(f, g, h);
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = this.getLightColor(partialTick);
        vertexConsumer.vertex(VERTICES[0].x(), VERTICES[0].y(), VERTICES[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(VERTICES[1].x(), VERTICES[1].y(), VERTICES[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(VERTICES[2].x(), VERTICES[2].y(), VERTICES[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(VERTICES[3].x(), VERTICES[3].y(), VERTICES[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        private static SpriteSet cachedSpriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
            cachedSpriteSet = spriteSet;
            WeatherEngine.invalidate();
        }

        /** Returns the cached SpriteSet for direct particle instantiation (bypasses registry). */
        public static SpriteSet getCachedSpriteSet() {
            return cachedSpriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new RefinedSnowParticle(level, x, y, z, this.spriteSet);
        }
    }
}
