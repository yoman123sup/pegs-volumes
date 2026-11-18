package com.pegs.volumes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pegs.volumes.PegsVolumesClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VolumesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("pegsvolumes.json");

    public boolean enabled = true;

    public int maxActiveVolumes = 650;
    public int baseProbesPerTick = 18;
    public float maxSpawnRadius = 42.0f;
    public float nearCameraFadeStart = 4.5f;
    public float nearCameraFadeEnd = 1.75f;
    public float globalDensityMultiplier = 1.0f;
    public float globalOpacityMultiplier = 1.0f;
    public int targetMinimumFps = 55;
    public boolean autoAdaptToShaders = true;
    public float shaderOpacityScale = 0.62f;

    public boolean enableCampfiresAndHearths = true;
    public boolean enableLavaAndMagma = true;
    public boolean enableForestMist = true;
    public boolean enableJungleHumidity = true;
    public boolean enableDesertAndBadlandsDust = true;
    public boolean enableCoastalAndOceanSpray = true;
    public boolean enableSwampMiasma = true;
    public boolean enableAlpineAndSnowClouds = true;
    public boolean enableCavernDampness = true;
    public boolean enableDeepDarkSculk = true;
    public boolean enableWaterfallsAndRivers = true;
    public boolean enableRainAndStormVapor = true;
    public boolean enableCherryAndMeadowHaze = true;
    public boolean enableNetherAndEndAether = true;

    public static VolumesConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                VolumesConfig loaded = GSON.fromJson(reader, VolumesConfig.class);
                if (loaded != null) {
                    loaded.clamp();
                    loaded.save();
                    return loaded;
                }
            } catch (Exception e) {
                PegsVolumesClient.LOGGER.warn("Failed to read pegsvolumes.json, restoring defaults", e);
            }
        }
        VolumesConfig defaults = new VolumesConfig();
        defaults.save();
        return defaults;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            PegsVolumesClient.LOGGER.error("Failed to write pegsvolumes.json", e);
        }
    }

    private void clamp() {
        maxActiveVolumes = Math.clamp(maxActiveVolumes, 64, 3000);
        baseProbesPerTick = Math.clamp(baseProbesPerTick, 4, 64);
        maxSpawnRadius = Math.clamp(maxSpawnRadius, 16.0f, 80.0f);
        nearCameraFadeStart = Math.clamp(nearCameraFadeStart, 1.5f, 12.0f);
        nearCameraFadeEnd = Math.clamp(nearCameraFadeEnd, 0.5f, nearCameraFadeStart - 0.25f);
        globalDensityMultiplier = Math.clamp(globalDensityMultiplier, 0.1f, 3.0f);
        globalOpacityMultiplier = Math.clamp(globalOpacityMultiplier, 0.1f, 2.5f);
        targetMinimumFps = Math.clamp(targetMinimumFps, 20, 240);
        shaderOpacityScale = Math.clamp(shaderOpacityScale, 0.2f, 1.2f);
    }
}
