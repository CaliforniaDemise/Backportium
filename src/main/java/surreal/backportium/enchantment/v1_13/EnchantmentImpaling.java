package surreal.backportium.enchantment.v1_13;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import surreal.backportium.api.enums.ModCreatureAttributes;
import surreal.backportium.api.enums.ModEnchantmentTypes;
import surreal.backportium.enchantment.EnchantmentDef;

import javax.annotation.Nonnull;

// TODO Damage entities in water, which needs an ASM.
public class EnchantmentImpaling extends EnchantmentDef {

    public EnchantmentImpaling(Rarity rarityIn) {
        super(rarityIn, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float calcDamageByCreature(int level, @Nonnull EnumCreatureAttribute creatureType) {
        return creatureType == ModCreatureAttributes.AQUATIC ? 2.5F * level : 0.0F;
    }
}
