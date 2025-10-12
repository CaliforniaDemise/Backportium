package surreal.backportium._internal.client.renderer.item;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class ModTEISR extends TileEntityItemStackRenderer {

    private static final ModTEISR INSTANCE = new ModTEISR();

    @Override
    public void renderByItem(@Nonnull ItemStack stack, float partialTicks) {
        Item item = stack.getItem();
        if (item instanceof ItemRender) ((ItemRender) item).render(stack, partialTicks);
    }

    public static ModTEISR instance() {
        return INSTANCE;
    }
}
