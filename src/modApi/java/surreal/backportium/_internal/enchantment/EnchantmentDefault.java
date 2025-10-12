package surreal.backportium._internal.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class EnchantmentDefault extends Enchantment {

    public EnchantmentDefault(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @NotNull
    @Override
    public Enchantment setName(@NotNull String enchName) {
        return super.setName("enchanment." + enchName + ".name");
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }
}
