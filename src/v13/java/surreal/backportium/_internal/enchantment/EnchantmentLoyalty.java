package surreal.backportium._internal.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import surreal.backportium.init.ModEnchantmentTypes;
import surreal.backportium.init.ModEnchantments;

import javax.annotation.Nonnull;

public class EnchantmentLoyalty extends EnchantmentDefault {

    public EnchantmentLoyalty() {
        super(Rarity.RARE, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    protected boolean canApplyTogether(@Nonnull Enchantment ench) {
        return ench != ModEnchantments.RIPTIDE;
    }
}
