package surreal.backportium.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import surreal.backportium.Tags;

public class ModEntities {

    // 1.13
    public static EntityEntry DOLPHIN;
    public static EntityEntry DROWNED;
    public static EntityEntry COD;
    public static EntityEntry SALMON;
    public static EntityEntry PUFFERFISH;
    public static EntityEntry TROPICAL_FISH;
    public static EntityEntry PHANTOM;
    public static EntityEntry TURTLE;

    // 1.14
    public static EntityEntry CAT;
    public static EntityEntry FOX;
    public static EntityEntry BROWN_MOOSHROOM;
    public static EntityEntry PANDA;
    public static EntityEntry PILLAGER;
    public static EntityEntry RAVAGER;
    public static EntityEntry TRADER_LLAMA;
    public static EntityEntry WANDERING_TRADER;

    // 1.15
    public static EntityEntry BEE;

    // 1.16
    public static EntityEntry HOGLIN;
    public static EntityEntry PIGLIN;
    public static EntityEntry STRIDER;
    public static EntityEntry ZOGLIN;

    // 1.17
    public static EntityEntry GLOW_ITEM_FRAME;
    public static EntityEntry AXOLOTL;
    public static EntityEntry GLOW_SQUID;
    public static EntityEntry GOAT;

    // 1.19
    public static EntityEntry ALLAY;
    public static EntityEntry FROG;
    public static EntityEntry TADPOLE;
    public static EntityEntry WARDEN;
    public static EntityEntry CHEST_BOAT;

    // 1.20
    public static EntityEntry CAMEL;
    public static EntityEntry SNIFFER;
    public static EntityEntry CHERRY_BOAT;
    public static EntityEntry BAMBOO_RAFT;

    // 1.21
    public static EntityEntry BOGGED;
    public static EntityEntry BREEZE;
    public static EntityEntry WIND_CHARGE;

    public static void init() {
        DOLPHIN = entity("dolphin");
        DROWNED = entity("drowned");
        COD = entity("cod");
        SALMON = entity("salmon");
        PUFFERFISH = entity("pufferfish");
        TROPICAL_FISH = entity("tropical_fish");
        PHANTOM = entity("phantom");
        TURTLE = entity("turtle");

        CAT = entity("cat");
        FOX = entity("fox");
        BROWN_MOOSHROOM = entity("brown_mooshroom");
        PANDA = entity("panda");
        PILLAGER = entity("pillager");
        RAVAGER = entity("ravager");
        TRADER_LLAMA = entity("trader_llama");
        WANDERING_TRADER = entity("wandering_trader");

        BEE = entity("bee");

        HOGLIN = entity("hoglin");
        PIGLIN = entity("piglin");
        STRIDER = entity("strider");
        ZOGLIN = entity("zoglin");

        GLOW_ITEM_FRAME = entity("glow_item_frame");
        AXOLOTL = entity("axolotl");
        GLOW_SQUID = entity("glow_squid");
        GOAT = entity("goat");

        ALLAY = entity("allay");
        FROG = entity("frog");
        TADPOLE = entity("tadpole");
        WARDEN = entity("warden");
        CHEST_BOAT = entity("chest_boat");

        CAMEL = entity("camel");
        SNIFFER = entity("sniffer");
        CHERRY_BOAT = entity("cherry_boat");
        BAMBOO_RAFT = entity("bamboo_raft");

        BOGGED = entity("bogged");
        BREEZE = entity("breeze");
        WIND_CHARGE = entity("wind_charge");
    }

    private static EntityEntry entity(String name) {
        return ForgeRegistries.ENTITIES.getValue(new ResourceLocation(Tags.MOD_ID, name));
    }
}
