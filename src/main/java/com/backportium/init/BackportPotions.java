package com.backportium.init;

import com.backportium.Tags;
import com.google.common.base.Preconditions;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class BackportPotions {

    @NotNull
    private static Potion get(ResourceLocation name) {
        Potion potion = ForgeRegistries.POTIONS.getValue(name);
        Preconditions.checkNotNull(potion);
        return potion;
    }

    @NotNull
    private static Potion get(String name) {
        return get(new ResourceLocation(Tags.MOD_ID, name));
    }

    private BackportPotions() {}
}
