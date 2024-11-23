package surreal.backportium.block;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import surreal.backportium.Tags;
import surreal.backportium.api.enums.CoralType;
import surreal.backportium.block.plant.BlockPlantDouble;
import surreal.backportium.block.plant.BlockPlantWater;
import surreal.backportium.block.plant.coral.BlockCoral;
import surreal.backportium.block.plant.coral.BlockCoralBlock;
import surreal.backportium.block.plant.coral.BlockCoralFan;
import surreal.backportium.block.v1_13.BlockPumpkin;
import surreal.backportium.block.v1_13.*;
import surreal.backportium.item.*;
import surreal.backportium.item.v1_13.ItemBlockCoral;
import surreal.backportium.item.v1_13.ItemBlockCoralFan;
import surreal.backportium.item.v1_13.ItemBlockKelp;
import surreal.backportium.tile.v1_13.TileConduit;
import surreal.backportium.util.SupplierInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static surreal.backportium.api.enums.CoralType.*;

@SuppressWarnings("deprecation")
public class ModBlocks {

    private static final List<Block> BLOCKS = new ArrayList<>();

    // Default Item impl
    private static final SupplierInput<Block, Item> BASIC_ITEM = ItemBlock::new;
    private static final SupplierInput<Block, Item> BASIC_ITEM_SUBTYPE = ItemBlockSub::new;
    private static final SupplierInput<Block, Item> BASIC_ITEM_TEISR = ItemBlockTEISR::new;
    private static final SupplierInput<Block, Item> BASIC_CLUSTERED_ITEM = ItemBlockClustered::new;
    private static final SupplierInput<Block, Item> BASIC_KELP_ITEM = ItemBlockKelp::new;
    private static final SupplierInput<Block, Item> BASIC_CORAL_ITEM = ItemBlockCoral::new;
    private static final SupplierInput<Block, Item> BASIC_FAN_ITEM = ItemBlockCoralFan::new;
    private static final SupplierInput<Block, Item> BASIC_SLAB_ITEM = block -> {
        assert block instanceof BlockSlabDef;
        BlockSlabDef slab = (BlockSlabDef) block;
        return new ItemSlabDef(block, slab, slab.getDoubleSlab());
    };

    // 1.13
    public static final BlockBlueIce BLUE_ICE = register(new BlockBlueIce(), BASIC_ITEM, "blue_ice");
    public static final BlockBubbleColumn BUBBLE_COLUMN = register(new BlockBubbleColumn(), null, "bubble_column");
    public static final BlockPumpkin UNCARVED_PUMPKIN = register(new BlockPumpkin(), BASIC_ITEM, "pumpkin");
    public static final BlockConduit CONDUIT = register(new BlockConduit(), BASIC_ITEM_TEISR, "conduit");
    public static final BlockKelp KELP = register(new BlockKelp(), BASIC_KELP_ITEM, "kelp");
    public static final Block DRIED_KELP_BLOCK = register(new BlockDef(Material.GRASS, MapColor.BLACK).setSoundType(SoundType.PLANT).setCreativeTab(CreativeTabs.BUILDING_BLOCKS), block -> new ItemBlockBurnable(block, 4000), "dried_kelp_block");
    public static final BlockSeaPickle SEA_PICKLE = register((BlockSeaPickle) new BlockSeaPickle(Material.GRASS, Material.GRASS.getMaterialMapColor()).setSoundType(SoundType.PLANT), BASIC_CLUSTERED_ITEM, "sea_pickle");
    public static final BlockPlantDouble SEAGRASS_DOUBLE = register(new BlockDoubleSeagrass(Material.GRASS), null, "seagrass_double");
    public static final BlockPlantWater SEAGRASS = register(new BlockPlantWater(Material.GRASS, Material.GRASS.getMaterialMapColor(), SEAGRASS_DOUBLE), BASIC_ITEM, "seagrass");
    public static final BlockTurtleEgg TURTLE_EGG = register(new BlockTurtleEgg(Material.SPONGE, Material.SPONGE.getMaterialMapColor()), BASIC_CLUSTERED_ITEM, "turtle_egg");
    public static final BlockCoral CORAL = register(new BlockCoralImpl(Material.CORAL), BASIC_CORAL_ITEM, "coral");
    public static final BlockCoralBlock CORAL_BLOCK = register(new BlockCoralBlockImpl(Material.CORAL), BASIC_CORAL_ITEM, "coral_block");
    public static final BlockCoralFan TUBE_CORAL_FAN = register(new BlockCoralFanImpl(Material.CORAL, TUBE), BASIC_FAN_ITEM, "tube_coral_fan");
    public static final BlockCoralFan BRAIN_CORAL_FAN = register(new BlockCoralFanImpl(Material.CORAL, BRAIN), BASIC_FAN_ITEM, "brain_coral_fan");
    public static final BlockCoralFan BUBBLE_CORAL_FAN = register(new BlockCoralFanImpl(Material.CORAL, BUBBLE), BASIC_FAN_ITEM, "bubble_coral_fan");
    public static final BlockCoralFan FIRE_CORAL_FAN = register(new BlockCoralFanImpl(Material.CORAL, FIRE), BASIC_FAN_ITEM, "fire_coral_fan");
    public static final BlockCoralFan HORN_CORAL_FAN = register(new BlockCoralFanImpl(Material.CORAL, HORN), BASIC_FAN_ITEM, "horn_coral_fan");

    public static final BlockSmoothSandstone SMOOTH_SANDSTONE = register(new BlockSmoothSandstone(Material.ROCK), BASIC_ITEM_SUBTYPE, "smooth_sandstone");
    public static final Block SMOOTH_QUARTZ = register(new BlockDef(Material.ROCK).setHardness(2F).setResistance(6F).setCreativeTab(CreativeTabs.BUILDING_BLOCKS), BASIC_ITEM, "smooth_quartz");
    public static final Block SMOOTH_STONE = register(new BlockDef(Material.ROCK).setHardness(2F).setResistance(6F).setCreativeTab(CreativeTabs.BUILDING_BLOCKS), BASIC_ITEM, "smooth_stone");

    public static final BlockSlabDef PRISMARINE_SLAB = register(new BlockSlabPrismarine(Material.ROCK).setDoubleSlab(register(new BlockSlabPrismarine.Double(Material.ROCK), null, "prismarine_slab_double")), BASIC_SLAB_ITEM, "prismarine_slab");
    public static final BlockSlabDef SMOOTH_SANDSTONE_SLAB = register(new BlockSlabSmoothSandstone(Material.ROCK).setDoubleSlab(register(new BlockSlabSmoothSandstone.Double(Material.ROCK), null, "smooth_sandstone_slab_double")), BASIC_SLAB_ITEM, "smooth_sandstone_slab");
    public static final BlockSlabDef SMOOTH_QUARTZ_SLAB = register((BlockSlabDef) new BlockSlabImpl(Material.ROCK).setDoubleSlab(register(new BlockSlabImpl.Double(Material.ROCK).setHardness(2F).setResistance(6F), null, "smooth_quartz_slab_double")).setHardness(2F).setResistance(6F), BASIC_SLAB_ITEM, "smooth_quartz_slab");
    public static final BlockSlabDef SMOOTH_STONE_SLAB = register((BlockSlabDef) new BlockSlabImpl(Material.ROCK).setDoubleSlab(register(new BlockSlabImpl.Double(Material.ROCK).setHardness(2F).setResistance(6F), null, "smooth_stone_slab_double")).setHardness(2F).setResistance(6F), BASIC_SLAB_ITEM, "smooth_stone_slab");

    public static final BlockStairsDef PRISMARINE_STAIRS = register(new BlockStairsDef(Blocks.PRISMARINE.getStateFromMeta(0)), BASIC_ITEM, "prismarine_stairs");
    public static final BlockStairsDef PRISMARINE_BRICKS_STAIRS = register(new BlockStairsDef(Blocks.PRISMARINE.getStateFromMeta(1)), BASIC_ITEM, "prismarine_bricks_stairs");
    public static final BlockStairsDef DARK_PRISMARINE_STAIRS = register(new BlockStairsDef(Blocks.PRISMARINE.getStateFromMeta(2)), BASIC_ITEM, "dark_prismarine_stairs");
    public static final BlockStairsDef SMOOTH_SANDSTONE_STAIRS = register(new BlockStairsDef(SMOOTH_SANDSTONE.getDefaultState()), BASIC_ITEM, "smooth_sandstone_stairs");
    public static final BlockStairsDef SMOOTH_RED_SANDSTONE_STAIRS = register(new BlockStairsDef(SMOOTH_SANDSTONE.getStateFromMeta(1)), BASIC_ITEM, "smooth_red_sandstone_stairs");
    public static final BlockStairsDef SMOOTH_QUARTZ_STAIRS = register(new BlockStairsDef(SMOOTH_QUARTZ.getDefaultState()), BASIC_ITEM, "smooth_quartz_stairs");

    public static final BlockTrapDoor SPRUCE_TRAPDOOR = register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "spruce_trapdoor");
    public static final BlockTrapDoor BIRCH_TRAPDOOR = register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "birch_trapdoor");
    public static final BlockTrapDoor JUNGLE_TRAPDOOR = register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "jungle_trapdoor");
    public static final BlockTrapDoor ACACIA_TRAPDOOR = register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "acacia_trapdoor");
    public static final BlockTrapDoor DARKOAK_TRAPDOOR = register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "dark_oak_trapdoor");

    public static final BlockPressurePlate SPRUCE_PLATE = register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "spruce_plate");
    public static final BlockPressurePlate BIRCH_PLATE = register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "birch_plate");
    public static final BlockPressurePlate JUNGLE_PLATE = register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "jungle_plate");
    public static final BlockPressurePlate ACACIA_PLATE = register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "acacia_plate");
    public static final BlockPressurePlate DARKOAK_PLATE = register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "dark_oak_plate");

    public static final BlockButton SPRUCE_BUTTON = register(new BlockButtonWoodDef(), BASIC_ITEM, "spruce_button");
    public static final BlockButton BIRCH_BUTTON = register(new BlockButtonWoodDef(), BASIC_ITEM, "birch_button");
    public static final BlockButton JUNGLE_BUTTON = register(new BlockButtonWoodDef(), BASIC_ITEM, "jungle_button");
    public static final BlockButton ACACIA_BUTTON = register(new BlockButtonWoodDef(), BASIC_ITEM, "acacia_button");
    public static final BlockButton DARKOAK_BUTTON = register(new BlockButtonWoodDef(), BASIC_ITEM, "dark_oak_button");

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        BLOCKS.forEach(registry::register);
    }

    public static void registerTiles() {
        GameRegistry.registerTileEntity(TileConduit.class, Objects.requireNonNull(CONDUIT.getRegistryName()));
    }

    private static <T extends Block> T register(T block, SupplierInput<Block, Item> handleItem, String name) {
        block.setRegistryName(Tags.MOD_ID, name).setTranslationKey(Tags.MOD_ID + "." + name);
        BLOCKS.add(block);
        if (handleItem != null) {
            Item itemBlock = handleItem.get(block);
            ModItems.register(itemBlock, Objects.requireNonNull(block.getRegistryName()));
        }
        return block;
    }

    public static Block getWoodTrapdoor(BlockPlanks.EnumType type) {
        switch (type) {
            default:
                return Blocks.TRAPDOOR;
            case SPRUCE:
                return SPRUCE_TRAPDOOR;
            case BIRCH:
                return BIRCH_TRAPDOOR;
            case JUNGLE:
                return JUNGLE_TRAPDOOR;
            case ACACIA:
                return ACACIA_TRAPDOOR;
            case DARK_OAK:
                return DARKOAK_TRAPDOOR;
        }
    }

    public static Block getWoodPlate(BlockPlanks.EnumType type) {
        switch (type) {
            default:
                return Blocks.WOODEN_PRESSURE_PLATE;
            case SPRUCE:
                return SPRUCE_PLATE;
            case BIRCH:
                return BIRCH_PLATE;
            case JUNGLE:
                return JUNGLE_PLATE;
            case ACACIA:
                return ACACIA_PLATE;
            case DARK_OAK:
                return DARKOAK_PLATE;
        }
    }

    public static Block getWoodButton(BlockPlanks.EnumType type) {
        switch (type) {
            default:
                return Blocks.WOODEN_BUTTON;
            case SPRUCE:
                return SPRUCE_BUTTON;
            case BIRCH:
                return BIRCH_BUTTON;
            case JUNGLE:
                return JUNGLE_BUTTON;
            case ACACIA:
                return ACACIA_BUTTON;
            case DARK_OAK:
                return DARKOAK_BUTTON;
        }
    }

    public static Block getCoralFan(CoralType type) {
        switch (type) {
            default:
                return Blocks.AIR;
            case TUBE:
                return TUBE_CORAL_FAN;
            case BRAIN:
                return BRAIN_CORAL_FAN;
            case BUBBLE:
                return BUBBLE_CORAL_FAN;
            case FIRE:
                return FIRE_CORAL_FAN;
            case HORN:
                return HORN_CORAL_FAN;
        }
    }
}
