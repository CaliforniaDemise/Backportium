package surreal.backportium.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.entity.v1_13.EntityTrident;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderTrident<T extends EntityTrident> extends Render<T> {

    protected static final ResourceLocation TRIDENT_TEXTURE = new ResourceLocation("textures/entity/trident.png");

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
        //GlStateManager.scale(0.8F, 0.8F, 0.8F);
        render(entity);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void render(@Nonnull T entity) {
        this.bindEntityTexture(entity);
        GlStateManager.scale(1F, -1F, -1F);
        this.trident.render(entity, 0, 0, 0, 0, 0, 0.06F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull T entity) {
        return TRIDENT_TEXTURE;
    }
}
