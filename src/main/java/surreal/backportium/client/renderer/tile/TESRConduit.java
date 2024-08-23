package surreal.backportium.client.renderer.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import surreal.backportium.client.model.tile.ModelConduit;
import surreal.backportium.tile.v1_13.TileConduit;

import javax.annotation.Nonnull;

public class TESRConduit extends TileEntitySpecialRenderer<TileConduit> {

    private static final ResourceLocation SHELL_TEXTURE = new ResourceLocation("textures/entity/conduit/base.png");
    private static final ResourceLocation CAGE_TEXTURE = new ResourceLocation("textures/entity/conduit/cage.png");

    private static final ResourceLocation CLOSED_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/closed_eye.png");
    private static final ResourceLocation OPEN_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/open_eye.png");

    private static final String WIND_TEXTURE = "minecraft:entity/conduit/wind";
    private static final String WIND_VERTICAL_TEXTURE = "minecraft:entity/conduit/wind_vertical";

    private final ModelConduit SHELL = new ModelConduit.Shell();
    private final ModelConduit CAGE = new ModelConduit.Cage();

    @Override
    public void render(@Nonnull TileConduit te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 0.425F, z + 0.5F);
        renderTile(Minecraft.getMinecraft(), te, x, y, z, partialTicks);
        GlStateManager.popMatrix();
    }

    private void renderTile(Minecraft mc, TileConduit te, double x, double y, double z, float partialTicks) {
        int power = te.getPower();

        if (power != 0) {
            float rotation = (te.getFrame() + partialTicks) * -0.0375F * (float) (180F / Math.PI);
            float offset = MathHelper.sin((te.getFrame() + partialTicks) * 0.1F) / 2.0F;
            offset = offset * offset + offset * 2;

            renderWind(mc, te, x, y, z, partialTicks);

            GlStateManager.translate(0, offset * 0.2F, 0);
            renderEye(mc, te, x, y, z, partialTicks);

            GlStateManager.rotate(rotation, 0.5F, 1.0F, 0.5F);
        }

        renderCage(mc, te, x, y, z, partialTicks);
    }

    private void renderCage(Minecraft mc, TileConduit te, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();
        int power = te.getPower();
        if (power == 0) {
            this.bindTexture(SHELL_TEXTURE);
            GlStateManager.translate(0, 0.075F, 0);
            this.SHELL.render(mc, te, x, y, z, 0.0625F, partialTicks);
        }
        else {
            this.bindTexture(CAGE_TEXTURE);
            GlStateManager.disableCull();
            this.CAGE.render(mc, te, x, y, z, 0.0625F, partialTicks);
            GlStateManager.enableCull();
        }
        GlStateManager.popMatrix();
    }

    private void renderWind(Minecraft mc, TileConduit te, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.translate(-0.5F, -0.0425F, -0.5F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(WIND_TEXTURE);

        double minU = sprite.getMinU();
        double maxU = sprite.getMaxU();
        double minV = sprite.getMinV();
        double maxV = sprite.getMaxV() / 2;

        float aPix = 1F / 16;
        float fifteenPix = aPix * 15;

        // -Z
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(aPix, aPix, 0).tex(minU, maxV).endVertex();
        builder.pos(fifteenPix, aPix, 0).tex(maxU, maxV).endVertex();
        builder.pos(fifteenPix, fifteenPix, 0).tex(maxU, minV).endVertex();
        builder.pos(aPix, fifteenPix, 0).tex(minU, minV).endVertex();

        // +Z
        builder.pos(aPix, aPix, 1).tex(maxU, maxV).endVertex();
        builder.pos(fifteenPix, aPix, 1).tex(minU, maxV).endVertex();
        builder.pos(fifteenPix, fifteenPix, 1).tex(minU, minV).endVertex();
        builder.pos(aPix, fifteenPix, 1).tex(maxU, minV).endVertex();

        // -X
        builder.pos(0, aPix, aPix).tex(maxU, maxV).endVertex();
        builder.pos(0, aPix, fifteenPix).tex(minU, maxV).endVertex();
        builder.pos(0, fifteenPix, fifteenPix).tex(minU, minV).endVertex();
        builder.pos(0, fifteenPix, aPix).tex(maxU, minV).endVertex();

        // +X
        builder.pos(1, aPix, aPix).tex(minU, maxV).endVertex();
        builder.pos(1, aPix, fifteenPix).tex(maxU, maxV).endVertex();
        builder.pos(1, fifteenPix, fifteenPix).tex(maxU, minV).endVertex();
        builder.pos(1, fifteenPix, aPix).tex(minU, minV).endVertex();

        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void renderEye(Minecraft mc, TileConduit tile, double x, double y, double z, float partialTicks) {
        int power = tile.getPower();

        GlStateManager.pushMatrix();
        if (power == 5) this.bindTexture(OPEN_EYE_TEXTURE);
        else this.bindTexture(CLOSED_EYE_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        RenderManager manager = mc.getRenderManager();
        GlStateManager.rotate(180F - manager.playerViewY, 0, 5, 0);
        GlStateManager.rotate((float) (manager.options.thirdPersonView == 2 ? -1 : 1) * -manager.playerViewX, 5, 0, 0);

        builder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        double size = 0.6;
        builder.pos(-size/2 + 0.15F , -size/4 - 0.3F, 0.0D).tex(0, 1).normal(0, 1, 0).endVertex();
        builder.pos(size/2 + 0.15F, -size/4 - 0.3F, 0.0D).tex(1, 1).normal(0, 1, 0).endVertex();
        builder.pos(size/2 + 0.15F, (size/4) * 3 - 0.3F, 0.0D).tex(1, 0).normal(0, 1, 0).endVertex();
        builder.pos(-size/2 + 0.15F, (size/4) * 3 - 0.3F, 0.0D).tex(0, 0).normal(0, 1, 0).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }
}
