package surreal.backportium.client.renderer.entity.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;
import surreal.backportium.api.helper.TridentHelper;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") // Used in TridentTransformer
public class LayerRiptide implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocation TRIDENT_RIPTIDE_TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/entity/trident_riptide.png");

    private final Render<EntityLivingBase> entityRender;
    private final ModelBase riptideModel;

    public LayerRiptide(Render<EntityLivingBase> entityRender, ModelBase riptideModel) {
        this.entityRender = entityRender;
        this.riptideModel = riptideModel;
    }

    @Override
    public void doRenderLayer(@Nonnull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (TridentHelper.isInRiptide(entity)) {
            this.entityRender.bindTexture(TRIDENT_RIPTIDE_TEXTURE);
            this.riptideModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
