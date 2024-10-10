package surreal.backportium.client.renderer.entity.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.entity.v1_13.EntityPhantom;

import javax.annotation.Nonnull;

public class LayerPhantomEyes<T extends EntityPhantom> implements LayerRenderer<T> {

    private static final ResourceLocation PHANTOM_EYES_TEXTURE = new ResourceLocation("textures/entity/phantom_eyes.png");

    private final RenderLiving<T> render;

    public LayerPhantomEyes(RenderLiving<T> render) {
        this.render = render;
    }

    @Override
    public void doRenderLayer(@Nonnull T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        this.render.bindTexture(PHANTOM_EYES_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(!entitylivingbaseIn.isInvisible()); // DIFFERENCE: They don't close depth mask on newer versions, it's a bug https://bugs.mojang.com/browse/MC-219279
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 65536, 0);
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        this.render.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
