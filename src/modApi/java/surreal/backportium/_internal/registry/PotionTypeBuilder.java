package surreal.backportium._internal.registry;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;

import java.util.LinkedList;
import java.util.List;

public class PotionTypeBuilder {

    private final List<PotionEffect> effects = new LinkedList<>();

    private PotionTypeBuilder() {}

    protected static PotionTypeBuilder create() {
        return new PotionTypeBuilder();
    }

    public PotionTypeBuilder effect(Potion potion) {
        return effect(potion, 0);
    }

    public PotionTypeBuilder effect(Potion potion, int duration) {
        return effect(potion, duration, 0);
    }

    public PotionTypeBuilder effect(Potion potion, int duration, int amplifier) {
        return effect(potion, duration, amplifier, false, true);
    }

    public PotionTypeBuilder effect(Potion potion, int duration, int amplifier, boolean ambient, boolean showParticles) {
        this.effects.add(new PotionEffect(potion, duration, amplifier, ambient, showParticles));
        return this;
    }

    protected PotionType build() {
        return new PotionType(this.effects.toArray(new PotionEffect[0]));
    }
}
