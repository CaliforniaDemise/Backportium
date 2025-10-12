package surreal.backportium._internal.block;

import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class SoundType extends net.minecraft.block.SoundType {

    private final Supplier<SoundEvent> breakSound;
    private final Supplier<SoundEvent> stepSound;
    private final Supplier<SoundEvent> placeSound;
    private final Supplier<SoundEvent> hitSound;
    private final Supplier<SoundEvent> fallSound;

    private SoundType(float volumeIn, float pitchIn, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        super(volumeIn, pitchIn, null, null, null, null, null);
        this.breakSound = breakSound;
        this.stepSound = stepSound;
        this.placeSound = placeSound;
        this.hitSound = hitSound;
        this.fallSound = fallSound;
    }

    public static SoundType get(float volumeIn, float pitchIn, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        return new SoundType(volumeIn, pitchIn, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    public static SoundType get(Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        return get(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    @NotNull
    @Override
    public SoundEvent getBreakSound() {
        return breakSound.get();
    }

    @NotNull
    @Override
    public SoundEvent getStepSound() {
        return stepSound.get();
    }

    @NotNull
    @Override
    public SoundEvent getPlaceSound() {
        return placeSound.get();
    }

    @NotNull
    @Override
    public SoundEvent getHitSound() {
        return hitSound.get();
    }

    @NotNull
    @Override
    public SoundEvent getFallSound() {
        return fallSound.get();
    }
}
