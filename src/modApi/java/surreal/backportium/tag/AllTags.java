package surreal.backportium.tag;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockSeaLantern;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.registry.EntityEntry;

/**
 *
 * Default biome tags get registered in {@link ForgeModContainer#registerAllBiomesAndGenerateEvents()}
 */
public class AllTags {

    public static final BlockTag BLOCK_TAG = new BlockTag();
    public static final ItemTag ITEM_TAG = new ItemTag();
    public static final EntityTag ENTITY_TAG = new EntityTag();
    public static final Tag<Biome> BIOME_TAG = new Tag<>(null);

    public static final String BLOCK_CONDUIT_BUILDING_BLOCKS = "backportium:block/conduit_building_blocks";
    public static final String BLOCK_SEA_PICKLE_GROWABLE = "backportium:block/sea_pickle_growable";
    public static final String BLOCK_TURTLE_EGG_HATCHABLE = "backportium:block/turtle_egg_hatchable";

    public static final String ENTITY_IMPALING_WHITELIST = "backportium:entity/enchantment/impaling_whitelist";
    public static final String ENTITY_CHANNELING_BLACKLIST = "backportium:entity/enchantment/channeling_blacklist";
    public static final String ENTITY_BLOCK_CONDUIT_ATTACK = "backportium:entity/block/conduit_attack";

    // TODO Better world generation handling (?)
    public static final String BIOME_GENERATION_WARM_VEGETATION = "backportium:biome/generation/warm_vegetation";
    public static final String BIOME_GENERATION_SEAGRASS = "backportium:biome/generation/sea_grass";
    public static final String BIOME_GENERATION_KELP = "backportium:biome/generation/kelp";
    public static final String BIOME_GENERATION_ICEBERG = "backportium:biome/generation/iceberg";
    public static final String BIOME_GENERATION_ICEBERG_BLUE = "backportium:biome/generation/iceberg_blue";
    public static final String BIOME_GENERATION_BLUE_ICE = "backportium:biome/generation/blue_ice";
}