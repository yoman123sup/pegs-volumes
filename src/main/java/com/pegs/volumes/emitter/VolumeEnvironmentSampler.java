package com.pegs.volumes.emitter;

import com.pegs.volumes.config.VolumesConfig;
import com.pegs.volumes.registry.VolumeParticleTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public final class VolumeEnvironmentSampler {
    private final VolumesConfig config;
    private final PerformanceGovernor governor;
    private final SituationContext ctx = new SituationContext();

    private int rainCooldownTicks = 0;

    public VolumeEnvironmentSampler(VolumesConfig config, PerformanceGovernor governor) {
        this.config = config;
        this.governor = governor;
    }

    public void tick(Minecraft client) {
        ClientLevel level = client.level;
        Camera camera = client.gameRenderer.mainCamera();
        if (level == null || camera == null) {
            return;
        }

        if (level.isRaining()) {
            rainCooldownTicks = 1200;
        } else if (rainCooldownTicks > 0) {
            rainCooldownTicks--;
        }

        if (!governor.canSpawnVolume()) {
            return;
        }

        RandomSource random = level.getRandom();
        Vec3 camPos = camera.position();
        Vec3 lookVec = new Vec3(camera.forwardVector());

        long dayTime = level.getDayTime() % 24000L;
        float sunAngle = (dayTime / 24000.0f) * Mth.TWO_PI;
        ctx.dayFactor = Mth.clamp(Mth.sin(sunAngle) * 0.5f + 0.5f, 0.0f, 1.0f);
        ctx.dawnDuskFactor = 1.0f - Math.abs(Mth.sin(sunAngle));
        ctx.isRaining = level.isRaining();
        ctx.isThundering = level.isThundering();

        int probes = governor.getProbesForCurrentTick();
        float maxRadius = config.maxSpawnRadius;

        for (int i = 0; i < probes; i++) {
            if (!governor.canSpawnVolume()) {
                break;
            }

            double dist = 3.5 + Math.pow(random.nextDouble(), 0.72) * (maxRadius - 3.5);
            double yawJitter = (random.nextDouble() - 0.5) * 2.35;
            double pitchJitter = (random.nextDouble() - 0.5) * 1.35;

            double cosYaw = Math.cos(yawJitter);
            double sinYaw = Math.sin(yawJitter);
            double dirX = lookVec.x * cosYaw - lookVec.z * sinYaw;
            double dirZ = lookVec.x * sinYaw + lookVec.z * cosYaw;
            double dirY = Mth.clamp(lookVec.y + pitchJitter, -0.85, 0.85);

            int bx = Mth.floor(camPos.x + dirX * dist);
            int by = Mth.floor(camPos.y + dirY * dist);
            int bz = Mth.floor(camPos.z + dirZ * dist);

            if (by < level.getMinY() + 1 || by >= level.getMaxY() - 2) {
                continue;
            }

            ctx.pos.set(bx, by, bz);
            if (!level.hasChunkAt(ctx.pos)) {
                continue;
            }

            evaluateSample(level, random);
        }
    }

    private void evaluateSample(ClientLevel level, RandomSource random) {
        ctx.state = level.getBlockState(ctx.pos);
        ctx.fluidState = ctx.state.getFluidState();

        ctx.neighborPos.set(ctx.pos.getX(), ctx.pos.getY() + 1, ctx.pos.getZ());
        ctx.aboveState = level.getBlockState(ctx.neighborPos);

        ctx.neighborPos.set(ctx.pos.getX(), ctx.pos.getY() - 1, ctx.pos.getZ());
        ctx.belowState = level.getBlockState(ctx.neighborPos);

        double x = ctx.pos.getX() + random.nextDouble();
        double y = ctx.pos.getY() + random.nextDouble();
        double z = ctx.pos.getZ() + random.nextDouble();

        if (config.enableCampfiresAndHearths) {
            if (ctx.state.is(Blocks.CAMPFIRE) && ctx.state.getOptionalValue(CampfireBlock.LIT).orElse(false)) {
                spawn(level, VolumeParticleTypes.CAMPFIRE_VOLUME, x, ctx.pos.getY() + 0.75, z,
                        (random.nextDouble() - 0.5) * 0.015, 0.045 + random.nextDouble() * 0.025, (random.nextDouble() - 0.5) * 0.015);
                return;
            }
            if (ctx.state.is(Blocks.SOUL_CAMPFIRE) && ctx.state.getOptionalValue(CampfireBlock.LIT).orElse(false)) {
                spawn(level, VolumeParticleTypes.SOUL_CAMPFIRE_VOLUME, x, ctx.pos.getY() + 0.75, z,
                        (random.nextDouble() - 0.5) * 0.015, 0.042 + random.nextDouble() * 0.025, (random.nextDouble() - 0.5) * 0.015);
                return;
            }
            if ((ctx.state.is(Blocks.BLAST_FURNACE) || ctx.state.is(Blocks.SMOKER))
                    && ctx.state.getOptionalValue(BlockStateProperties.LIT).orElse(false)
                    && ctx.aboveState.isAir() && random.nextFloat() < 0.35f) {
                spawn(level, VolumeParticleTypes.CAMPFIRE_VOLUME, x, ctx.pos.getY() + 1.05, z,
                        0.0, 0.025, 0.0);
                return;
            }
        }

        if (config.enableLavaAndMagma) {
            if (ctx.fluidState.is(FluidTags.LAVA) && ctx.aboveState.isAir()) {
                if (random.nextFloat() < 0.55f) {
                    spawn(level, VolumeParticleTypes.LAVA_HEAT_VOLUME, x, ctx.pos.getY() + 1.05, z,
                            (random.nextDouble() - 0.5) * 0.02, 0.038 + random.nextDouble() * 0.028, (random.nextDouble() - 0.5) * 0.02);
                }
                return;
            }
            if (ctx.state.is(Blocks.MAGMA_BLOCK) && ctx.aboveState.isAir() && random.nextFloat() < 0.30f) {
                spawn(level, VolumeParticleTypes.LAVA_HEAT_VOLUME, x, ctx.pos.getY() + 1.05, z,
                        0.0, 0.028, 0.0);
                return;
            }
        }

        if (config.enableWaterfallsAndRivers && ctx.fluidState.is(FluidTags.WATER)) {
            boolean isFallingWater = ctx.fluidState.getAmount() < 8
                    || ctx.aboveState.getFluidState().is(FluidTags.WATER);
            if (isFallingWater && hasAdjacentAir(level)) {
                if (random.nextFloat() < 0.45f) {
                    spawn(level, VolumeParticleTypes.WATERFALL_MIST_VOLUME, x, y, z,
                            (random.nextDouble() - 0.5) * 0.045, 0.02 + random.nextDouble() * 0.02, (random.nextDouble() - 0.5) * 0.045);
                }
                return;
            }
        }

        if (!ctx.state.isAir()) {
            return;
        }

        ctx.biome = level.getBiome(ctx.pos);
        ctx.skyLight = level.getBrightness(LightLayer.SKY, ctx.pos);
        ctx.blockLight = level.getBrightness(LightLayer.BLOCK, ctx.pos);

        if (config.enableNetherAndEndAether) {
            ResourceKey<Level> dim = level.dimension();
            if (dim == Level.NETHER) {
                if (random.nextFloat() < 0.24f) {
                    spawnNetherVolume(level, x, y, z);
                }
                return;
            }
            if (dim == Level.END) {
                if (random.nextFloat() < 0.20f) {
                    spawn(level, VolumeParticleTypes.DIMENSION_AETHER_VOLUME, x, y, z, 0.52, 0.34, 0.78);
                }
                return;
            }
        }

        if (config.enableDeepDarkSculk) {
            if (ctx.biome.is(Biomes.DEEP_DARK)
                    || ctx.belowState.is(Blocks.SCULK)
                    || ctx.belowState.is(Blocks.SCULK_CATALYST)
                    || ctx.belowState.is(Blocks.SCULK_SHRIEKER)) {
                if (random.nextFloat() < 0.28f) {
                    spawn(level, VolumeParticleTypes.SCULK_ABYSS_VOLUME, x, ctx.pos.getY() + 0.35, z,
                            (random.nextDouble() - 0.5) * 0.012, 0.004, (random.nextDouble() - 0.5) * 0.012);
                }
                return;
            }
        }

        if (config.enableCavernDampness && ctx.skyLight <= 3 && ctx.pos.getY() < 56) {
            if (isCavernFloor(ctx.belowState) && random.nextFloat() < 0.22f) {
                spawn(level, VolumeParticleTypes.CAVERN_DAMP_VOLUME, x, ctx.pos.getY() + 0.45, z,
                        (random.nextDouble() - 0.5) * 0.014, 0.003, (random.nextDouble() - 0.5) * 0.014);
            }
            return;
        }

        if (config.enableRainAndStormVapor && ctx.skyLight >= 11 && !ctx.belowState.isAir()) {
            if (ctx.isThundering && random.nextFloat() < 0.26f) {
                spawn(level, VolumeParticleTypes.STORM_VAPOR_VOLUME, x, ctx.pos.getY() + 0.5, z,
                        0.045, 0.012, 0.025);
                return;
            }
            if ((ctx.isRaining || rainCooldownTicks > 0) && random.nextFloat() < 0.16f) {
                spawn(level, VolumeParticleTypes.STORM_VAPOR_VOLUME, x, ctx.pos.getY() + 0.35, z,
                        0.02, 0.016, 0.015);
                return;
            }
        }

        if (ctx.belowState.isAir()) {
            if (config.enableAlpineAndSnowClouds && ctx.pos.getY() > 115 && ctx.biome.is(BiomeTags.IS_MOUNTAIN)) {
                if (random.nextFloat() < 0.14f) {
                    spawn(level, VolumeParticleTypes.ALPINE_CLOUD_VOLUME, x, y, z,
                            0.032, 0.002, 0.014);
                }
            }
            return;
        }

        if (config.enableSwampMiasma && (ctx.biome.is(Biomes.SWAMP) || ctx.biome.is(Biomes.MANGROVE_SWAMP))) {
            if (random.nextFloat() < 0.34f) {
                spawn(level, VolumeParticleTypes.SWAMP_MIASMA_VOLUME, x, ctx.pos.getY() + 0.35, z,
                        (random.nextDouble() - 0.5) * 0.012, 0.003, (random.nextDouble() - 0.5) * 0.012);
            }
            return;
        }

        if (config.enableCoastalAndOceanSpray
                && (ctx.biome.is(BiomeTags.IS_BEACH) || ctx.biome.is(BiomeTags.IS_OCEAN) || ctx.biome.is(Biomes.STONY_SHORE))) {
            boolean nearWater = ctx.belowState.getFluidState().is(FluidTags.WATER) || hasAdjacentWater(level);
            if (nearWater && random.nextFloat() < 0.30f) {
                spawn(level, VolumeParticleTypes.COASTAL_SPRAY_VOLUME, x, ctx.pos.getY() + 0.45, z,
                        0.026 + (random.nextDouble() - 0.5) * 0.015, 0.008, 0.018 + (random.nextDouble() - 0.5) * 0.015);
            }
            return;
        }

        if (config.enableDesertAndBadlandsDust
                && (ctx.biome.is(Biomes.DESERT) || ctx.biome.is(BiomeTags.IS_BADLANDS) || ctx.belowState.is(BlockTags.SAND))) {
            if (random.nextFloat() < 0.24f) {
                spawn(level, VolumeParticleTypes.DESERT_DUST_VOLUME, x, ctx.pos.getY() + 0.40, z,
                        0.055 + random.nextDouble() * 0.03, 0.005, 0.022 + (random.nextDouble() - 0.5) * 0.02);
            }
            return;
        }

        if (config.enableAlpineAndSnowClouds
                && (ctx.biome.is(BiomeTags.IS_MOUNTAIN) || ctx.belowState.is(Blocks.SNOW_BLOCK)
                || ctx.belowState.is(Blocks.POWDER_SNOW) || ctx.belowState.is(Blocks.ICE))) {
            if (random.nextFloat() < 0.25f) {
                spawn(level, VolumeParticleTypes.ALPINE_CLOUD_VOLUME, x, ctx.pos.getY() + 0.65, z,
                        0.038, 0.003, 0.018);
            }
            return;
        }

        if (config.enableJungleHumidity && (ctx.biome.is(BiomeTags.IS_JUNGLE) || ctx.biome.is(Biomes.LUSH_CAVES))) {
            if (random.nextFloat() < 0.28f) {
                spawn(level, VolumeParticleTypes.JUNGLE_HUMIDITY_VOLUME, x, ctx.pos.getY() + 0.65, z,
                        (random.nextDouble() - 0.5) * 0.015, 0.006, (random.nextDouble() - 0.5) * 0.015);
            }
            return;
        }

        if (config.enableCherryAndMeadowHaze
                && (ctx.biome.is(Biomes.CHERRY_GROVE) || ctx.biome.is(Biomes.MEADOW) || ctx.biome.is(Biomes.FLOWER_FOREST))) {
            if (random.nextFloat() < 0.22f) {
                spawn(level, VolumeParticleTypes.BLOSSOM_HAZE_VOLUME, x, ctx.pos.getY() + 0.55, z,
                        (random.nextDouble() - 0.5) * 0.016, 0.005, (random.nextDouble() - 0.5) * 0.016);
            }
            return;
        }

        if (config.enableForestMist
                && (ctx.biome.is(BiomeTags.IS_FOREST) || ctx.biome.is(BiomeTags.IS_TAIGA) || ctx.biome.is(Biomes.PALE_GARDEN))) {
            float forestChance = 0.14f + 0.18f * ctx.dawnDuskFactor;
            if (ctx.biome.is(Biomes.DARK_FOREST) || ctx.biome.is(Biomes.PALE_GARDEN)) {
                forestChance += 0.12f;
            }
            if (random.nextFloat() < forestChance) {
                spawn(level, VolumeParticleTypes.FOREST_MIST_VOLUME, x, ctx.pos.getY() + 0.50, z,
                        (random.nextDouble() - 0.5) * 0.014, 0.004, (random.nextDouble() - 0.5) * 0.014);
            }
            return;
        }

        if (config.enableWaterfallsAndRivers && ctx.biome.is(BiomeTags.IS_RIVER) && ctx.belowState.getFluidState().is(FluidTags.WATER)) {
            if (random.nextFloat() < 0.20f) {
                spawn(level, VolumeParticleTypes.WATERFALL_MIST_VOLUME, x, ctx.pos.getY() + 0.25, z,
                        (random.nextDouble() - 0.5) * 0.015, 0.006, (random.nextDouble() - 0.5) * 0.015);
            }
        }
    }

    private void spawnNetherVolume(ClientLevel level, double x, double y, double z) {
        Holder<Biome> biome = ctx.biome;
        if (biome.is(Biomes.SOUL_SAND_VALLEY)) {
            spawn(level, VolumeParticleTypes.DIMENSION_AETHER_VOLUME, x, y, z, 0.24, 0.78, 0.84);
        } else if (biome.is(Biomes.WARPED_FOREST)) {
            spawn(level, VolumeParticleTypes.DIMENSION_AETHER_VOLUME, x, y, z, 0.18, 0.72, 0.65);
        } else if (biome.is(Biomes.CRIMSON_FOREST)) {
            spawn(level, VolumeParticleTypes.DIMENSION_AETHER_VOLUME, x, y, z, 0.78, 0.20, 0.26);
        } else if (biome.is(Biomes.BASALT_DELTAS)) {
            spawn(level, VolumeParticleTypes.DIMENSION_AETHER_VOLUME, x, y, z, 0.52, 0.50, 0.54);
        } else {
            spawn(level, VolumeParticleTypes.DIMENSION_AETHER_VOLUME, x, y, z, 0.74, 0.34, 0.22);
        }
    }

    private boolean hasAdjacentAir(ClientLevel level) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            ctx.neighborPos.setWithOffset(ctx.pos, dir);
            if (level.getBlockState(ctx.neighborPos).isAir()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAdjacentWater(ClientLevel level) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            ctx.neighborPos.setWithOffset(ctx.pos, dir);
            if (level.getFluidState(ctx.neighborPos).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCavernFloor(net.minecraft.world.level.block.state.BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(Blocks.DRIPSTONE_BLOCK)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(Blocks.CLAY)
                || state.getFluidState().is(FluidTags.WATER);
    }

    private static void spawn(
            ClientLevel level,
            SimpleParticleType type,
            double x, double y, double z,
            double xd, double yd, double zd
    ) {
        level.addParticle(type, false, false, x, y, z, xd, yd, zd);
    }
}
