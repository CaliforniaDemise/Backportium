package surreal.backportium._internal.registry;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public interface Potions {
    <V extends Potion> V register(V entry, String name);
    <V extends Potion> V register(V entry, ResourceLocation name);
}
