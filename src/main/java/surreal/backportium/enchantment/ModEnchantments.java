package surreal.backportium.enchantment;


import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import surreal.backportium.Tags;
import surreal.backportium.enchantment.v1_13.EnchantmentImpaling;
import surreal.backportium.enchantment.v1_13.EnchantmentLoyalty;
import surreal.backportium.enchantment.v1_13.EnchantmentRiptide;

import java.util.ArrayList;
import java.util.List;

public class ModEnchantments {

    private static final List<Enchantment> ENCHANTMENTS = new ArrayList<>();

    public static final Enchantment IMPALING = register(new EnchantmentImpaling(Enchantment.Rarity.RARE), "impaling");
    public static final Enchantment LOYALTY = register(new EnchantmentLoyalty(Enchantment.Rarity.RARE), "loyalty");
    public static final Enchantment RIPTIDE = register(new EnchantmentRiptide(Enchantment.Rarity.RARE), "riptide");

    private static Enchantment register(Enchantment enchantment, String name) {
        enchantment.setName(name).setRegistryName(Tags.MOD_ID, name);
        ENCHANTMENTS.add(enchantment);
        return enchantment;
    }

    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        IForgeRegistry<Enchantment> registry = event.getRegistry();
        ENCHANTMENTS.forEach(registry::register);
    }
}
