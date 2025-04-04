package surreal.backportium.item.v13;

import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import surreal.backportium.Backportium;
import surreal.backportium.api.helper.TridentHelper;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.entity.v13.EntityTrident;
import surreal.backportium.item.ItemTEISR;
import surreal.backportium.sound.ModSounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemTrident extends ItemTEISR {

    public static final IItemPropertyGetter TRIDENT_MODE = (stack, worldIn, entityIn) -> {
        if (worldIn == null || entityIn == null) return -1F;
        if (entityIn.getActiveItemStack().getItem() instanceof ItemTrident) {
            int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);
            if (riptide != 0 && !TridentHelper.canRiptide(worldIn, entityIn)) {
                return 0F;
            }
            return 1F;
        }
        return 0F;
    };

    public ItemTrident() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.addPropertyOverride(new ResourceLocation("throwing"), TRIDENT_MODE);
    }

    @Override
    public void setTileEntityItemStackRenderer(@Nullable TileEntityItemStackRenderer teisr) {
        super.setTileEntityItemStackRenderer(teisr);
    }

    @Override
    @Nonnull
    public IRarity getForgeRarity(@Nonnull ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);

        if (riptide > 0 && !(playerIn.isInWater() || worldIn.isRainingAt(playerIn.getPosition()))) {
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        }

        playerIn.setActiveHand(handIn);
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        float velocity = (ItemBow.getArrowVelocity(this.getMaxItemUseDuration(stack) - timeLeft) / 5) * 4;
        if (velocity > 0.3F) {
            int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);
            if (riptide != 0) {
                if (TridentHelper.canRiptide(worldIn, entityLiving)) {
                    TridentHelper.handleRiptide(entityLiving, stack);
                    entityLiving.setPosition(entityLiving.posX, entityLiving.posY + 1.0D, entityLiving.posZ);
                    stack.damageItem(1, entityLiving);
                }
                return;
            }
            if (!worldIn.isRemote && stack.getItemDamage() != stack.getMaxDamage() - 1) {
                stack.damageItem(1, entityLiving);
                EntityTrident trident = new EntityTrident(worldIn, entityLiving, stack);
                trident.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, velocity * 3F, 1F);

                worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, ModSounds.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

                boolean infinite = false;

                if (entityLiving instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entityLiving;
                    if (player.isCreative()) {
                        infinite = true;
                        trident.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    }

                    player.addStat(StatList.getObjectUseStats(this));
                    worldIn.spawnEntity(trident);
                } else infinite = true;

                if (!infinite) stack.shrink(1);
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
        return 72000;
    }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
        EnumAction action = Backportium.SPEAR;
        if (action == null) action = EnumAction.BOW;
        return action;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        int impaling = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.IMPALING, stack);
        if (impaling > 0 && TridentHelper.canImpale(entity)) {
            float damageAdd = TridentHelper.handleImpaling(0.0F, impaling);
            entity.attackEntityFrom(DamageSource.causePlayerDamage(player), damageAdd);
        }
        return false;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = super.getAttributeModifiers(equipmentSlot, stack);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon Modifier", 8F, 0));
            map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon Modifier", -2.9F, 0));
        }

        return map;
    }
}
