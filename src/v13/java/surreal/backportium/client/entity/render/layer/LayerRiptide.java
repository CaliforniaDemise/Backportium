package surreal.backportium.client.entity.render.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;
import surreal.backportium.api.entity.RiptideEntity;
import surreal.backportium.client.entity.model.ModelRiptide;

import javax.annotation.Nonnull;

public class LayerRiptide implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocation TRIDENT_RIPTIDE_TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/entity/trident_riptide.png");
    private static final ModelBase riptideModel = new ModelRiptide();

    private final Render<EntityLivingBase> entityRender;

    public LayerRiptide(Render<EntityLivingBase> entityRender) {
        this.entityRender = entityRender;
    }

    @Override
    public void doRenderLayer(@Nonnull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (RiptideEntity.cast(entity).inRiptide()) {
            this.entityRender.bindTexture(TRIDENT_RIPTIDE_TEXTURE);
            riptideModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}