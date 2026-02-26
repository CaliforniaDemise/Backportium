package com.backportium.init;

import com.backportium.Tags;
import com.google.common.base.Preconditions;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class BackportPotionTypes {

    @NotNull
    private static PotionType get(ResourceLocation name) {
        PotionType type = ForgeRegistries.POTION_TYPES.getValue(name);
        Preconditions.checkNotNull(type);
        return type;
    }

    @NotNull
    private static PotionType get(String name) {
        return get(new ResourceLocation(Tags.MOD_ID, name));
    }

    private BackportPotionTypes() {}
}
