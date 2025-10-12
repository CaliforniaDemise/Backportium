package surreal.backportium._internal.registry;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public interface Items {
    <V extends Item> V register(V entry, String name);
    <V extends Item> V register(V entry, ResourceLocation location);
}
