package surreal.backportium.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import surreal.backportium.Tags;
import surreal.backportium._internal.potion.PotionBasic;

public class ModPotions {

    // 1.13
    public static PotionBasic CONDUIT_POWER;
    public static PotionBasic DOLPHINS_GRACE;
    public static PotionBasic SLOW_FALLING;

    // 1.14
    public static PotionBasic BAD_OMEN;
    public static PotionBasic HERO_OF_THE_VILLAGE;

    // 1.21
    public static PotionBasic INFESTED;
    public static PotionBasic OOZING;
    public static PotionBasic RAID_OMEN;
    public static PotionBasic TRIAL_OMEN;
    public static PotionBasic WEAVING;
    public static PotionBasic WIND_CHARGED;

    public static void init() {
        CONDUIT_POWER = potion("conduit_power");
        DOLPHINS_GRACE = potion("dolphins_grace");
        SLOW_FALLING = potion("slow_falling");
    }

    @SuppressWarnings("unchecked")
    public static <T extends PotionBasic> T potion(String name) {
        return (T) ForgeRegistries.POTIONS.getValue(new ResourceLocation(Tags.MOD_ID, name));
    }
}
