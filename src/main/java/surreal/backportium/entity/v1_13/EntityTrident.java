package surreal.backportium.entity.v1_13;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import surreal.backportium.api.enums.ModCreatureAttributes;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.item.ModItems;
import surreal.backportium.sound.ModSounds;

import javax.annotation.Nonnull;

public class EntityTrident extends EntityArrow {

    protected NBTTagCompound stackTag; // We're throwing the item. Not using the item to throw things like Bow and Arrow

    private boolean moveLoyalty; // If it moves because of loyalty

    private int damage;

    private int impalingLvl = 0;
    private int loyaltyLvl = 0;

    public EntityTrident(World worldIn) {
        super(worldIn);
    }

    public EntityTrident(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityTrident(World worldIn, EntityLivingBase shooter, ItemStack stack) {
        super(worldIn, shooter);
        this.stackTag = stack.getTagCompound();
        this.damage = stack.getItemDamage();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.moveLoyalty) {
            this.inGround = false;
            float speed = 0.25F;
            if (this.shootingEntity.posX < this.posX) {
                this.motionX = -speed;
            }
            else if (this.shootingEntity.posX > this.posX) {
                this.motionX = speed;
            }

            if (this.shootingEntity.posZ < this.posZ) {
                this.motionZ = -speed;
            }
            else if (this.shootingEntity.posZ > this.posZ) {
                this.motionZ = speed;
            }

            if (this.shootingEntity.posY < this.posY) {
                this.motionY = -speed;
            }
            else if (this.shootingEntity.posY > this.posY) {
                this.motionY = speed;
            }
        }
        else if (this.loyaltyLvl > 0 && this.inGround) {
            this.moveLoyalty = true;
        }
    }

    @Override
    protected void doBlockCollisions() {
        if (!moveLoyalty) super.doBlockCollisions();
    }

    //    @Nullable
//    @Override
//    public EntityItem entityDropItem(ItemStack stack, float offsetY) {
//        if (stack.getItemDamage())
//    }

    @Nonnull
    @Override
    protected ItemStack getArrowStack() {
        ItemStack stack = new ItemStack(ModItems.TRIDENT, 1, damage);
        stack.setTagCompound(stackTag);
        return stack;
    }

    @Override
    protected void arrowHit(@Nonnull EntityLivingBase living) {
        if (loyaltyLvl == 0) {
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
        compound.setTag("stackTag", stackTag);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        stackTag = compound.getCompoundTag("stackTag");
    }

    // @Override
    protected float getHitDamage(Entity entity, float original) {
        if (!(entity instanceof EntityLivingBase)) return 9;
        EntityLivingBase living = (EntityLivingBase) entity;

        initEnchLevels();

        float add = 0;

        if (living.getCreatureAttribute() == ModCreatureAttributes.AQUATIC) add += 2.5F * impalingLvl;

        return 9 + add;
    }

    private void initEnchLevels() {
        NBTTagList list = stackTag == null ? null : stackTag.getTagList("ench", Constants.NBT.TAG_COMPOUND);
        if (list != null) {
            int impaling = Enchantment.getEnchantmentID(ModEnchantments.IMPALING);
            int loyalty = Enchantment.getEnchantmentID(ModEnchantments.LOYALTY);

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
            }
        }
    }

    // @Override
    protected boolean shouldDieAfterHit(Entity hitEntity) {
        return false;
    }

    // @Override
    protected float getSpeedChangeInWater() {
        return 1.0F;
    }

    // @Override
    protected SoundEvent getEntityHitSound() {
        return ModSounds.ITEM_TRIDENT_HIT;
    }

    // @Override
    protected SoundEvent getBlockHitSound() {
        return ModSounds.ITEM_TRIDENT_HIT_GROUND;
    }
}
