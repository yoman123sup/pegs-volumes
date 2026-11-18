package com.pegs.volumes.emitter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class SituationContext {
    public final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    public final BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

    public BlockState state;
    public BlockState aboveState;
    public BlockState belowState;
    public FluidState fluidState;
    public Holder<Biome> biome;

    public int skyLight;
    public int blockLight;
    public boolean isRaining;
    public boolean isThundering;
    public float dayFactor;
    public float dawnDuskFactor;
}
