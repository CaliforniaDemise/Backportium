package surreal.backportium.enchantment.v13;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import surreal.backportium.api.enums.ModEnchantmentTypes;
import surreal.backportium.enchantment.EnchantmentDef;
import surreal.backportium.enchantment.ModEnchantments;

import javax.annotation.Nonnull;

public class EnchantmentChanneling extends EnchantmentDef {

    public EnchantmentChanneling(Rarity rarityIn) {
        super(rarityIn, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    }

    @Override
    protected boolean canApplyTogether(@Nonnull Enchantment ench) {
        return ench != ModEnchantments.RIPTIDE;
    }
}
