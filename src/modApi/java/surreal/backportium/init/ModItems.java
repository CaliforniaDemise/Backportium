package surreal.backportium.init;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import surreal.backportium.Tags;

public class ModItems {

    // 1.13
    public static Item EXPLORER_MAP;
    public static Item DEBUG_STICK;
    public static Item DRIED_KELP;
    public static Item MOB_BUCKET;
    public static Item SEA_HEART;
    public static Item NAUTILUS_SHELL;
    public static Item PHANTOM_MEMBRANE;
    public static Item TURTLE_SCUTE;
    public static Item TRIDENT;
    public static Item TURTLE_SHELL;

    // 1.14
    public static Item BANNER_PATTERN;
    public static Item CROSSBOW;
    public static Item BLUE_DYE;
    public static Item BROWN_DYE;
    public static Item BLACK_DYE;
    public static Item WHITE_DYE;
    public static Item LEATHER_HORSE_ARMOR;
    public static Item SUSPICIOUS_STEW;

    // 1.15
    public static Item HONEY_BOTTLE;
    public static Item HONEYCOMB;

    // 1.16
    public static Item LODESTONE_COMPASS;
    public static Item PIGSTEP;
    public static Item NETHERITE_HELMET;
    public static Item NETHERITE_CHESTPLATE;
    public static Item NETHERITE_LEGGINGS;
    public static Item NETHERITE_BOOTS;
    public static Item NETHERITE_AXE;
    public static Item NETHERITE_HOE;
    public static Item NETHERITE_PICKAXE;
    public static Item NETHERITE_SHOVEL;
    public static Item NETHERITE_SWORD;
    public static Item NETHERITE_INGOT;
    public static Item NETHERITE_SCRAP;
    public static Item WARPED_FUNGUS_STICK;

    // 1.17
    public static Item AMETHYST_SHARD;
    public static Item BUNDLE;
    public static Item COPPER_INGOT;
    public static Item GLOW_BERRIES;
    public static Item GLOW_INK_SAC;
    public static Item POWDER_SNOW_BUCKET;
    public static Item RAW_IRON;
    public static Item RAW_GOLD;
    public static Item RAW_COPPER;
    public static Item SPYGLASS;

    // 1.18
    public static Item OTHERSIDE;

    // 1.19
    public static Item DISC_FRAGMENT;
    public static Item ECHO_SHARD;
    public static Item GOAT_HORN;
    public static Item MANGROVE_BOAT;
    public static Item MUSIC_DISC_5;
    public static Item RECOVERY_COMPASS;

    // 1.20
    public static Item BRUSH;
    public static Item RELIC;
    public static Item ANGLER_POTTERY_SHERD;
    public static Item ARCHER_POTTERY_SHERD;
    public static Item ARMS_UP_POTTERY_SHERD;
    public static Item BLADE_POTTERY_SHERD;
    public static Item BREWER_POTTERY_SHERD;
    public static Item BURN_POTTERY_SHERD;
    public static Item DANGER_POTTERY_SHERD;
    public static Item EXPLORER_POTTERY_SHERD;
    public static Item FRIEND_POTTERY_SHERD;
    public static Item HEART_POTTERY_SHERD;
    public static Item HEARTBREAK_POTTERY_SHERD;
    public static Item HOWL_POTTERY_SHERD;
    public static Item MINER_POTTERY_SHERD;
    public static Item MOURNER_POTTERY_SHERD;
    public static Item PLENTY_POTTERY_SHERD;
    public static Item PRIZE_POTTERY_SHERD;
    public static Item SHEAF_POTTERY_SHERD;
    public static Item SHELTER_POTTERY_SHERD;
    public static Item SKULL_POTTERY_SHERD;
    public static Item SNORT_POTTERY_SHERD;
    public static Item NETHERITE_UPGRADE;
    public static Item COAST_ARMOR_TRIM;
    public static Item DUNE_ARMOR_TRIM;
    public static Item EYE_ARMOR_TRIM;
    public static Item HOST_ARMOR_TRIM;
    public static Item RAISER_ARMOR_TRIM;
    public static Item RIB_ARMOR_TRIM;
    public static Item SENTRY_ARMOR_TRIM;
    public static Item SHAPER_ARMOR_TRIM;
    public static Item SILENCE_ARMOR_TRIM;
    public static Item SNOUT_ARMOR_TRIM;
    public static Item SPIRE_ARMOR_TRIM;
    public static Item TIDE_ARMOR_TRIM;
    public static Item VEX_ARMOR_TRIM;
    public static Item WARD_ARMOR_TRIM;
    public static Item WAYFINDER_ARMOR_TRIM;
    public static Item WILD_ARMOR_TRIM;

    // 1.21
    public static Item BREEZE_ROD;
    public static Item MACE;
    public static Item PRECIPICE;
    public static Item CREATOR;
    public static Item CREATOR_MUSIC_BOX;
    public static Item OMINOUS_BOTTLE;
    public static Item OMINOUS_TRIAL_KEY;
    public static Item FLOW_POTTERY_SHERD;
    public static Item GUSTER_POTTERY_SHERD;
    public static Item SCRAPE_POTTERY_SHERD;
    public static Item TRIAL_EXPLORER_MAP;
    public static Item TRIAL_KEY;
    public static Item WIND_CHARGE;

    public static void init() {
        EXPLORER_MAP = item("explorer_map");
        DEBUG_STICK = item("debug_stick");
        DRIED_KELP = item("dried_kelp");
        MOB_BUCKET = item("mob_bucket");
        SEA_HEART = item("sea_heart");
        NAUTILUS_SHELL = item("nautilus_shell");
        PHANTOM_MEMBRANE = item("phantom_membrane");
        TURTLE_SCUTE = item("turtle_scute");
        TRIDENT = item("trident");
        TURTLE_SHELL = item("turtle_shell");

        BANNER_PATTERN = item("banner_pattern");
        CROSSBOW = item("crossbow");
        BLUE_DYE = item("blue_dye");
        BROWN_DYE = item("brown_dye");
        BLACK_DYE = item("black_dye");
        WHITE_DYE = item("white_dye");
        LEATHER_HORSE_ARMOR = item("leather_horse_armor");
        SUSPICIOUS_STEW = item("suspicious_stew");

        HONEY_BOTTLE = item("honey_bottle");
        HONEYCOMB = item("honeycomb");

        LODESTONE_COMPASS = item("lodestone_compass");
        PIGSTEP = item("pigstep");
        NETHERITE_HELMET = item("netherite_helmet");
        NETHERITE_CHESTPLATE = item("netherite_chestplate");
        NETHERITE_LEGGINGS = item("netherite_leggings");
        NETHERITE_BOOTS = item("netherite_boots");
        NETHERITE_AXE = item("netherite_axe");
        NETHERITE_HOE = item("netherite_hoe");
        NETHERITE_PICKAXE = item("netherite_pickaxe");
        NETHERITE_SHOVEL = item("netherite_shovel");
        NETHERITE_SWORD = item("netherite_sword");
        NETHERITE_INGOT = item("netherite_ingot");
        NETHERITE_SCRAP = item("netherite_scrap");
        WARPED_FUNGUS_STICK = item("warped_fungus_stick");

        AMETHYST_SHARD = item("amethyst_shard");
        BUNDLE = item("bundle");
        COPPER_INGOT = item("copper_ingot");
        GLOW_BERRIES = item("glow_berries");
        GLOW_INK_SAC = item("glow_ink_sac");
        POWDER_SNOW_BUCKET = item("powder_snow_bucket");
        RAW_IRON = item("raw_iron");
        RAW_GOLD = item("raw_gold");
        RAW_COPPER = item("raw_copper");
        SPYGLASS = item("spyglass");

        OTHERSIDE = item("otherside");

        DISC_FRAGMENT = item("disc_fragment");
        ECHO_SHARD = item("echo_shard");
        GOAT_HORN = item("goat_horn");
        MANGROVE_BOAT = item("mangrove_boat");
        MUSIC_DISC_5 = item("music_disc_5");
        RECOVERY_COMPASS = item("recovery_compass");

        BRUSH = item("brush");
        RELIC = item("relic");
        ANGLER_POTTERY_SHERD = item("angler_pottery_sherd");
        ARCHER_POTTERY_SHERD = item("archer_pottery_sherd");
        ARMS_UP_POTTERY_SHERD = item("arms_up_pottery_sherd");
        BLADE_POTTERY_SHERD = item("blade_pottery_sherd");
        BREWER_POTTERY_SHERD = item("brewer_pottery_sherd");
        BURN_POTTERY_SHERD = item("burn_pottery_sherd");
        DANGER_POTTERY_SHERD = item("danger_pottery_sherd");
        EXPLORER_POTTERY_SHERD = item("explorer_pottery_sherd");
        FRIEND_POTTERY_SHERD = item("friend_pottery_sherd");
        HEART_POTTERY_SHERD = item("heart_pottery_sherd");
        HEARTBREAK_POTTERY_SHERD = item("heartbreak_pottery_sherd");
        HOWL_POTTERY_SHERD = item("howl_pottery_sherd");
        MINER_POTTERY_SHERD = item("miner_pottery_sherd");
        MOURNER_POTTERY_SHERD = item("mourner_pottery_sherd");
        PLENTY_POTTERY_SHERD = item("plenty_pottery_sherd");
        PRIZE_POTTERY_SHERD = item("prize_pottery_sherd");
        SHEAF_POTTERY_SHERD = item("sheaf_pottery_sherd");
        SHELTER_POTTERY_SHERD = item("shelter_pottery_sherd");
        SKULL_POTTERY_SHERD = item("skull_pottery_sherd");
        SNORT_POTTERY_SHERD = item("snort_pottery_sherd");
        NETHERITE_UPGRADE = item("netherite_upgrade");
        COAST_ARMOR_TRIM = item("coast_armor_trim");
        DUNE_ARMOR_TRIM = item("dune_armor_trim");
        EYE_ARMOR_TRIM = item("eye_armor_trim");
        HOST_ARMOR_TRIM = item("host_armor_trim");
        RAISER_ARMOR_TRIM = item("raiser_armor_trim");
        RIB_ARMOR_TRIM = item("rib_armor_trim");
        SENTRY_ARMOR_TRIM = item("sentry_armor_trim");
        SHAPER_ARMOR_TRIM = item("shaper_armor_trim");
        SILENCE_ARMOR_TRIM = item("silence_armor_trim");
        SNOUT_ARMOR_TRIM = item("snout_armor_trim");
        SPIRE_ARMOR_TRIM = item("spire_armor_trim");
        TIDE_ARMOR_TRIM = item("tide_armor_trim");
        VEX_ARMOR_TRIM = item("vex_armor_trim");
        WARD_ARMOR_TRIM = item("ward_armor_trim");
        WAYFINDER_ARMOR_TRIM = item("wayfinder_armor_trim");
        WILD_ARMOR_TRIM = item("wild_armor_trim");

        BREEZE_ROD = item("breeze_rod");
        MACE = item("mace");
        PRECIPICE = item("precipice");
        CREATOR = item("creator");
        CREATOR_MUSIC_BOX = item("creator_music_box");
        OMINOUS_BOTTLE = item("ominous_bottle");
        OMINOUS_TRIAL_KEY = item("ominous_trial_key");
        FLOW_POTTERY_SHERD = item("flow_pottery_sherd");
        GUSTER_POTTERY_SHERD = item("guster_pottery_sherd");
        SCRAPE_POTTERY_SHERD = item("scrape_pottery_sherd");
        TRIAL_EXPLORER_MAP = item("trial_explorer_map");
        TRIAL_KEY = item("trial_key");
        WIND_CHARGE = item("wind_charge");
    }

    private static Item item(String name) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(Tags.MOD_ID, name));
    }
}
