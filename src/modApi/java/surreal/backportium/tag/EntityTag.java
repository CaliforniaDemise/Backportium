package surreal.backportium.tag;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityTag extends Tag<ResourceLocation> {

    private static final ResourceLocation PLAYER = new ResourceLocation("player");

    public EntityTag() {
        super(null);
    }

    public boolean contains(String name, Entity entity) {
        ResourceLocation location;
        if (entity instanceof EntityPlayer) {
            location = PLAYER;
        }
        else {
            EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
            if (entry == null) return false;
            location = entry.getRegistryName();
        }
        return this.contains(name, location);
    }

    public void add(String name, Class<? extends Entity> entityCls) {
        ResourceLocation location;
        if (EntityPlayer.class.isAssignableFrom(entityCls)) {
            location = PLAYER;
        }
        else {
            EntityEntry entry = EntityRegistry.getEntry(entityCls);
            if (entry == null) return;
            location = entry.getRegistryName();
        }
        this.add(name, location);
    }

    public void add(String name, @NotNull EntityEntry entry) {
        Objects.requireNonNull(entry.getRegistryName());
        this.add(name, entry.getRegistryName());
    }

    @Override
    public void add(String name, ResourceLocation location) {
        if (location.getNamespace().equals("minecraft") && location.getPath().equals("player")) location = PLAYER;
        super.add(name, location);
    }

    public void remove(String name, Class<? extends Entity> entityCls) {
        EntityEntry entry = EntityRegistry.getEntry(entityCls);
        if (entry == null) return;
        this.remove(name, entry.getRegistryName());
    }

    public void remove(String name, @NotNull EntityEntry entry) {
        Objects.requireNonNull(entry.getRegistryName());
        Class<? extends Entity> entityCls = EntityList.getClass(entry.getRegistryName());
        if (entityCls == null) return;
        this.remove(name, entityCls);
    }
}
