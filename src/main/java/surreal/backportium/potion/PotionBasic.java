package surreal.backportium.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class PotionBasic extends Potion {

    public PotionBasic(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @Nonnull
    @Override
    public Potion setPotionName(@Nonnull String nameIn) {
        return super.setPotionName("potion." + nameIn);
    }

    protected abstract ResourceLocation getTextureLocation();

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        ResourceLocation location = this.getTextureLocation();
        if (location == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(location);
        Gui.drawScaledCustomSizeModalRect(x + 6, y + 7, 0F, 0F, 1, 1, 18, 18, 1F, 1F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
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
