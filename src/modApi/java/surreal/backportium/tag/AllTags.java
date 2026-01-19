package surreal.backportium.tag;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeModContainer;

/**
 *
 * Default biome tags get registered in {@link ForgeModContainer#registerAllBiomesAndGenerateEvents()}
 */
public class AllTags {

    public static final BlockTag BLOCK_TAG = new BlockTag();
    public static final ItemTag ITEM_TAG = new ItemTag();
    public static final EntityTag ENTITY_TAG = new EntityTag();
    public static final Tag<Biome> BIOME_TAG = new Tag<>(null);

    // GLOBAL
    public static final String ENTITY_BOAT = "all:boat";
    public static final String ENTITY_AQUATIC = "all:aquatic";
    public static final String ENTITY_ARTHROPOD = "all:arthropod";
    public static final String ENTITY_UNDEAD = "all:undead";
    public static final String ENTITY_ZOMBIE = "all:zombie";
    public static final String ENTITY_SKELETON = "all:skeleton";

    // VANILLA
    /* POTION IMMUNITY */
    /* DAMAGE TYPE IMMUNITY */
    public static final String ENTITY_WITHER_ALLY = "minecraft:wither_friend";
    public static final String ENTITY_ENDERMAN_ENEMY = "minecraft:enderman_enemy"; // Endermite
    public static final String ENTITY_BANE_OF_ARTHROPODS_SENSITIVE = "minecraft:bane_of_arthropods_sensitive";

    // 1.13
    public static final String BLOCK_CONDUIT_BUILDING_BLOCKS = "backportium:conduit/building_blocks";
    public static final String BLOCK_CAN_GROW_SEA_PICKLE = "backportium:can_grow_sea_pickle";
    public static final String BLOCK_CAN_HATCH_TURTLE_EGG = "backportium:can_hatch_turtle_egg";
    public static final String BLOCK_CHANNELING_SENSITIVE = "backportium:channeling_sensitive";

    public static final String ENTITY_IMPALING_SENSITIVE = "backportium:impaling_sensitive";
    public static final String ENTITY_CONDUIT_ATTACKS = "backportium:conduit/attacks";
    public static final String ENTITY_CONDUIT_GIVES_EFFECT = "backportium:conduit/gives_effect";

    // TODO Better world generation handling (?)
    public static final String BIOME_GENERATION_WARM_VEGETATION = "backportium:biome/generation/warm_vegetation";
    public static final String BIOME_GENERATION_SEAGRASS = "backportium:biome/generation/sea_grass";
    public static final String BIOME_GENERATION_KELP = "backportium:biome/generation/kelp";
    public static final String BIOME_GENERATION_ICEBERG = "backportium:biome/generation/iceberg";
    public static final String BIOME_GENERATION_ICEBERG_BLUE = "backportium:biome/generation/iceberg_blue";
    public static final String BIOME_GENERATION_BLUE_ICE = "backportium:biome/generation/blue_ice";
}