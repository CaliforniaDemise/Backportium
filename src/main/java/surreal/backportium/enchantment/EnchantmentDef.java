package surreal.backportium.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

import javax.annotation.Nonnull;

public abstract class EnchantmentDef extends Enchantment {

    public EnchantmentDef(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @Nonnull
    @Override
    public Enchantment setName(@Nonnull String enchName) {
        this.name = "enchantment." + enchName;
        return this;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }
}
