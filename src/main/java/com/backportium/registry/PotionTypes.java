package com.backportium.registry;

import com.backportium.Tags;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public interface PotionTypes {

    void register(ResourceLocation name, Consumer<PotionTypeBuilder> consumer);

    default void register(String name, Consumer<PotionTypeBuilder> consumer) {
        register(new ResourceLocation(Tags.MOD_ID, name), consumer);
    }
}
