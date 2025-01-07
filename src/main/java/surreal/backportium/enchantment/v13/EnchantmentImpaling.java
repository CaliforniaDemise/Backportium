package surreal.backportium.enchantment.v13;

import net.minecraft.inventory.EntityEquipmentSlot;
import surreal.backportium.api.enums.ModEnchantmentTypes;
import surreal.backportium.enchantment.EnchantmentDef;

public class EnchantmentImpaling extends EnchantmentDef {

    public EnchantmentImpaling(Rarity rarityIn) {
        super(rarityIn, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
