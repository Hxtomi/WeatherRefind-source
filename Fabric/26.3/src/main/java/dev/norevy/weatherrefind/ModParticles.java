package dev.norevy.weatherrefind;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModParticles {

    public static final SimpleParticleType RAIN = FabricParticleTypes.simple();
    public static final SimpleParticleType SNOW = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(WeatherRefind.MOD_ID, "rain"), RAIN);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(WeatherRefind.MOD_ID, "snow"), SNOW);
    }
}
