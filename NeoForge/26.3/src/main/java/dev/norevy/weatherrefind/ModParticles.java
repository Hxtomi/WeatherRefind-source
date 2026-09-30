package dev.norevy.weatherrefind;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, WeatherRefind.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RAIN =
            PARTICLE_TYPES.register("rain", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SNOW =
            PARTICLE_TYPES.register("snow", () -> new SimpleParticleType(false));
}
