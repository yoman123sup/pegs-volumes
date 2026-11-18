package com.pegs.volumes.particle;

import com.pegs.volumes.particle.VolumetricMistParticle.VolumeSpec;
import com.pegs.volumes.registry.VolumeParticleTypes;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public final class VolumeParticlePresets {
    private VolumeParticlePresets() {}

    public static void registerProviders() {
        ParticleProviderRegistry reg = ParticleProviderRegistry.getInstance();

        reg.register(VolumeParticleTypes.CAMPFIRE_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        95 + r.nextInt(55), -0.008f, true, 0.97f, 0.985f,
                        jitter(0.76f, 0.05f, r), jitter(0.72f, 0.05f, r), jitter(0.68f, 0.05f, r),
                        0.24f, 0.55f, 2.35f + r.nextFloat() * 0.7f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.008f,
                        0.0018f, 0.09f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.SOUL_CAMPFIRE_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        105 + r.nextInt(55), -0.007f, true, 0.97f, 0.985f,
                        jitter(0.28f, 0.05f, r), jitter(0.84f, 0.06f, r), jitter(0.88f, 0.06f, r),
                        0.26f, 0.60f, 2.50f + r.nextFloat() * 0.65f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.009f,
                        0.0020f, 0.10f, r.nextFloat() * Mth.TWO_PI, true
                )));

        reg.register(VolumeParticleTypes.LAVA_HEAT_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        85 + r.nextInt(50), -0.014f, false, 0.96f, 0.98f,
                        jitter(0.95f, 0.04f, r), jitter(0.46f, 0.08f, r), jitter(0.18f, 0.05f, r),
                        0.20f, 0.85f, 3.10f + r.nextFloat() * 1.1f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.006f,
                        0.0025f, 0.14f, r.nextFloat() * Mth.TWO_PI, true
                )));

        reg.register(VolumeParticleTypes.FOREST_MIST_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        150 + r.nextInt(90), 0.0005f, false, 0.992f, 0.96f,
                        jitter(0.82f, 0.04f, r), jitter(0.89f, 0.04f, r), jitter(0.86f, 0.04f, r),
                        0.15f, 2.20f, 4.60f + r.nextFloat() * 1.4f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.003f,
                        0.0007f, 0.045f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.JUNGLE_HUMIDITY_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        140 + r.nextInt(80), -0.0008f, false, 0.99f, 0.965f,
                        jitter(0.76f, 0.05f, r), jitter(0.91f, 0.04f, r), jitter(0.74f, 0.05f, r),
                        0.18f, 2.10f, 4.40f + r.nextFloat() * 1.3f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.004f,
                        0.0009f, 0.05f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.DESERT_DUST_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        100 + r.nextInt(60), 0.0012f, true, 0.994f, 0.95f,
                        jitter(0.92f, 0.04f, r), jitter(0.80f, 0.05f, r), jitter(0.58f, 0.06f, r),
                        0.17f, 1.85f, 4.10f + r.nextFloat() * 1.5f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.007f,
                        0.0016f, 0.08f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.COASTAL_SPRAY_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        115 + r.nextInt(65), 0.0015f, false, 0.99f, 0.96f,
                        jitter(0.88f, 0.03f, r), jitter(0.95f, 0.03f, r), jitter(0.98f, 0.02f, r),
                        0.19f, 1.65f, 3.85f + r.nextFloat() * 1.2f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.005f,
                        0.0012f, 0.065f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.SWAMP_MIASMA_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        170 + r.nextInt(90), 0.0003f, false, 0.993f, 0.95f,
                        jitter(0.58f, 0.06f, r), jitter(0.72f, 0.06f, r), jitter(0.54f, 0.05f, r),
                        0.22f, 2.35f, 4.90f + r.nextFloat() * 1.4f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.0025f,
                        0.0006f, 0.038f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.ALPINE_CLOUD_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        130 + r.nextInt(75), 0.0006f, false, 0.995f, 0.97f,
                        jitter(0.93f, 0.03f, r), jitter(0.96f, 0.02f, r), jitter(1.00f, 0.00f, r),
                        0.21f, 2.80f, 6.20f + r.nextFloat() * 2.0f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.003f,
                        0.0011f, 0.055f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.CAVERN_DAMP_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        160 + r.nextInt(80), 0.0008f, true, 0.99f, 0.96f,
                        jitter(0.64f, 0.05f, r), jitter(0.71f, 0.05f, r), jitter(0.80f, 0.05f, r),
                        0.16f, 2.00f, 4.30f + r.nextFloat() * 1.2f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.0025f,
                        0.0006f, 0.04f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.SCULK_ABYSS_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        175 + r.nextInt(85), 0.0005f, true, 0.992f, 0.96f,
                        jitter(0.14f, 0.04f, r), jitter(0.42f, 0.06f, r), jitter(0.54f, 0.06f, r),
                        0.23f, 2.20f, 4.75f + r.nextFloat() * 1.3f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.003f,
                        0.0007f, 0.042f, r.nextFloat() * Mth.TWO_PI, true
                )));

        reg.register(VolumeParticleTypes.WATERFALL_MIST_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        75 + r.nextInt(45), -0.005f, true, 0.965f, 0.975f,
                        jitter(0.86f, 0.04f, r), jitter(0.95f, 0.03f, r), jitter(0.99f, 0.01f, r),
                        0.25f, 0.95f, 3.25f + r.nextFloat() * 1.0f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.010f,
                        0.0022f, 0.11f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.STORM_VAPOR_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        110 + r.nextInt(60), -0.0022f, false, 0.992f, 0.975f,
                        jitter(0.78f, 0.04f, r), jitter(0.84f, 0.04f, r), jitter(0.90f, 0.04f, r),
                        0.17f, 2.15f, 4.80f + r.nextFloat() * 1.5f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.004f,
                        0.0014f, 0.07f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.BLOSSOM_HAZE_VOLUME, sprites -> new PresetProvider(sprites, (xd, yd, zd, r) ->
                new VolumeSpec(
                        145 + r.nextInt(75), -0.0004f, false, 0.991f, 0.965f,
                        jitter(0.97f, 0.03f, r), jitter(0.84f, 0.05f, r), jitter(0.88f, 0.05f, r),
                        0.16f, 1.90f, 4.10f + r.nextFloat() * 1.2f,
                        r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.0035f,
                        0.0008f, 0.048f, r.nextFloat() * Mth.TWO_PI, false
                )));

        reg.register(VolumeParticleTypes.DIMENSION_AETHER_VOLUME, sprites -> (type, level, x, y, z, xd, yd, zd, r) -> {
            float red = Mth.clamp((float) xd, 0.05f, 1.0f);
            float green = Mth.clamp((float) yd, 0.05f, 1.0f);
            float blue = Mth.clamp((float) zd, 0.05f, 1.0f);
            double driftX = (r.nextDouble() - 0.5) * 0.018;
            double driftY = (r.nextDouble() - 0.35) * 0.010;
            double driftZ = (r.nextDouble() - 0.5) * 0.018;

            VolumeSpec spec = new VolumeSpec(
                    155 + r.nextInt(85), -0.001f, false, 0.992f, 0.97f,
                    red, green, blue,
                    0.20f, 2.40f, 5.30f + r.nextFloat() * 1.6f,
                    r.nextFloat() * Mth.TWO_PI, (r.nextFloat() - 0.5f) * 0.003f,
                    0.0009f, 0.05f, r.nextFloat() * Mth.TWO_PI, true
            );
            return new VolumetricMistParticle(level, x, y, z, driftX, driftY, driftZ, sprites.get(r), spec);
        });
    }

    private static float jitter(float base, float spread, RandomSource r) {
        return Mth.clamp(base + (r.nextFloat() * 2.0f - 1.0f) * spread, 0.0f, 1.0f);
    }

    @FunctionalInterface
    private interface SpecFactory {
        VolumeSpec create(double xd, double yd, double zd, RandomSource random);
    }

    private record PresetProvider(SpriteSet sprites, SpecFactory factory) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd,
                RandomSource random
        ) {
            VolumeSpec spec = factory.create(xd, yd, zd, random);
            return new VolumetricMistParticle(level, x, y, z, xd, yd, zd, sprites.get(random), spec);
        }
    }
}
