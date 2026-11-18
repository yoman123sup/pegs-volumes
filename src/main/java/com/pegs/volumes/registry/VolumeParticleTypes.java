package com.pegs.volumes.registry;

import com.pegs.volumes.PegsVolumesClient;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class VolumeParticleTypes {
    public static final SimpleParticleType CAMPFIRE_VOLUME = register("campfire_volume");
    public static final SimpleParticleType SOUL_CAMPFIRE_VOLUME = register("soul_campfire_volume");
    public static final SimpleParticleType LAVA_HEAT_VOLUME = register("lava_heat_volume");
    public static final SimpleParticleType FOREST_MIST_VOLUME = register("forest_mist_volume");
    public static final SimpleParticleType JUNGLE_HUMIDITY_VOLUME = register("jungle_humidity_volume");
    public static final SimpleParticleType DESERT_DUST_VOLUME = register("desert_dust_volume");
    public static final SimpleParticleType COASTAL_SPRAY_VOLUME = register("coastal_spray_volume");
    public static final SimpleParticleType SWAMP_MIASMA_VOLUME = register("swamp_miasma_volume");
    public static final SimpleParticleType ALPINE_CLOUD_VOLUME = register("alpine_cloud_volume");
    public static final SimpleParticleType CAVERN_DAMP_VOLUME = register("cavern_damp_volume");
    public static final SimpleParticleType SCULK_ABYSS_VOLUME = register("sculk_abyss_volume");
    public static final SimpleParticleType WATERFALL_MIST_VOLUME = register("waterfall_mist_volume");
    public static final SimpleParticleType STORM_VAPOR_VOLUME = register("storm_vapor_volume");
    public static final SimpleParticleType BLOSSOM_HAZE_VOLUME = register("blossom_haze_volume");
    public static final SimpleParticleType DIMENSION_AETHER_VOLUME = register("dimension_aether_volume");

    private VolumeParticleTypes() {}

    private static SimpleParticleType register(String path) {
        SimpleParticleType type = FabricParticleTypes.simple();
        return Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                Identifier.fromNamespaceAndPath(PegsVolumesClient.MOD_ID, path),
                type
        );
    }

    public static void registerAll() {
    }
}
