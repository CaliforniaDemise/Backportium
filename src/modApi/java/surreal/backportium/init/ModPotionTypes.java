package surreal.backportium.init;

import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import surreal.backportium.Tags;

public class ModPotionTypes {

    // 1.13
    public static PotionType SLOW_FALLING;
    public static PotionType LONG_SLOW_FALLING;
    public static PotionType TURTLE_MASTER;
    public static PotionType LONG_TURTLE_MASTER;
    public static PotionType STRONG_TURTLE_MASTER;

    public static void init() {
        SLOW_FALLING = potionType("slow_falling");
        LONG_SLOW_FALLING = potionType("long_slow_falling");
        TURTLE_MASTER = potionType("turtle_master");
        LONG_TURTLE_MASTER = potionType("long_turtle_master");
        STRONG_TURTLE_MASTER = potionType("strong_turtle_master");
    }

    private static PotionType potionType(String name) {
        return ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(Tags.MOD_ID, name));
    }
}
