package surreal.backportium.entity.v1_13;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIWanderAvoidWaterFlying;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import surreal.backportium.entity.EntityUndead;
import surreal.backportium.entity.ModEntities;

import javax.annotation.Nullable;

public class EntityPhantom extends EntityUndead {

    public EntityPhantom(World worldIn) {
        super(worldIn);
        this.setSize(0.5F, 0.9F);
        this.moveHelper = new EntityFlyHelper(this);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAIAvoidEntity<>(this, EntityOcelot.class, 16.0F, 1.0D, 1.0D));
        this.tasks.addTask(1, new EntityAIWanderAvoidWaterFlying(this, 1.0D));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.7D);
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return ModEntities.LOOT_PHANTOM;
    }

    // ParticleSuspendedTown
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (world.isRemote && !this.isInvisible()) {
            Minecraft mc = Minecraft.getMinecraft();
            float parTicks = mc.isGamePaused() ? 0.0F : Minecraft.getMinecraft().getRenderPartialTicks();
            float rot = MathHelper.sin((this.ticksExisted + parTicks) / 8.0F) * 0.5F;
            float rotY = MathHelper.sin(-rot) + 0.15F;
            float rotX = MathHelper.cos(-rot) + 0.35F;
            float rotZ = rotX;
            float yaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * parTicks;
            yaw *= 0.017453292F;
            rotX *= MathHelper.cos(yaw);
            rotZ *= MathHelper.sin(yaw);
            this.world.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, this.posX + rotX, this.posY + rotY, this.posZ + rotZ, 0, 0, 0);
            this.world.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, this.posX - rotX, this.posY + rotY, this.posZ - rotZ, 0, 0, 0);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
