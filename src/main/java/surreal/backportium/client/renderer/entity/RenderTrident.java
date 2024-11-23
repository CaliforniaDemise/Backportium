package surreal.backportium.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;
import surreal.backportium.entity.v1_13.EntityTrident;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderTrident<T extends EntityTrident> extends Render<T> {

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    protected static final ResourceLocation TRIDENT_TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/entity/trident.png");

    protected final ModelBase trident;

    public RenderTrident(RenderManager renderManager, ModelBase trident) {
        super(renderManager);
        this.trident = trident;
    }

    @Override
    public void doRender(@Nonnull T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 90F, 0.0F, 0.0F, 1.0F);
        this.bindEntityTexture(entity);
        GlStateManager.scale(1F, -1F, -1F);
        this.trident.render(entity, 0, 0, 0, 0, 0, 0.06F);
        GlStateManager.enableBlend();
        this.renderEffect(entity, ItemStack.EMPTY);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void renderStack(@Nonnull T entity, @Nonnull ItemStack stack) {
        this.bindEntityTexture(entity);
        GlStateManager.scale(1F, -1F, -1F);
        this.trident.render(entity, 0, 0,0 , 0, 0, 0.06F);
        this.renderEffect(entity, stack);
    }

    private void renderEffect(T entity, ItemStack stack) {
        if (!stack.isEmpty()) {
            if (!stack.hasEffect()) return;
        }
        else if (!entity.hasEffect()) return;
        float r, g, b, a;
        {
            int aVal = (-8372020 >> 24) & 0xFF;
            int rVal = (-8372020 >> 16) & 0xFF;
            int gVal = (-8372020 >> 8) & 0xFF;
            int bVal = -8372020 & 0xFF;
            r = (float) rVal / 255F;
            g = (float) gVal / 255F;
            b = (float) bVal / 255F;
            a = (float) aVal / 255F;
        }
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        this.bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.48F, 0.48F, 0.48F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.color(r, g, b, a);
        this.trident.render(entity, 0, 0, 0, 0, 0, 0.06F);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.48F, 0.48F, 0.48F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.color(r, g, b, a);
        this.trident.render(entity, 0, 0, 0, 0, 0, 0.06F);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull T entity) {
        return TRIDENT_TEXTURE;
    }
}
