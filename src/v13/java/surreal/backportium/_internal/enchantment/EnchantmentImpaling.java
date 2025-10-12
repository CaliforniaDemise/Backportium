package surreal.backportium._internal.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import surreal.backportium.init.ModEnchantmentTypes;
import surreal.backportium.init.ModEnchantments;

public class EnchantmentImpaling extends EnchantmentDefault {

    public EnchantmentImpaling() {
        super(Rarity.RARE, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

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
}
