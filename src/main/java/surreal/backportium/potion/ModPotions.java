package surreal.backportium.potion;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import surreal.backportium.Tags;
import surreal.backportium.potion.v1_13.PotionConduitPower;
import surreal.backportium.potion.v1_13.PotionSlowFalling;

import java.util.ArrayList;
import java.util.List;

public class ModPotions {

    private static final List<Potion> POTIONS = new ArrayList<>();
    private static final List<PotionType> POTION_TYPES = new ArrayList<>();

    public static final PotionSlowFalling SLOW_FALLING = register(new PotionSlowFalling(false, 0xF3CFB9), "slow_falling");
    public static final PotionConduitPower CONDUIT_POWER = register(new PotionConduitPower(false, 0x1DC2D1), "conduit_power");

    public static final PotionType SLOW_FALLING_TYPE = register(new PotionType(new PotionEffect(SLOW_FALLING, 1800, 1)), "slow_falling");
    public static final PotionType LONG_SLOW_FALLING_TYPE = register(new PotionType(new PotionEffect(SLOW_FALLING, 4800, 1)), "long_slow_falling");

    public static final PotionType TURTLE_MASTER_TYPE = register(new PotionType(new PotionEffect(MobEffects.SLOWNESS, 1200, 4), new PotionEffect(MobEffects.STRENGTH, 1200, 3)), "turtle_master");
    public static final PotionType LONG_TURTLE_MASTER_TYPE = register(new PotionType(new PotionEffect(MobEffects.SLOWNESS, 3600, 4), new PotionEffect(MobEffects.STRENGTH, 3600, 3)), "long_turtle_master");
    public static final PotionType STRONG_TURTLE_MASTER_TYPE = register(new PotionType(new PotionEffect(MobEffects.SLOWNESS, 1200, 6), new PotionEffect(MobEffects.STRENGTH, 1200, 4)), "strong_turtle_master");

    public static <T extends Potion> T register(T potion, String name) {
        potion.setPotionName(name).setRegistryName(Tags.MOD_ID, name);
        POTIONS.add(potion);
        return potion;
    }

    public static <T extends PotionType> T register(T potionType, String name) {
        potionType.setRegistryName(Tags.MOD_ID, name);
        POTION_TYPES.add(potionType);
        return potionType;
    }

    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        IForgeRegistry<Potion> registry = event.getRegistry();
        POTIONS.forEach(registry::register);
    }

    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        IForgeRegistry<PotionType> registry = event.getRegistry();
        POTION_TYPES.forEach(registry::register);
    }
}
