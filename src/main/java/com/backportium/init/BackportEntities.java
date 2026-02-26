package com.backportium.init;

import com.backportium.Tags;
import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class BackportEntities {

    @NotNull
    private static EntityEntry get(ResourceLocation name) {
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(name);
        Preconditions.checkNotNull(entry);
        return entry;
    }

    @NotNull
    private static EntityEntry get(String name) {
        return get(new ResourceLocation(Tags.MOD_ID, name));
    }

    private BackportEntities() {}
}
