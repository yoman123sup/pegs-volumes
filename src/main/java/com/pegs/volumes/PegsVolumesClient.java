package com.pegs.volumes;

import com.pegs.volumes.compat.IrisCompat;
import com.pegs.volumes.config.VolumesConfig;
import com.pegs.volumes.emitter.PerformanceGovernor;
import com.pegs.volumes.emitter.VolumeEnvironmentSampler;
import com.pegs.volumes.particle.VolumeParticlePresets;
import com.pegs.volumes.registry.VolumeParticleTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PegsVolumesClient implements ClientModInitializer {
    public static final String MOD_ID = "pegsvolumes";
    public static final Logger LOGGER = LoggerFactory.getLogger("Peg's Volumes");

    private static VolumesConfig config;
    private static PerformanceGovernor governor;
    private static VolumeEnvironmentSampler sampler;

    @Override
    public void onInitializeClient() {
        config = VolumesConfig.load();
        IrisCompat.init();
        VolumeParticleTypes.registerAll();
        VolumeParticlePresets.registerProviders();

        governor = new PerformanceGovernor(config);
        sampler = new VolumeEnvironmentSampler(config, governor);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null || client.isPaused()) {
                return;
            }
            if (!config.enabled) {
                return;
            }
            governor.update(client);
            sampler.tick(client);
        });

        LOGGER.info("Peg's Volumes initialized for Minecraft 26.3 (Iris detected: {})", IrisCompat.isIrisLoaded());
    }

    public static VolumesConfig getConfig() {
        return config;
    }

    public static PerformanceGovernor getGovernor() {
        return governor;
    }
}
