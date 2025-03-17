package surreal.backportium.potion.v13;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.Tags;
import surreal.backportium.potion.PotionBasic;
import surreal.backportium.tile.v13.TileConduit;

public class PotionConduitPower extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/mob_effect/conduit_power.png");

    public PotionConduitPower(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.setBeneficial();
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    public boolean shouldApply(@NotNull EntityLivingBase entity) {
        return TileConduit.shouldApplyToEntity(entity);
    }
}
