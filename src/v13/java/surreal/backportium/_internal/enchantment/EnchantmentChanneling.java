package surreal.backportium._internal.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import surreal.backportium.init.ModEnchantmentTypes;
import surreal.backportium.init.ModEnchantments;

import javax.annotation.Nonnull;

public class EnchantmentChanneling extends EnchantmentDefault {

    public EnchantmentChanneling() {
        super(Rarity.RARE, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    }

    @Override
    protected boolean canApplyTogether(@Nonnull Enchantment ench) {
        return ench != ModEnchantments.RIPTIDE;
    }
}
