package com.pegs.volumes.particle;

import com.pegs.volumes.PegsVolumesClient;
import com.pegs.volumes.compat.IrisCompat;
import com.pegs.volumes.config.VolumesConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class VolumetricMistParticle extends SingleQuadParticle {
    private final float baseAlpha;
    private final float startScale;
    private final float endScale;
    private final float rotSpeed;
    private final float turbulenceAmp;
    private final float turbulenceFreq;
    private final float phaseOffset;
    private final float verticalDrag;
    private final boolean emissiveGlow;

    public VolumetricMistParticle(
            ClientLevel level,
            double x, double y, double z,
            double xd, double yd, double zd,
            TextureAtlasSprite sprite,
            VolumeSpec spec
    ) {
        super(level, x, y, z, sprite);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.lifetime = spec.lifetimeTicks;
        this.gravity = spec.gravity;
        this.hasPhysics = spec.collideWithBlocks;
        this.friction = spec.horizontalDrag;
        this.verticalDrag = spec.verticalDrag;

        this.rCol = spec.red;
        this.gCol = spec.green;
        this.bCol = spec.blue;
        this.baseAlpha = spec.alpha;
        this.alpha = 0.0f;

        this.startScale = spec.startScale;
        this.endScale = spec.endScale;
        this.quadSize = spec.startScale;

        this.roll = spec.initialRoll;
        this.oRoll = this.roll;
        this.rotSpeed = spec.rollSpeed;

        this.turbulenceAmp = spec.turbulenceAmp;
        this.turbulenceFreq = spec.turbulenceFreq;
        this.phaseOffset = spec.phaseOffset;
        this.emissiveGlow = spec.emissiveGlow;

        PegsVolumesClient.getGovernor().onVolumeSpawned();
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float progress = (float) this.age / (float) this.lifetime;

        float t = (this.age * this.turbulenceFreq) + this.phaseOffset;
        this.xd += Mth.sin(t) * this.turbulenceAmp;
        this.zd += Mth.cos(t * 0.87f + 1.1f) * this.turbulenceAmp;
        this.yd = (this.yd - 0.04 * this.gravity) * this.verticalDrag;

        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.zd *= this.friction;

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        float easeOut = 1.0f - (1.0f - progress) * (1.0f - progress);
        this.quadSize = Mth.lerp(easeOut, this.startScale, this.endScale);

        float fadeIn = Mth.clamp(progress / 0.22f, 0.0f, 1.0f);
        fadeIn = fadeIn * fadeIn * (3.0f - 2.0f * fadeIn);
        float fadeOut = Mth.clamp((1.0f - progress) / 0.38f, 0.0f, 1.0f);
        fadeOut = fadeOut * fadeOut * (3.0f - 2.0f * fadeOut);

        float computedAlpha = this.baseAlpha * fadeIn * fadeOut;

        VolumesConfig cfg = PegsVolumesClient.getConfig();
        computedAlpha *= cfg.globalOpacityMultiplier;
        if (cfg.autoAdaptToShaders && IrisCompat.areShadersActive()) {
            computedAlpha *= cfg.shaderOpacityScale;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
        if (camera != null) {
            Vec3 camPos = camera.position();
            double dx = this.x - camPos.x;
            double dy = this.y - camPos.y;
            double dz = this.z - camPos.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            float fadeStart = cfg.nearCameraFadeStart;
            float fadeEnd = cfg.nearCameraFadeEnd;

            if (distSq <= fadeEnd * fadeEnd) {
                computedAlpha = 0.0f;
            } else if (distSq < fadeStart * fadeStart) {
                float dist = (float) Math.sqrt(distSq);
                float nearFactor = (dist - fadeEnd) / (fadeStart - fadeEnd);
                computedAlpha *= nearFactor * nearFactor;
            }
        }

        this.alpha = Mth.clamp(computedAlpha, 0.0f, 0.85f);
    }

    @Override
    protected int getLightCoords(float partialTick) {
        if (this.emissiveGlow) {
            int baseLight = super.getLightCoords(partialTick);
            int blockLight = Math.max((baseLight >> 4) & 0xF, 12);
            int skyLight = (baseLight >> 20) & 0xF;
            return (skyLight << 20) | (blockLight << 4);
        }
        return super.getLightCoords(partialTick);
    }

    @Override
    public void remove() {
        if (!this.removed) {
            PegsVolumesClient.getGovernor().onVolumeRemoved();
        }
        super.remove();
    }

    public record VolumeSpec(
            int lifetimeTicks,
            float gravity,
            boolean collideWithBlocks,
            float horizontalDrag,
            float verticalDrag,
            float red,
            float green,
            float blue,
            float alpha,
            float startScale,
            float endScale,
            float initialRoll,
            float rollSpeed,
            float turbulenceAmp,
            float turbulenceFreq,
            float phaseOffset,
            boolean emissiveGlow
    ) {}
}
