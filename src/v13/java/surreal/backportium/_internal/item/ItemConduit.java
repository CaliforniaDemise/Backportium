package surreal.backportium._internal.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium._internal.client.renderer.item.ItemRender;
import surreal.backportium._internal.client.renderer.item.ModTEISR;
import surreal.backportium._internal.tile.TileConduit;

public class ItemConduit extends ItemBlock implements ItemRender {

    @SideOnly(Side.CLIENT)
    private static TileConduit conduit = new TileConduit();

    public ItemConduit(Block block) {
        super(block);
        if (FMLLaunchHandler.side().isClient()) {
            this.setTileEntityItemStackRenderer(ModTEISR.instance());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void render(ItemStack stack, float partialTicks) {
        TileEntitySpecialRenderer<TileConduit> tesr = TileEntityRendererDispatcher.instance.getRenderer(conduit);
        if (tesr != null) {
            GlStateManager.pushMatrix();
            tesr.render(conduit, 0, 0, 0, partialTicks, 0, 255);
            GlStateManager.popMatrix();
        }
    }
}
