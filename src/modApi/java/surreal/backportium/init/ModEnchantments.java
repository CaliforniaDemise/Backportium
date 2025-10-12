package surreal.backportium.init;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import surreal.backportium.Tags;

public class ModEnchantments {

    // 1.13
    public static Enchantment CHANNELING;
    public static Enchantment IMPALING;
    public static Enchantment LOYALTY;
    public static Enchantment RIPTIDE;

    // 1.21
    public static Enchantment BREACH;
    public static Enchantment DENSITY;
    public static Enchantment WIND_BURST;

    public static void init() {
        CHANNELING = enchantment("channeling");
        IMPALING = enchantment("impaling");
        LOYALTY = enchantment("loyalty");
        RIPTIDE = enchantment("riptide");

        BREACH = enchantment("breach");
        DENSITY = enchantment("density");
        WIND_BURST = enchantment("wind_burst");
    }

    private static Enchantment enchantment(String name) {
        return ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(Tags.MOD_ID, name));
    }
}
