package surreal.backportium.client.renderer.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.client.renderer.entity.RenderTrident;
import surreal.backportium.entity.v1_13.EntityTrident;
import surreal.backportium.item.ItemBlockTEISR;
import surreal.backportium.item.v1_13.ItemShulkerBox;
import surreal.backportium.item.v1_13.ItemTrident;
import surreal.backportium.tile.v1_13.TileConduit;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class ModTEISR extends TileEntityItemStackRenderer {

    private static final ModTEISR INSTANCE = new ModTEISR();

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final EntityTrident trident = new EntityTrident(Minecraft.getMinecraft().world);

    private final TileConduit conduit = new TileConduit();
    private final TileEntityShulkerBox shulkerBox = new TileEntityShulkerBox(null);

    @Override
    public void renderByItem(@Nonnull ItemStack stack, float partialTicks) {
        Item item = stack.getItem();

        if (item instanceof ItemTrident) {
            RenderTrident<EntityTrident> render = (RenderTrident) mc.getRenderManager().getEntityRenderObject(trident);
            if (render != null) {
                GlStateManager.pushMatrix();
                render.renderStack(trident, stack);
                GlStateManager.popMatrix();
            }
            return;
        }

        if (item instanceof ItemBlockTEISR) {
            Block block = ((ItemBlockTEISR) item).getBlock();
            if (block == ModBlocks.CONDUIT) {
                TileEntitySpecialRenderer<TileConduit> tesr = TileEntityRendererDispatcher.instance.getRenderer(conduit);
                if (tesr != null) {
                    GlStateManager.pushMatrix();
                    tesr.render(conduit, 0, 0, 0, partialTicks, 0, 255);
                    GlStateManager.popMatrix();
                }
            }
            return;
        }

        if (item instanceof ItemShulkerBox) {
            TileEntitySpecialRenderer<TileEntityShulkerBox> tesr = TileEntityRendererDispatcher.instance.getRenderer(shulkerBox);
            if (tesr != null) {
                GlStateManager.pushMatrix();
                tesr.render(shulkerBox, 0, 0, 0, partialTicks, -1, 255);
                GlStateManager.popMatrix();
            }
        }
    }

    public static ModTEISR get() {
        return INSTANCE;
    }
}
