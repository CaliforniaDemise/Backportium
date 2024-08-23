package surreal.backportium.enchantment.v1_13;

import net.minecraft.inventory.EntityEquipmentSlot;
import surreal.backportium.api.enums.ModEnchantmentTypes;
import surreal.backportium.enchantment.EnchantmentDef;

public class EnchantmentLoyalty extends EnchantmentDef {

    public EnchantmentLoyalty(Rarity rarityIn) {
        super(rarityIn, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
