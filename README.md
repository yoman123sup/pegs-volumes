# Peg's Volumes (`pegsvolumes`) — Minecraft 26.3 (Fabric)

Performance-friendly, shader-compatible volumetric mist, smoke, heat-haze, sea-spray, and atmospheric volumes for every biome and environment.

## Building `pegs-volumes-1.0.0.jar`

All particle sprites (`mist_volume.png` and `ember_wisp.png`) are already pre-generated in `src/main/resources/assets/pegsvolumes/textures/particle/`.

1. Ensure **Java 25 (JDK 25)** and **Gradle 9.x** (or a Fabric 26.3 Gradle wrapper) are installed.
2. Run:
   ```bash
   gradle build
   ```
3. Copy `build/libs/pegs-volumes-1.0.0.jar` into your Minecraft `26.3` `mods/` directory alongside **Fabric API `0.166.2+26.3`** (and optionally **Sodium `0.9.2+`** and **Iris `1.11.7+`**).
