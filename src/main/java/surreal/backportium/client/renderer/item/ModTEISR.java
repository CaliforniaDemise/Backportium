package surreal.backportium.client.renderer.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.client.renderer.entity.RenderTrident;
import surreal.backportium.entity.v1_13.EntityTrident;
import surreal.backportium.item.v1_13.ItemTrident;
import surreal.backportium.tile.v1_13.TileConduit;

import javax.annotation.Nonnull;

public class ModTEISR extends TileEntityItemStackRenderer {

    private static final ModTEISR INSTANCE = new ModTEISR();

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final EntityTrident trident = new EntityTrident(Minecraft.getMinecraft().world);

    private final TileConduit conduit = new TileConduit();

    @Override
    public void renderByItem(@Nonnull ItemStack stack, float partialTicks) {
        Item item = stack.getItem();

        if (item instanceof ItemTrident) {
            RenderTrident<EntityTrident> render = (RenderTrident) mc.getRenderManager().getEntityRenderObject(trident);
            if (render != null) {
                GlStateManager.pushMatrix();
                render.render(trident);
                GlStateManager.popMatrix();
            }
        }

        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();

            if (block == ModBlocks.CONDUIT) {
                TileEntitySpecialRenderer<TileConduit> tesr = TileEntityRendererDispatcher.instance.getRenderer(conduit);
                if (tesr != null) {
                    GlStateManager.pushMatrix();
                    tesr.render(conduit, 0, 0,0, partialTicks, 0, 255);
                    GlStateManager.popMatrix();
                }
            }
        }


        //        if (stack.getItem() instanceof ItemTrident) {
//            if (this.itemActivationItem != null && this.itemActivationTicks > 0)
//            {
//                int i = 40 - this.itemActivationTicks;
//                float f = ((float)i + p_190563_3_) / 40.0F;
//                float f1 = f * f;
//                float f2 = f * f1;
//                float f3 = 10.25F * f2 * f1 + -24.95F * f1 * f1 + 25.5F * f2 + -13.8F * f1 + 4.0F * f;
//                float f4 = f3 * (float)Math.PI;
//                float f5 = this.itemActivationOffX * (float)(p_190563_1_ / 4);
//                float f6 = this.itemActivationOffY * (float)(p_190563_2_ / 4);
//                GlStateManager.enableAlpha();
//                GlStateManager.pushMatrix();
//                GlStateManager.pushAttrib();
//                GlStateManager.enableDepth();
//                GlStateManager.disableCull();
//                RenderHelper.enableStandardItemLighting();
//                GlStateManager.translate((float)(p_190563_1_ / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), (float)(p_190563_2_ / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
//                float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
//                GlStateManager.scale(f7, -f7, f7);
//                GlStateManager.rotate(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
//                GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
//                GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
//                this.mc.getRenderItem().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED);
//                GlStateManager.popAttrib();
//                GlStateManager.popMatrix();
//                RenderHelper.disableStandardItemLighting();
//                GlStateManager.enableCull();
//                GlStateManager.disableDepth();
//            }
//        }
    }

    public static ModTEISR get() {
        return INSTANCE;
    }
}
