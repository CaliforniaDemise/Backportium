package surreal.backportium.sound;

import net.minecraft.block.SoundType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModSoundTypes {

    public static final SoundType CORAL = new SoundTypeSupplier(1.0F, 1.0F, () -> ModSounds.BLOCK_CORAL_BLOCK_BREAK, () -> ModSounds.BLOCK_CORAL_BLOCK_STEP, () -> ModSounds.BLOCK_CORAL_BLOCK_PLACE, () -> ModSounds.BLOCK_CORAL_BLOCK_HIT, () -> ModSounds.BLOCK_CORAL_BLOCK_FALL);


    private static class SoundTypeSupplier extends SoundType {

        private final Supplier<SoundEvent> breakSound;
        private final Supplier<SoundEvent> stepSound;
        private final Supplier<SoundEvent> placeSound;
        private final Supplier<SoundEvent> hitSound;
        private final Supplier<SoundEvent> fallSound;

        protected SoundTypeSupplier(float volumeIn, float pitchIn, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
            super(volumeIn, pitchIn, SoundEvents.BLOCK_STONE_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);
            this.breakSound = breakSound;
            this.stepSound = stepSound;
            this.placeSound = placeSound;
            this.hitSound = hitSound;
            this.fallSound = fallSound;
        }

        @NotNull @Override public SoundEvent getBreakSound() { return this.breakSound.get(); }
        @NotNull @Override public SoundEvent getStepSound() { return this.stepSound.get(); }
        @NotNull @Override public SoundEvent getPlaceSound() { return this.placeSound.get(); }
        @NotNull @Override public SoundEvent getHitSound() { return this.hitSound.get(); }
        @NotNull @Override public SoundEvent getFallSound() { return this.fallSound.get(); }
    }
}
