package surreal.backportium.tag;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityTag extends Tag<EntityEntry> {

    public EntityTag() {
        super(null);
    }

    public boolean contains(String name, Entity entity) {
        EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
        return this.contains(name, entry);
    }

    public void add(String name, Class<? extends Entity> entityCls) {
        EntityEntry entry = EntityRegistry.getEntry(entityCls);
        if (entry == null) return;
        this.add(name, entry);
    }

    public void add(String name, ResourceLocation location) {
        Class<? extends Entity> entityCls = EntityList.getClass(location);
        if (entityCls == null) return;
        this.add(name, entityCls);
    }

    public void remove(String name, Class<? extends Entity> entityCls) {
        EntityEntry entry = EntityRegistry.getEntry(entityCls);
        if (entry == null) return;
        this.remove(name, entry);
    }

    public void remove(String name, ResourceLocation location) {
        Class<? extends Entity> entityCls = EntityList.getClass(location);
        if (entityCls == null) return;
        this.remove(name, entityCls);
    }
}
