package surreal.backportium.tag;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Objects;

public class EntityTag extends Tag<EntityEntry> {

    public EntityTag() {
        super(ENTITY_STRATEGY);
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

    private static final Hash.Strategy<EntityEntry> ENTITY_STRATEGY = new Hash.Strategy<EntityEntry>() {

        @Override
        public int hashCode(EntityEntry entry) {
            if (entry == null) return 0;
            return Objects.hashCode(entry);
        }

        @Override
        public boolean equals(EntityEntry a, EntityEntry b) {
            if (a == null) return b == null;
            if (b == null) return false;
            return a == b;
        }
    };
}
