package surreal.backportium.api.enums;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.common.util.EnumHelper;
import surreal.backportium.item.v1_13.ItemTrident;

public class ModEnchantmentTypes {

    public static final EnumEnchantmentType
            TRIDENT;

    static {
        TRIDENT = EnumHelper.addEnchantmentType("trident", item -> item instanceof ItemTrident);
    }
}
