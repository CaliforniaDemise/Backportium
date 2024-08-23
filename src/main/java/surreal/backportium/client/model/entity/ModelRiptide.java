package surreal.backportium.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import surreal.backportium.api.helper.RiptideHelper;

import javax.annotation.Nonnull;

public class ModelRiptide extends ModelBase {

    private final ModelRenderer riptide;

    public ModelRiptide() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.riptide = new ModelRenderer(this, 0, 0);
        this.riptide.addBox(-8.0F, -16.0F, -8.0F, 16, 32, 16);
    }

    @Override
    public void render(@Nonnull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entityIn instanceof EntityLivingBase && RiptideHelper.isInRiptide((EntityLivingBase) entityIn)) {
            for (int i = 0; i < 3; i++) {
                GlStateManager.pushMatrix();
                float n = netHeadYaw * (float) (-(45 + i * 5));
                GlStateManager.rotate(n, 0.0F, 1.0F, 0.0F);
                float o = 0.75F * (float) i;
                GlStateManager.scale(o, o, o);
                GlStateManager.translate(0.0F, -0.2F+ 0.6F * (float) i, 0.0F);
                this.riptide.render(scale);
                GlStateManager.popMatrix();
            }
        }
    }
}
