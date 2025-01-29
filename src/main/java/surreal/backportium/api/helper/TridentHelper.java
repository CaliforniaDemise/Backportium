package surreal.backportium.api.helper;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.sound.ModSounds;

import java.lang.reflect.Method;

public class TridentHelper {

    // Impaling
    public static float handleImpaling(float damage, ItemStack stack) {
        int impalingLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.IMPALING, stack);
        return impalingLevel == 0 ? damage : damage + 2.5F * impalingLevel;
    }

    public static float handleImpaling(float damage, int level) {
        return damage + 2.5F * level;
    }

    public static boolean canImpale(Entity target) {
        return target instanceof EntityWaterMob || target instanceof EntityGuardian;
    }

    // Riptide //
    private static final Method HANDLE_RIPTIDE, IS_IN_RIPTIDE, GET_RIPTIDE_TICK;

    @NotNull
    public static SoundEvent getRiptideSound(int level) {
        switch (level) {
            case 2: return ModSounds.ITEM_TRIDENT_RIPTIDE_2;
            case 3: return ModSounds.ITEM_TRIDENT_RIPTIDE_3;
            default: return ModSounds.ITEM_TRIDENT_RIPTIDE_1;
        }
    }

    public static int getRiptideLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);
    }

    public static boolean canRiptide(World world, EntityLivingBase entity) {
        return entity.isInWater() || world.isRainingAt(new BlockPos(entity).up());
    }

    // Reflection
    public static void handleRiptide(EntityLivingBase entity, ItemStack stack) {
        try {
            HANDLE_RIPTIDE.invoke(entity, stack);
        }
        catch (Exception e) { throw new RuntimeException("Could not handle riptide on " + entity + " with " + stack, e); }
    }

    public static boolean isInRiptide(EntityLivingBase entity) {
        try {
            return (boolean) IS_IN_RIPTIDE.invoke(entity);
        }
        catch (Exception e) { throw new RuntimeException("Unexpected issue occurred while trying to check if " + entity + " is in riptide", e); }
    }

    public static int getRiptideTickLeft(EntityLivingBase entity) {
        try {
            return (int) GET_RIPTIDE_TICK.invoke(entity);
        }
        catch (Exception e) { throw new RuntimeException("Unexpected issue occurred while trying to gather the riptide time of " + entity, e); }
    }

    static {
        try {
            HANDLE_RIPTIDE = EntityLivingBase.class.getMethod("handleRiptide", ItemStack.class);
            IS_IN_RIPTIDE = EntityLivingBase.class.getMethod("isInRiptide");
            GET_RIPTIDE_TICK = EntityLivingBase.class.getMethod("getRiptideTickLeft");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
