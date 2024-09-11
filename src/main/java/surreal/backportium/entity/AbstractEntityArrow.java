package surreal.backportium.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

// Not the best implementation but why bother.
// Not going to handle these in EntityTrident because it would get messy pretty fast.
public abstract class AbstractEntityArrow extends EntityArrow {

    protected int xTile, yTile, zTile;
    protected Block inTile;
    protected int inData;
    protected int ticksInGround, ticksInAir;
    protected int knockbackStrength;

    public AbstractEntityArrow(World worldIn) {
        super(worldIn);
    }

    public AbstractEntityArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public AbstractEntityArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    @Override
    public void onUpdate() {
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }
        this.onEntityUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (this.shouldHitBlock(blockpos, iblockstate)) {

            if (iblockstate.getMaterial() != Material.AIR)
            {
                AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);

                if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ)))
                {
                    this.inGround = true;
                }
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            int j = block.getMetaFromState(iblockstate);

            if ((block != this.inTile || j != this.inData) && !this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.05D)))
            {
                this.inGround = false;
                this.motionX *= (this.rand.nextFloat() * 0.2F);
                this.motionY *= (this.rand.nextFloat() * 0.2F);
                this.motionZ *= (this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
            else
            {
                ++this.ticksInGround;

                if (this.ticksInGround >= 1200)
                {
                    this.setDead();
                }
            }

            ++this.timeInGround;
        }
        else
        {
            this.timeInGround = 0;
            ++this.ticksInAir;
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
            vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (raytraceresult != null) {
                vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }

            Entity entity = this.findEntityOnPath(vec3d1, vec3d);

            if (entity != null) {
                raytraceresult = new RayTraceResult(entity);
            }

            if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;

                if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer)) {
                    raytraceresult = null;
                }
            }

            if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onHit(raytraceresult);
            }

            if (this.getIsCritical()) {
                for (int k = 0; k < 4; ++k) {
                    this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double)k / 4.0D, this.posY + this.motionY * (double)k / 4.0D, this.posZ + this.motionZ * (double)k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f4) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {}

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f1 = 0.99F;

            if (this.isInWater())
            {
                for (int i = 0; i < 4; ++i) {
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
                }

                f1 = this.getSpeedChangeInWater();
            }

            if (this.isWet())
            {
                this.extinguish();
            }

            this.motionX *= f1;
            this.motionY *= f1;
            this.motionZ *= f1;

            if (!this.hasNoGravity())
            {
                this.motionY -= 0.05000000074505806D;
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {

        Entity entity = raytraceResultIn.entityHit;

        if (entity != null && shouldHitEntity(entity)) {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            int i = MathHelper.ceil((double)f * this.getDamage());

            if (this.getIsCritical()) {
                i += this.rand.nextInt(i / 2 + 2);
            }

            DamageSource damagesource;

            if (this.shootingEntity == null) {
                damagesource = DamageSource.causeArrowDamage(this, this);
            }
            else {
                damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
            }

            if (this.isBurning() && !(entity instanceof EntityEnderman)) {
                entity.setFire(5);
            }

            if (entity.attackEntityFrom(damagesource, this.getHitDamage(entity, (float) i))) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase)entity;

                    if (!this.world.isRemote) {
                        entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                    }

                    if (this.knockbackStrength > 0)
                    {
                        float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                        if (f1 > 0.0F)
                        {
                            entitylivingbase.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1);
                        }
                    }

                    if (this.shootingEntity instanceof EntityLivingBase)
                    {
                        EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, entitylivingbase);
                    }

                    this.arrowHit(entitylivingbase);

                    if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
                    }
                }

                this.playSound(this.getEntityHitSound(entity), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                if (this.shouldDieAfterHit(entity)) {
                    this.setDead();
                }
            }
            else
            {
                this.motionX *= -0.10000000149011612D;
                this.motionY *= -0.10000000149011612D;
                this.motionZ *= -0.10000000149011612D;
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;
                this.ticksInAir = 0;

                if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D)
                {
                    if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED)
                    {
                        this.entityDropItem(this.getArrowStack(), 0.1F);
                    }

                    this.setDead();
                }
            }
        }
        else {
            BlockPos blockpos = raytraceResultIn.getBlockPos();
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            if (this.shouldHitBlock(blockpos, iblockstate)) {
                this.xTile = blockpos.getX();
                this.yTile = blockpos.getY();
                this.zTile = blockpos.getZ();
                this.inTile = iblockstate.getBlock();
                this.inData = this.inTile.getMetaFromState(iblockstate);
                this.motionX = ((float)(raytraceResultIn.hitVec.x - this.posX));
                this.motionY = ((float)(raytraceResultIn.hitVec.y - this.posY));
                this.motionZ = ((float)(raytraceResultIn.hitVec.z - this.posZ));
                float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
                this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
                this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
                this.playSound(this.getBlockHitSound(blockpos, iblockstate), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.inGround = true;
                this.arrowShake = 7;
                this.setIsCritical(false);

                if (iblockstate.getMaterial() != Material.AIR) {
                    this.inTile.onEntityCollision(this.world, blockpos, iblockstate, this);
                }
            }
        }
    }

    public boolean shouldDieAfterHit(Entity entity) {
        return !(entity instanceof EntityEnderman);
    }

    public boolean shouldHitEntity(Entity entity) {
        return true;
    }

    public boolean shouldHitBlock(BlockPos pos, IBlockState state) {
        return true;
    }

    public SoundEvent getEntityHitSound(Entity entity) {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    public SoundEvent getBlockHitSound(BlockPos pos, IBlockState state) {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    public float getSpeedChangeInWater() {
        return 0.6F;
    }

    public float getHitDamage(Entity entity, float damage) {
        return damage;
    }
}