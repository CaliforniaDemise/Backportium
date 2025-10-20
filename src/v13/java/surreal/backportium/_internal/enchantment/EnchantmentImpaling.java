package surreal.backportium._internal.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import surreal.backportium.init.ModEnchantmentTypes;
import surreal.backportium.init.ModEnchantments;
import surreal.backportium.tag.AllTags;

public class EnchantmentImpaling extends EnchantmentDefault {

    public EnchantmentImpaling() {
        super(Rarity.RARE, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    public static float handle(float damage, ItemStack stack) {
        int impalingLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.IMPALING, stack);
        return impalingLevel == 0 ? damage : damage + 2.5F * impalingLevel;
    }

    public static float handle(float damage, int level) {
        return damage + 2.5F * level;
    }

    public static boolean canApplyTo(Entity target) {
        return AllTags.ENTITY_TAG.contains(AllTags.ENTITY_IMPALING_WHITELIST, target);
    }
}
