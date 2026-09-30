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

public class RefinedRainParticle extends TextureSheetParticle {

    private static int aliveCount = 0;
    private final int weatherGeneration=WeatherEngine.generation();
    private static final Quaternionf REUSABLE_QUAT = new Quaternionf();
    private static final Vector3f[] VERTICES = {
            new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()
    };
    private final SpriteSet spriteSet;
    private final float targetAlpha;
    private final int fadeInTicks;

    public RefinedRainParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, int fadeInTicks) {
        super(level, x, y, z);
        ++aliveCount;
        this.spriteSet = spriteSet;
        this.fadeInTicks = Math.max(0, fadeInTicks);
        int minLife = WeatherRefindConfig.current.rainMinLifetime();
        int maxLife = WeatherRefindConfig.current.rainMaxLifetime();
        this.lifetime = minLife + this.random.nextInt(Math.max(1, maxLife - minLife + 1));
        this.gravity = (float) WeatherRefindConfig.current.rainGravity();
        this.hasPhysics = true;
        double sizeMin = WeatherRefindConfig.current.rainQuadSizeMin();
        double sizeMax = WeatherRefindConfig.current.rainQuadSizeMax();
        this.quadSize = (float)(sizeMin + this.random.nextFloat() * Math.max(0, sizeMax - sizeMin));

        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;

        this.targetAlpha = (float) WeatherRefindConfig.current.rainAlpha();
        this.alpha = this.fadeInTicks > 0 ? 0.0F : this.targetAlpha;

        this.pickSprite(spriteSet);
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
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp((double)partialTick, this.xo, this.x) - vec3.x());
        float g = (float)(Mth.lerp((double)partialTick, this.yo, this.y) - vec3.y());
        float h = (float)(Mth.lerp((double)partialTick, this.zo, this.z) - vec3.z());

        REUSABLE_QUAT.identity().rotationY(-camera.getYRot() * Mth.DEG_TO_RAD);

        VERTICES[0].set(-1.0F, -1.0F, 0.0F);
        VERTICES[1].set(-1.0F, 1.0F, 0.0F);
        VERTICES[2].set(1.0F, 1.0F, 0.0F);
        VERTICES[3].set(1.0F, -1.0F, 0.0F);
        float f4 = this.getQuadSize(partialTick);

        for (int i = 0; i < 4; ++i) {
            Vector3f v = VERTICES[i];
            v.rotate(REUSABLE_QUAT);
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
            return new RefinedRainParticle(level, x, y, z, this.spriteSet, Mth.ceil(ySpeed));
        }
    }
}
