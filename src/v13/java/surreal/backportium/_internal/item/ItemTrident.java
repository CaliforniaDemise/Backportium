package surreal.backportium._internal.item;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium._internal.client.renderer.item.ItemRender;
import surreal.backportium._internal.client.renderer.item.ModTEISR;
import surreal.backportium._internal.enchantment.EnchantmentImpaling;
import surreal.backportium._internal.enchantment.EnchantmentRiptide;
import surreal.backportium._internal.entity.EntityTrident;
import surreal.backportium.api.entity.RiptideEntity;
import surreal.backportium.api.item.Trident;
import surreal.backportium.client.entity.render.RenderTrident;
import surreal.backportium.init.ModActions;
import surreal.backportium.init.ModEnchantments;
import surreal.backportium.init.ModSounds;

public class ItemTrident extends Item implements Trident, ItemRender {

    @SideOnly(Side.CLIENT)
    private EntityTrident TRIDENT = new EntityTrident(Minecraft.getMinecraft().world);

    public static final IItemPropertyGetter TRIDENT_MODE = (stack, worldIn, entityIn) -> {
        if (worldIn == null || entityIn == null) return -1F;
        if (entityIn.getActiveItemStack().getItem() instanceof ItemTrident) {
            int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);
            if (riptide != 0 && !EnchantmentRiptide.canRiptide(worldIn, entityIn)) {
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
        if (FMLLaunchHandler.side().isClient()) {
            this.setTileEntityItemStackRenderer(ModTEISR.instance());
        }
    }

    @Override
    public void setTileEntityItemStackRenderer(@Nullable TileEntityItemStackRenderer teisr) {
        super.setTileEntityItemStackRenderer(teisr);
    }

    @Override
    @NotNull
    public IRarity getForgeRarity(@NotNull ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, EntityPlayer playerIn, @NotNull EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);
        if (riptide > 0 && !(playerIn.isInWater() || worldIn.isRainingAt(playerIn.getPosition()))) {
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        }
        playerIn.setActiveHand(handIn);
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(@NotNull ItemStack stack, @NotNull World worldIn, @NotNull EntityLivingBase entityLiving, int timeLeft) {
        float velocity = (ItemBow.getArrowVelocity(this.getMaxItemUseDuration(stack) - timeLeft) / 5) * 4;
        if (velocity > 0.3F) {
            int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);
            if (riptide != 0) {
                if (EnchantmentRiptide.canRiptide(worldIn, entityLiving)) {
                    this.handleRiptide(entityLiving, stack);
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

    public void handleRiptide(EntityLivingBase entity, ItemStack stack) {
        int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);
        Vec3d look = entity.getLookVec();
        double speed = 1.0 + 0.8 * (double) level;
        entity.motionX += speed * look.x;
        entity.motionY += speed * look.y;
        entity.motionZ += speed * look.z;
        RiptideEntity riptide = RiptideEntity.cast(entity);
        riptide.setRiptideTimeLeft(20);
        SoundEvent sound = EnchantmentRiptide.getRiptideSound(level);
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, sound, entity.getSoundCategory(), 1.0F, 1.0F);
        riptide.riptideStart(stack);
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @NotNull
    @Override
    public EnumAction getItemUseAction(@NotNull ItemStack stack) {
        EnumAction action = ModActions.TRIDENT;
        if (action == null) action = EnumAction.BOW;
        return action;
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull EntityPlayer player, @NotNull Entity entity) {
        int impaling = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.IMPALING, stack);
        if (impaling > 0 && EnchantmentImpaling.canApplyTo(entity)) {
            float damageAdd = EnchantmentImpaling.handle(0.0F, impaling);
            entity.attackEntityFrom(DamageSource.causePlayerDamage(player), damageAdd);
        }
        return false;
    }

    @NotNull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@NotNull EntityEquipmentSlot equipmentSlot, @NotNull ItemStack stack) {
        Multimap<String, AttributeModifier> map = super.getAttributeModifiers(equipmentSlot, stack);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon Modifier", 8F, 0));
            map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon Modifier", -2.9F, 0));
        }

        return map;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void render(ItemStack stack, float partialTicks) {
        if (TRIDENT.getItem() != stack) {
            TRIDENT.setItem(stack);
        }
        Minecraft mc = Minecraft.getMinecraft();
        RenderTrident<EntityTrident> render = (RenderTrident) mc.getRenderManager().getEntityRenderObject(TRIDENT);
        if (render != null) {
            GlStateManager.pushMatrix();
            render.renderStack(TRIDENT);
            GlStateManager.popMatrix();
        }
    }
}