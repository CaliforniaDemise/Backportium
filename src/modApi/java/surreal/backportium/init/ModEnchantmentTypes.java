package surreal.backportium.init;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.common.util.EnumHelper;
import surreal.backportium.api.item.Trident;

public class ModEnchantmentTypes {

    public static final EnumEnchantmentType TRIDENT = EnumHelper.addEnchantmentType("trident", item -> item instanceof Trident);
}
