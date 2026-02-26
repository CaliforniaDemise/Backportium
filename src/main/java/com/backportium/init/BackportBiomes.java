package com.backportium.init;

import com.backportium.Tags;
import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class BackportBiomes {

    @NotNull
    private static Biome get(ResourceLocation name) {
        Biome biome = ForgeRegistries.BIOMES.getValue(name);
        Preconditions.checkNotNull(biome);
        return biome;
    }

    @NotNull
    private static Biome get(String name) {
        return get(new ResourceLocation(Tags.MOD_ID, name));
    }

    private BackportBiomes() {}
}
