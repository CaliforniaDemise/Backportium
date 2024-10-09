package surreal.backportium.entity;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class EntityUndead extends EntityMob {

    public EntityUndead(World worldIn) {
        super(worldIn);
    }

    @Nonnull
    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        float brightness = this.getBrightness();
        if (!this.world.isRemote && this.shouldBurn() && this.world.isDaytime() && brightness > 0.5F && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && this.world.canSeeSky(new BlockPos(this))) {
            this.setFire(8);
        }
    }

    protected boolean shouldBurn() {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }
}