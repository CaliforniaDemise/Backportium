package surreal.backportium._internal.registry;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium._internal.entity.RenderProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class RegistryEntity extends Registry<EntityEntry> implements Entities {

    private int count = 0;

    protected RegistryEntity(RegistryManager manager) {
        super(manager);
    }

    @Override
    public <T extends Entity> EntityEntry register(Class<T> clazz, String name) {
        return register(clazz, name, 3);
    }

    @Override
    public <T extends Entity> EntityEntry register(Class<T> clazz, ResourceLocation name) {
        return register(clazz, name, 3);
    }

    @Override
    public <T extends Entity> EntityEntry register(Class<T> clazz, String name, int updateFrequency) {
        return register(clazz, name, updateFrequency, b -> {});
    }

    @Override
    public <T extends Entity> EntityEntry register(Class<T> clazz, ResourceLocation location, int updateFrequency) {
        return register(clazz, location, updateFrequency, b -> {});
    }

    @Override
    public <T extends Entity> EntityEntry register(Class<T> clazz, String name, int updateFrequency, Consumer<EntityEntryBuilder<T>> consumer) {
        return register(clazz, new ResourceLocation(manager.getModId(), name), updateFrequency, consumer);
    }

    @Override
    public <T extends Entity> EntityEntry register(Class<T> clazz, ResourceLocation location, int updateFrequency, Consumer<EntityEntryBuilder<T>> consumer) {
        EntityEntryBuilder<T> builder = EntityEntryBuilder.create();
        builder.entity(clazz);
        builder.id(location, count++);
        builder.name(location.getPath());
        builder.tracker(80, updateFrequency, true);
        consumer.accept(builder);
        return this.register(builder.build());
    }

    @SideOnly(Side.CLIENT)
    protected void registerRenders() {
        Set<Class<?>> set = new HashSet<>();
        this.list.forEach(e -> {
            Class<? extends Entity> entityClass = e.getEntityClass();
            if (RenderProvider.class.isAssignableFrom(entityClass)) {
                if (set.contains(entityClass)) {
                    return;
                }
                set.add(entityClass);
                try {
                    Entity entity = entityClass.getConstructor(World.class).newInstance((World) null);
                    ((RenderProvider) entity).bindRender();
                }
                catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
