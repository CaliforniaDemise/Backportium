package surreal.backportium.api.enums;

import net.minecraft.item.ItemArmor;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import surreal.backportium.sound.ModSounds;

public class ModArmorMaterials {

    public static final ItemArmor.ArmorMaterial
        TURTLE_SHELL;

    public static void register() {}

    private static ItemArmor.ArmorMaterial register(String name, int durability, int enchantability, float toughness, SoundEvent soundOnEquip, int... reductionAmounts) {
        return EnumHelper.addArmorMaterial(name, name, durability, reductionAmounts, enchantability, soundOnEquip, toughness);
    }

    static {
        TURTLE_SHELL = register("turtle_shell", 275, 9, 0F, ModSounds.ITEM_ARMOR_EQUIP_TURTLE, 2, 5, 6, 2);
    }
}
