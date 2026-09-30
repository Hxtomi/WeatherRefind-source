package dev.norevy.weatherrefind;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, WeatherRefind.MOD_ID);

    public static final RegistryObject<SimpleParticleType> RAIN =
            PARTICLE_TYPES.register("rain", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SNOW =
            PARTICLE_TYPES.register("snow", () -> new SimpleParticleType(false));
}
