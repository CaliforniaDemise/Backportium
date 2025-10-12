package surreal.backportium._internal.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.client.renderer.TextureStitcher;

public abstract class PotionBasic extends Potion implements TextureStitcher {

    private final ResourceLocation textureLocation;

    protected PotionBasic(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        ResourceLocation location = this.getTextureLocation();
        this.textureLocation = new ResourceLocation(location.getNamespace(), location.getPath().substring(9, location.getPath().length() - 4));
    }

    @NotNull
    @Override
    public Potion setPotionName(@NotNull String nameIn) {
        return super.setPotionName("potion." + nameIn);
    }

    protected abstract ResourceLocation getTextureLocation();
    @Override public void stitchTextures(TextureMap map) { map.registerSprite(this.textureLocation); }

    public boolean shouldApply(EntityLivingBase entity) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(@NotNull PotionEffect effect, @NotNull Gui gui, int x, int y, float z) {
        ResourceLocation location = this.getTextureLocation();
        if (location == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(location);
        Gui.drawScaledCustomSizeModalRect(x + 6, y + 7, 0F, 0F, 1, 1, 18, 18, 1F, 1F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(@NotNull PotionEffect effect, @NotNull Gui gui, int x, int y, float z, float alpha) {
        ResourceLocation location = this.getTextureLocation();
        if (location == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(location);
        Gui.drawScaledCustomSizeModalRect(x + 3, y + 3, 0F, 0F, 1, 1, 18, 18, 1F, 1F);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
}
