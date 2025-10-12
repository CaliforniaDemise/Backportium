package surreal.backportium._internal.registry;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import java.util.function.Consumer;

public interface Entities {
    <T extends Entity> EntityEntry register(Class<T> clazz, String name);
    <T extends Entity> EntityEntry register(Class<T> clazz, ResourceLocation name);
    <T extends Entity> EntityEntry register(Class<T> clazz, String name, int updateFrequency);
    <T extends Entity> EntityEntry register(Class<T> clazz, ResourceLocation location, int updateFrequency);
    <T extends Entity> EntityEntry register(Class<T> clazz, String name, int updateFrequency, Consumer<EntityEntryBuilder<T>> consumer);
    <T extends Entity> EntityEntry register(Class<T> clazz, ResourceLocation location, int updateFrequency, Consumer<EntityEntryBuilder<T>> consumer);
}
