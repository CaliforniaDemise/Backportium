package surreal.backportium._internal.client.renderer.item;

import net.minecraft.item.ItemStack;

public interface ItemRender {
    void render(ItemStack stack, float partialTicks);
}
