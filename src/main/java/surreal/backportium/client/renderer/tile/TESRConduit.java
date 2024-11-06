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
import surreal.backportium.Tags;
import surreal.backportium.client.model.tile.ModelConduit;
import surreal.backportium.tile.v1_13.TileConduit;

import javax.annotation.Nonnull;

public class TESRConduit extends TileEntitySpecialRenderer<TileConduit> {

    private static final ResourceLocation SHELL_TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/entity/conduit/base.png");
    private static final ResourceLocation CAGE_TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/entity/conduit/cage.png");

    private static final ResourceLocation CLOSED_EYE_TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/entity/conduit/closed_eye.png");
    private static final ResourceLocation OPEN_EYE_TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/entity/conduit/open_eye.png");

    private static final String WIND_TEXTURE = Tags.MOD_ID + ":entity/conduit/wind";
    private static final String WIND_VERTICAL_TEXTURE = Tags.MOD_ID + ":entity/conduit/wind_vertical";

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
            float offset = MathHelper.sin((te.getFrame() + partialTicks) * 0.1F) / 4.0F;
            offset += 0.15F;
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
        } else {
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

        GlStateManager.translate(-0.5F, -0.425D, -0.5F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        TextureAtlasSprite sprite;

        int l = te.getFrame() / 55 % 3;

        if (l == 1) sprite = mc.getTextureMapBlocks().getAtlasSprite(WIND_VERTICAL_TEXTURE);
        else {
            sprite = mc.getTextureMapBlocks().getAtlasSprite(WIND_TEXTURE);
        }

        double u0 = sprite.getInterpolatedU(0.0D);
        double u1 = sprite.getInterpolatedU(4.0D);
        double u2 = sprite.getInterpolatedU(8.0D);
        double u3 = sprite.getInterpolatedU(12.0D);
        double u4 = sprite.getInterpolatedU(16.0D);

        double minV = sprite.getInterpolatedV(4.01D);
        double maxV = sprite.getInterpolatedV(7.99D);

        double smallMinV = sprite.getInterpolatedV(4.125D);
        double smallMaxV = sprite.getInterpolatedV(7.825D);

        double start = 0.01D;
        double end = 0.99D;

        double smallStart = 0.125D;
        double smallEnd = 0.875D;

        builder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        // ----- LARGE ----- //
        if (l == 1) {
            // -X
            builder.pos(start, start, start).tex(u0, minV).normal(-1, 0, 0).endVertex();
            builder.pos(start, start, end).tex(u0, maxV).normal(-1, 0, 0).endVertex();
            builder.pos(start, end, end).tex(u1, maxV).normal(-1, 0, 0).endVertex();
            builder.pos(start, end, start).tex(u1, minV).normal(-1, 0, 0).endVertex();

            // +Y
            builder.pos(start, end, start).tex(u1, minV).normal(0, 1, 0).endVertex();
            builder.pos(end, end, start).tex(u2, minV).normal(0, 1, 0).endVertex();
            builder.pos(end, end, end).tex(u2, maxV).normal(0, 1, 0).endVertex();
            builder.pos(start, end, end).tex(u1, maxV).normal(0, 1, 0).endVertex();

            // +X
            builder.pos(end, start, start).tex(u3, minV).normal(-1, 0, 0).endVertex();
            builder.pos(end, start, end).tex(u3, maxV).normal(-1, 0, 0).endVertex();
            builder.pos(end, end, end).tex(u2, maxV).normal(-1, 0, 0).endVertex();
            builder.pos(end, end, start).tex(u2, minV).normal(-1, 0, 0).endVertex();

            // -Y
//            builder.pos(start, start, start).tex(u4, minV).normal(0, 1, 0).endVertex();
//            builder.pos(end, start, start).tex(u3, minV).normal(0, 1, 0).endVertex();
//            builder.pos(end, start, end).tex(u3, maxV).normal(0, 1, 0).endVertex();
//            builder.pos(start, start, end).tex(u4, maxV).normal(0, 1, 0).endVertex();
        }
        else if (l == 2) {
            // -Z
            builder.pos(start, start, end).tex(u0, minV).normal(0, 0, -1).endVertex();
            builder.pos(end, start, end).tex(u0, maxV).normal(0, 0, -1).endVertex();
            builder.pos(end, end, end).tex(u1, maxV).normal(0, 0, -1).endVertex();
            builder.pos(start, end, end).tex(u1, minV).normal(0, 0, -1).endVertex();

            // +Y
            builder.pos(start, end, end).tex(u1, minV).normal(0, 1, 0).endVertex();
            builder.pos(end, end, end).tex(u1, maxV).normal(0, 1, 0).endVertex();
            builder.pos(end, end, start).tex(u2, maxV).normal(0, 1, 0).endVertex();
            builder.pos(start, end, start).tex(u2, minV).normal(0, 1, 0).endVertex();

            // +Z
            builder.pos(start, end, start).tex(u2, minV).normal(0, 0, 1).endVertex();
            builder.pos(end, end, start).tex(u2, maxV).normal(0, 0, 1).endVertex();
            builder.pos(end, start, start).tex(u3, maxV).normal(0, 0, 1).endVertex();
            builder.pos(start, start, start).tex(u3, minV).normal(0, 0, 1).endVertex();

            // -Y
            builder.pos(start, start, end).tex(u4, minV).normal(0, 1, 0).endVertex();
            builder.pos(end, start, end).tex(u4, maxV).normal(0, 1, 0).endVertex();
            builder.pos(end, start, start).tex(u3, maxV).normal(0, 1, 0).endVertex();
            builder.pos(start, start, start).tex(u3, minV).normal(0, 1, 0).endVertex();
        }
        else {
            // ----- BIG ----- //
            // -Z
            builder.pos(start, start, start).tex(u0, minV).normal(0, 0, -1).endVertex();
            builder.pos(end, start, start).tex(u1, minV).normal(0, 0, -1).endVertex();
            builder.pos(end, end, start).tex(u1, maxV).normal(0, 0, -1).endVertex();
            builder.pos(start, end, start).tex(u0, maxV).normal(0, 0, -1).endVertex();

            // +X
            builder.pos(end, start, start).tex(u1, minV).normal(1, 0, 0).endVertex();
            builder.pos(end, start, end).tex(u2, minV).normal(1, 0, 0).endVertex();
            builder.pos(end, end, end).tex(u2, maxV).normal(1, 0, 0).endVertex();
            builder.pos(end, end, start).tex(u1, maxV).normal(1, 0, 0).endVertex();

            // +Z
            builder.pos(start, start, end).tex(u3, minV).normal(0, 0, 1).endVertex();
            builder.pos(end, start, end).tex(u2, minV).normal(0, 0, 1).endVertex();
            builder.pos(end, end, end).tex(u2, maxV).normal(0, 0, 1).endVertex();
            builder.pos(start, end, end).tex(u3, maxV).normal(0, 0, 1).endVertex();

            // -X
            builder.pos(start, start, start).tex(u4, minV).normal(-1, 0, 0).endVertex();
            builder.pos(start, start, end).tex(u3, minV).normal(-1, 0, 0).endVertex();
            builder.pos(start, end, end).tex(u3, maxV).normal(-1, 0, 0).endVertex();
            builder.pos(start, end, start).tex(u4, maxV).normal(-1, 0, 0).endVertex();
        }

        // ----- SMALL ----- //
        // +Z
        builder.pos(smallStart, smallStart, smallEnd).tex(u1, smallMaxV).normal(0, 0, 1).endVertex();
        builder.pos(smallEnd, smallStart, smallEnd).tex(u0, smallMaxV).normal(0, 0, 1).endVertex();
        builder.pos(smallEnd, smallEnd, smallEnd).tex(u0, smallMinV).normal(0, 0, 1).endVertex();
        builder.pos(smallStart, smallEnd, smallEnd).tex(u1, smallMinV).normal(0, 0, 1).endVertex();

        // -X
        builder.pos(smallStart, smallStart, smallStart).tex(u2, smallMaxV).normal(-1, 0, 0).endVertex();
        builder.pos(smallStart, smallStart, smallEnd).tex(u1, smallMaxV).normal(-1, 0, 0).endVertex();
        builder.pos(smallStart, smallEnd, smallEnd).tex(u1, smallMinV).normal(-1, 0, 0).endVertex();
        builder.pos(smallStart, smallEnd, smallStart).tex(u2, smallMinV).normal(-1, 0, 0).endVertex();

        // -Z
        builder.pos(smallStart, smallStart, smallStart).tex(u2, smallMaxV).normal(0, 0, -1).endVertex();
        builder.pos(smallEnd, smallStart, smallStart).tex(u3, smallMaxV).normal(0, 0, -1).endVertex();
        builder.pos(smallEnd, smallEnd, smallStart).tex(u3, smallMinV).normal(0, 0, -1).endVertex();
        builder.pos(smallStart, smallEnd, smallStart).tex(u2, smallMinV).normal(0, 0, -1).endVertex();

        // +X
        builder.pos(smallEnd, smallStart, smallStart).tex(u3, smallMaxV).normal(1, 0, 0).endVertex();
        builder.pos(smallEnd, smallStart, smallEnd).tex(u4, smallMaxV).normal(1, 0, 0).endVertex();
        builder.pos(smallEnd, smallEnd, smallEnd).tex(u4, smallMinV).normal(1, 0, 0).endVertex();
        builder.pos(smallEnd, smallEnd, smallStart).tex(u3, smallMinV).normal(1, 0, 0).endVertex();

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
        builder.pos(-size / 2 + 0.15F, -size / 4 - 0.3F, 0.0D).tex(0, 1).normal(0, 1, 0).endVertex();
        builder.pos(size / 2 + 0.15F, -size / 4 - 0.3F, 0.0D).tex(1, 1).normal(0, 1, 0).endVertex();
        builder.pos(size / 2 + 0.15F, (size / 4) * 3 - 0.3F, 0.0D).tex(1, 0).normal(0, 1, 0).endVertex();
        builder.pos(-size / 2 + 0.15F, (size / 4) * 3 - 0.3F, 0.0D).tex(0, 0).normal(0, 1, 0).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }
}
