package surreal.backportium.entity.v1_13;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.entity.AbstractEntityArrow;
import surreal.backportium.item.ModItems;

import javax.annotation.Nonnull;

public class EntityTrident extends AbstractEntityArrow {

    // Stack Details
    private int damage;
    protected NBTTagCompound tag; // We're throwing the item. Not using the item to throw things like Bow and Arrow

    // Enchantments
    private int loyaltyLvl = -1;
    private int impalingLvl = -1;
    private int channelingLvl = -1;

    // Loyalty
    private boolean moveLoyalty; // If it moves because of loyalty

    public EntityTrident(World worldIn) {
        super(worldIn);
    }

    public EntityTrident(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityTrident(World worldIn, EntityLivingBase shooter, ItemStack stack) {
        super(worldIn, shooter);
        this.tag = stack.getTagCompound();
        if (this.tag == null) this.tag = new NBTTagCompound();
        this.damage = stack.getItemDamage();
        this.initEnchLevels();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.moveLoyalty) {
            if (!this.world.isRemote) {
                double yEntity = this.shootingEntity.posY + this.shootingEntity.getEyeHeight() - 0.25D;
                this.prevRotationPitch = 180F;
                this.rotationPitch = 180F;
                double speed = 0.15D + loyaltyLvl * 0.05D;
                double x = this.shootingEntity.posX - this.posX;
                double y = yEntity - this.posY;
                double z = this.shootingEntity.posZ - this.posZ;
                this.motionX = x * speed;
                this.motionY = y * speed;
                this.motionZ = z * speed;
                double size = 0.25F;
                if (check(this.shootingEntity.posX, this.posX, size) && check(yEntity, this.posY, size) && check(this.shootingEntity.posZ, this.posZ, size)) {
                    EntityItem arrowStack = new EntityItem(this.world, this.posX, this.posY, this.posZ, this.getArrowStack());
                    arrowStack.setNoPickupDelay();
                    arrowStack.setNoDespawn();
                    this.setDead();
                }
            }
        }
        else if (this.loyaltyLvl > 0 && this.inGround) {
            this.moveLoyalty = true;
            // Set block air.
            this.inGround = false;
            this.ticksInGround = 0;
            this.inTile = Blocks.AIR;
            this.xTile = -1;
            this.yTile = -1;
            this.zTile = -1;
            this.inData = 0;
        }
    }

    // TODO Maybe add it to WorldHelper
    private boolean check(double pos, double pos2, double size) {
        return pos2 >= pos - size && pos2 <= pos + size;
    }

    @Override
    public DamageSource getDamageSource() {
        return new EntityDamageSourceIndirect("trident", this, this.shootingEntity == null ? this : this.shootingEntity);
    }

    @Nonnull
    @Override
    protected ItemStack getArrowStack() {
        ItemStack stack = new ItemStack(ModItems.TRIDENT, 1, this.damage);
        if (!this.tag.isEmpty()) stack.setTagCompound(this.tag);
        return stack;
    }

    @Override
    protected void arrowHit(@Nonnull EntityLivingBase living) {
        if (this.loyaltyLvl == 0) {
            this.motionX *= -0.01F;
            this.motionY = 0F;
            this.motionZ *= -0.01F;
        }
    }

    @Override
    public boolean getIsCritical() {
        return false;
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("damage", this.damage);
        compound.setTag("tag", this.tag);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.damage = compound.getInteger("damage");
        this.tag = compound.getCompoundTag("tag");
    }

    private void initEnchLevels() {
        if (this.impalingLvl >= 0) return;
        NBTTagList list = this.tag == null ? null : this.tag.getTagList("ench", Constants.NBT.TAG_COMPOUND);
        if (list != null) {
            int impaling = Enchantment.getEnchantmentID(ModEnchantments.IMPALING);
            int loyalty = Enchantment.getEnchantmentID(ModEnchantments.LOYALTY);
            int channeling = Enchantment.getEnchantmentID(ModEnchantments.CHANNELING);

            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound enchCompound = list.getCompoundTagAt(i);
                short id = enchCompound.getShort("id");
                short lvl = enchCompound.getShort("lvl");

                if (id == impaling) {
                    impalingLvl = lvl;
                }
                else if (id == loyalty) {
                    this.loyaltyLvl = lvl;
                }
                else if (id == channeling) {
                    this.channelingLvl = lvl;
                }
            }
        }

        if (this.impalingLvl == -1) this.impalingLvl = 0;
        if (this.loyaltyLvl == -1) this.loyaltyLvl = 0;
        if (this.channelingLvl == -1) this.channelingLvl = 0;
    }

    @Override
    public float getHitDamage(Entity entity, float original) {
        float add = 0F;
        if (this.impalingLvl > 0 && (entity instanceof EntityWaterMob || entity instanceof EntityGuardian)) {
            add += 2.5F * this.impalingLvl;
        }
        return 9 + add;
    }

    @Override
    public boolean shouldDieAfterHit(Entity hitEntity) {
        return false;
    }

    @Override
    public boolean shouldHitEntity(Entity entity) {
        return !this.moveLoyalty || this.shootingEntity != entity;
    }

    @Override
    public boolean shouldHitBlock(BlockPos pos, IBlockState state) {
        return !this.moveLoyalty;
    }

    @Override
    protected void doBlockCollisions() {
        if (!this.moveLoyalty) super.doBlockCollisions();
    }

    @Override
    public float getSpeedChangeInWater() {
        return 1.0F;
    }

    @Override
    public SoundEvent getEntityHitSound(Entity entity) {
        return super.getEntityHitSound(entity);
    }

    @Override
    public SoundEvent getBlockHitSound(BlockPos pos, IBlockState state) {
        return super.getBlockHitSound(pos, state);
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (!this.world.isRemote && this.channelingLvl > 0 && this.world.isThundering() && !entity.isInWater()) {
            this.world.addWeatherEffect(new EntityLightningBolt(this.world, entity.posX, entity.posY, entity.posZ, false));
        }
    }
}
