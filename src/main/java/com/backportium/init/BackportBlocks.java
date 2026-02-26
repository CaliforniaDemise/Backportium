package com.backportium.init;

import com.backportium.Tags;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class BackportBlocks {

    public static Block BLUE_ICE;

    public static void init() {
//        BLUE_ICE = get("blue_ice");
    }

    @NotNull
    private static Block get(ResourceLocation name) {
        Block block = ForgeRegistries.BLOCKS.getValue(name);
        Preconditions.checkNotNull(block);
        return block;
    }

    @NotNull
    private static Block get(String name) {
        return get(new ResourceLocation(Tags.MOD_ID, name));
    }

    private BackportBlocks() {}
}
