package surreal.backportium.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.enchantment.v1_13.EnchantmentChanneling;
import surreal.backportium.enchantment.v1_13.EnchantmentImpaling;
import surreal.backportium.enchantment.v1_13.EnchantmentLoyalty;
import surreal.backportium.enchantment.v1_13.EnchantmentRiptide;
import surreal.backportium.util.Registrar;

public class ModEnchantments extends Registrar<Enchantment> {

    @ObjectHolder("backportium:impaling") public static final Enchantment IMPALING = null;
    @ObjectHolder("backportium:loyalty") public static final Enchantment LOYALTY = null;
    @ObjectHolder("backportium:riptide") public static final Enchantment RIPTIDE = null;
    @ObjectHolder("backportium:channeling") public static final Enchantment CHANNELING = null;

    public ModEnchantments() {
        super(4);
        this.register();
    }

    @Override
    protected Enchantment register(@NotNull Enchantment entry, @NotNull ResourceLocation location) {
        return super.register(entry, location).setRegistryName(location).setName(location.getPath());
    }

    private void register() {
        this.register(new EnchantmentImpaling(Enchantment.Rarity.RARE), "impaling");
        this.register(new EnchantmentLoyalty(Enchantment.Rarity.RARE), "loyalty");
        this.register(new EnchantmentRiptide(Enchantment.Rarity.RARE), "riptide");
        this.register(new EnchantmentChanneling(Enchantment.Rarity.RARE), "channeling");
    }
}
