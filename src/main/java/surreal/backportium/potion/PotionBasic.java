package surreal.backportium.potion;

import net.minecraft.potion.Potion;

import javax.annotation.Nonnull;

public abstract class PotionBasic extends Potion {

    public PotionBasic(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @Nonnull
    @Override
    public Potion setPotionName(@Nonnull String nameIn) {
        return super.setPotionName("potion." + nameIn);
    }
}
