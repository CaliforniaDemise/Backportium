package surreal.backportium._internal.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

public interface Enchantments {
    <V extends Enchantment> V register(V entry, String name);
    <V extends Enchantment> V register(V entry, ResourceLocation name);
}
