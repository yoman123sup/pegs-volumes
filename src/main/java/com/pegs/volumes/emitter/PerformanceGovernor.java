package com.pegs.volumes.emitter;

import com.pegs.volumes.config.VolumesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import java.util.concurrent.atomic.AtomicInteger;

public final class PerformanceGovernor {
    private final VolumesConfig config;
    private final AtomicInteger activeVolumeCount = new AtomicInteger(0);
    private float smoothedFps = 60.0f;
    private float budgetScale = 1.0f;

    public PerformanceGovernor(VolumesConfig config) {
        this.config = config;
    }

    public void update(Minecraft client) {
        int currentFps = Math.max(client.getFps(), 1);
        this.smoothedFps = Mth.lerp(0.08f, this.smoothedFps, (float) currentFps);

        float target = (float) config.targetMinimumFps;
        if (smoothedFps < target) {
            float ratio = Mth.clamp(smoothedFps / target, 0.25f, 1.0f);
            this.budgetScale = Mth.lerp(0.10f, this.budgetScale, ratio * ratio);
        } else {
            this.budgetScale = Mth.lerp(0.05f, this.budgetScale, 1.0f);
        }
    }

    public boolean canSpawnVolume() {
        int effectiveMax = Math.max(32, Math.round(config.maxActiveVolumes * budgetScale));
        return activeVolumeCount.get() < effectiveMax;
    }

    public int getProbesForCurrentTick() {
        float scaled = config.baseProbesPerTick * config.globalDensityMultiplier * budgetScale;
        return Math.max(2, Math.round(scaled));
    }

    public void onVolumeSpawned() {
        activeVolumeCount.incrementAndGet();
    }

    public void onVolumeRemoved() {
        activeVolumeCount.updateAndGet(v -> Math.max(0, v - 1));
    }

    public int getActiveVolumeCount() {
        return activeVolumeCount.get();
    }

    public float getBudgetScale() {
        return budgetScale;
    }
}
