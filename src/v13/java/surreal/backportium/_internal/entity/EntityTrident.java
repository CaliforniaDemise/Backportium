package surreal.backportium._internal.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.enchantment.EnchantmentImpaling;
import surreal.backportium.client.entity.model.ModelTrident;
import surreal.backportium.client.entity.render.RenderTrident;
import surreal.backportium.entity.EntityItemThrowable;
import surreal.backportium.init.ModEnchantments;
import surreal.backportium.init.ModSounds;

import java.util.Map;

public class EntityTrident extends EntityItemThrowable implements RenderProvider {

    private int loyaltyLvl;
    private int impalingLvl;
    private int channelingLvl;

    private boolean moveLoyalty;

    public EntityTrident(World worldIn) {
        super(worldIn);
    }

    public EntityTrident(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z);
        this.setItem(stack);
    }

    public EntityTrident(World worldIn, EntityLivingBase shooter, ItemStack stack) {
        super(worldIn, shooter);
        this.setItem(stack);
    }

    @Override
    public void setItem(@NotNull ItemStack stack) {
        super.setItem(stack);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        this.loyaltyLvl = map.getOrDefault(ModEnchantments.LOYALTY, 0);
        this.impalingLvl = map.getOrDefault(ModEnchantments.IMPALING, 0);
        this.channelingLvl = map.getOrDefault(ModEnchantments.CHANNELING, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.moveLoyalty) {
            if (!this.world.isRemote) {
                double yEntity = this.shootingEntity.posY + this.shootingEntity.getEyeHeight() - 0.25D;
                this.prevRotationPitch = 180F;
                this.rotationPitch = 180F;
                double speed = 0.5D + loyaltyLvl * 0.05D;
                double x = this.shootingEntity.posX - this.posX;
                double y = yEntity - this.posY;
                double z = this.shootingEntity.posZ - this.posZ;
                double dotSqrt = Math.sqrt(x * x + y * y + z * z);
                if (dotSqrt != 0.0D) {
                    x /= dotSqrt;
                    y /= dotSqrt;
                    z /= dotSqrt;
                }
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
        else if (this.shootingEntity != null && this.loyaltyLvl > 0 && this.inGround && this.ticksInGround == 20.0F / this.loyaltyLvl) {
            if (!world.isRemote) this.world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.ITEM_TRIDENT_RETURN, SoundCategory.NEUTRAL, 8.0F, 1.0F);
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

    private boolean check(double pos, double pos2, double size) {
        return pos2 >= pos - size && pos2 <= pos + size;
    }

    @Override
    public DamageSource getDamageSource() {
        return new EntityDamageSourceIndirect("trident", this, this.shootingEntity == null ? this : this.shootingEntity);
    }

    @Override
    protected void arrowHit(@NotNull EntityLivingBase living) {
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
    public float getHitDamage(Entity entity, float original) {
        float add = 0F;
        if (this.impalingLvl > 0 && EnchantmentImpaling.canImpale(entity)) add = EnchantmentImpaling.handleImpaling(add, this.impalingLvl);
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
        return ModSounds.ITEM_TRIDENT_HIT;
    }

    @Override
    public SoundEvent getBlockHitSound(BlockPos pos, IBlockState state) {
        return ModSounds.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    protected void onHitEntity(Entity entity) {
        if (!this.world.isRemote && this.channelingLvl > 0 && this.world.isThundering() && !entity.isInWater()) {
            this.world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.ITEM_TRIDENT_THUNDER, SoundCategory.NEUTRAL, 5.0F, 1.0F);
            this.world.addWeatherEffect(new EntityLightningBolt(this.world, entity.posX, entity.posY, entity.posZ, false));
        }
    }

    @Override
    public void bindRender() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTrident.class, m -> new RenderTrident<>(m, new ModelTrident()));
    }
}