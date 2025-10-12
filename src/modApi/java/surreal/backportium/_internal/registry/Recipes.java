package surreal.backportium._internal.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public interface Recipes {
    void shaped(String name, ItemStack output, Object... inputs);
    void shaped(ResourceLocation name, ItemStack output, Object... inputs);
    void shapeless(ResourceLocation name, ItemStack output, Object... inputs);
    void shapeless(String name, ItemStack output, Object... inputs);
}
