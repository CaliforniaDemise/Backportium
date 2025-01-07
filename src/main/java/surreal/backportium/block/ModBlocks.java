package surreal.backportium.block;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.enums.CoralType;
import surreal.backportium.block.plant.BlockPlantWater;
import surreal.backportium.block.v13.BlockPumpkin;
import surreal.backportium.block.v13.*;
import surreal.backportium.item.*;
import surreal.backportium.item.v13.ItemBlockCoral;
import surreal.backportium.item.v13.ItemBlockCoralFan;
import surreal.backportium.item.v13.ItemBlockKelp;
import surreal.backportium.tile.v13.TileConduit;
import surreal.backportium.util.Registrar;
import surreal.backportium.util.SupplierInput;

import java.util.Objects;

import static surreal.backportium.api.enums.CoralType.*;

@SuppressWarnings("deprecation")
public class ModBlocks extends Registrar<Block> {

    // 1.13
    @ObjectHolder("minecraft:shulker_box") public static final Block SHULKER_BOX = null;

    @ObjectHolder("backportium:blue_ice") public static final Block BLUE_ICE = null;
    @ObjectHolder("backportium:bubble_column") public static final Block BUBBLE_COLUMN = null;
    @ObjectHolder("backportium:pumpkin") public static final Block UNCARVED_PUMPKIN = null;
    @ObjectHolder("backportium:conduit") public static final Block CONDUIT = null;
    @ObjectHolder("backportium:kelp") public static final Block KELP = null;
    @ObjectHolder("backportium:dried_kelp_block") public static final Block DRIED_KELP_BLOCK = null;
    @ObjectHolder("backportium:sea_pickle") public static final Block SEA_PICKLE = null;
    @ObjectHolder("backportium:seagrass_double") public static final Block SEAGRASS_DOUBLE = null;
    @ObjectHolder("backportium:seagrass") public static final Block SEAGRASS = null;
    @ObjectHolder("backportium:turtle_egg") public static final Block TURTLE_EGG = null;
    @ObjectHolder("backportium:coral") public static final Block CORAL = null;
    @ObjectHolder("backportium:coral_block") public static final Block CORAL_BLOCK = null;
    @ObjectHolder("backportium:tube_coral_fan") public static final Block TUBE_CORAL_FAN = null;
    @ObjectHolder("backportium:brain_coral_fan") public static final Block BRAIN_CORAL_FAN = null;
    @ObjectHolder("backportium:bubble_coral_fan") public static final Block BUBBLE_CORAL_FAN = null;
    @ObjectHolder("backportium:fire_coral_fan") public static final Block FIRE_CORAL_FAN = null;
    @ObjectHolder("backportium:horn_coral_fan") public static final Block HORN_CORAL_FAN = null;

    @ObjectHolder("backportium:smooth_sandstone") public static final Block SMOOTH_SANDSTONE = null;
    @ObjectHolder("backportium:smooth_quartz") public static final Block SMOOTH_QUARTZ = null;
    @ObjectHolder("backportium:smooth_stone") public static final Block SMOOTH_STONE = null;

    @ObjectHolder("backportium:prismarine_slab") public static final Block PRISMARINE_SLAB = null;
    @ObjectHolder("backportium:smooth_sandstone_slab") public static final Block SMOOTH_SANDSTONE_SLAB = null;
    @ObjectHolder("backportium:smooth_quartz_slab") public static final Block SMOOTH_QUARTZ_SLAB = null;
    @ObjectHolder("backportium:smooth_stone_slab") public static final Block SMOOTH_STONE_SLAB = null;

    @ObjectHolder("backportium:prismarine_stairs") public static final Block PRISMARINE_STAIRS = null;
    @ObjectHolder("backportium:prismarine_bricks_stairs") public static final Block PRISMARINE_BRICKS_STAIRS = null;
    @ObjectHolder("backportium:dark_prismarine_stairs") public static final Block DARK_PRISMARINE_STAIRS = null;
    @ObjectHolder("backportium:smooth_sandstone_stairs") public static final Block SMOOTH_SANDSTONE_STAIRS = null;
    @ObjectHolder("backportium:smooth_red_sandstone_stairs") public static final Block SMOOTH_RED_SANDSTONE_STAIRS = null;
    @ObjectHolder("backportium:smooth_quartz_stairs") public static final Block SMOOTH_QUARTZ_STAIRS = null;

    @ObjectHolder("backportium:spruce_trapdoor") public static final Block SPRUCE_TRAPDOOR = null;
    @ObjectHolder("backportium:birch_trapdoor") public static final Block BIRCH_TRAPDOOR = null;
    @ObjectHolder("backportium:jungle_trapdoor") public static final Block JUNGLE_TRAPDOOR = null;
    @ObjectHolder("backportium:acacia_trapdoor") public static final Block ACACIA_TRAPDOOR = null;
    @ObjectHolder("backportium:dark_oak_trapdoor") public static final Block DARKOAK_TRAPDOOR = null;

    @ObjectHolder("backportium:spruce_plate") public static final Block SPRUCE_PLATE = null;
    @ObjectHolder("backportium:birch_plate") public static final Block BIRCH_PLATE = null;
    @ObjectHolder("backportium:jungle_plate") public static final Block JUNGLE_PLATE = null;
    @ObjectHolder("backportium:acacia_plate") public static final Block ACACIA_PLATE = null;
    @ObjectHolder("backportium:dark_oak_plate") public static final Block DARKOAK_PLATE = null;

    @ObjectHolder("backportium:spruce_button") public static final Block SPRUCE_BUTTON = null;
    @ObjectHolder("backportium:birch_button") public static final Block BIRCH_BUTTON = null;
    @ObjectHolder("backportium:jungle_button") public static final Block JUNGLE_BUTTON = null;
    @ObjectHolder("backportium:acacia_button") public static final Block ACACIA_BUTTON = null;
    @ObjectHolder("backportium:dark_oak_button") public static final Block DARKOAK_BUTTON = null;

    public static Block getWoodTrapdoor(BlockPlanks.EnumType type) {
        switch (type) {
            case SPRUCE: return SPRUCE_TRAPDOOR;
            case BIRCH: return BIRCH_TRAPDOOR;
            case JUNGLE: return JUNGLE_TRAPDOOR;
            case ACACIA: return ACACIA_TRAPDOOR;
            case DARK_OAK: return DARKOAK_TRAPDOOR;
            default: return Blocks.TRAPDOOR;
        }
    }

    public static Block getWoodPlate(BlockPlanks.EnumType type) {
        switch (type) {
            case SPRUCE: return SPRUCE_PLATE;
            case BIRCH: return BIRCH_PLATE;
            case JUNGLE: return JUNGLE_PLATE;
            case ACACIA: return ACACIA_PLATE;
            case DARK_OAK: return DARKOAK_PLATE;
            default: return Blocks.WOODEN_PRESSURE_PLATE;
        }
    }

    public static Block getWoodButton(BlockPlanks.EnumType type) {
        switch (type) {
            case SPRUCE: return SPRUCE_BUTTON;
            case BIRCH: return BIRCH_BUTTON;
            case JUNGLE: return JUNGLE_BUTTON;
            case ACACIA: return ACACIA_BUTTON;
            case DARK_OAK: return DARKOAK_BUTTON;
            default: return Blocks.WOODEN_BUTTON;
        }
    }

    public static Block getCoralFan(CoralType type) {
        switch (type) {
            case TUBE: return TUBE_CORAL_FAN;
            case BRAIN: return BRAIN_CORAL_FAN;
            case BUBBLE: return BUBBLE_CORAL_FAN;
            case FIRE: return FIRE_CORAL_FAN;
            case HORN: return HORN_CORAL_FAN;
            default: return Blocks.AIR;
        }
    }

    private final ModItems modItems;

    public ModBlocks(ModItems modItems) {
        super(16);
        this.modItems = modItems;
    }

    @SideOnly(Side.CLIENT)
    public static void registerStateMappers() {
        ModelLoader.setCustomStateMapper(BUBBLE_COLUMN, new StateMap.Builder().ignore(BlockBubbleColumn.DRAG, BlockBubbleColumn.LEVEL).build());
    }

    protected Block register(Block block, SupplierInput<Block, Item> handleItem, String name) {
        this.register(block, name);
        if (handleItem != null) {
            Item itemBlock = handleItem.get(block);
            this.modItems.register(itemBlock, Objects.requireNonNull(block.getRegistryName()));
        }
        return block;
    }

    @Override
    protected Block register(@NotNull Block entry, @NotNull ResourceLocation location) {
        return super.register(entry, location).setRegistryName(location).setTranslationKey(location.getNamespace() + "." + location.getPath());
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        this.register();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        this.registerTiles();
    }

    private void registerTiles() {
        GameRegistry.registerTileEntity(TileConduit.class, Objects.requireNonNull(CONDUIT.getRegistryName()));
    }

    private void register() {
        final SupplierInput<Block, Item> BASIC_ITEM = ItemBlock::new;
        final SupplierInput<Block, Item> BASIC_ITEM_SUBTYPE = ItemBlockSub::new;
        final SupplierInput<Block, Item> BASIC_ITEM_TEISR = ItemBlockTEISR::new;
        final SupplierInput<Block, Item> BASIC_CLUSTERED_ITEM = ItemBlockClustered::new;
        final SupplierInput<Block, Item> BASIC_KELP_ITEM = ItemBlockKelp::new;
        final SupplierInput<Block, Item> BASIC_CORAL_ITEM = ItemBlockCoral::new;
        final SupplierInput<Block, Item> BASIC_FAN_ITEM = ItemBlockCoralFan::new;
        final SupplierInput<Block, Item> BASIC_SLAB_ITEM = block -> {
            assert block instanceof BlockSlabDef;
            BlockSlabDef slab = (BlockSlabDef) block;
            return new ItemSlabDef(block, slab, slab.getDoubleSlab());
        };

        this.register(new BlockBlueIce(), BASIC_ITEM, "blue_ice");
        this.register(new BlockBubbleColumn(), null, "bubble_column");
        this.register(new BlockPumpkin(), BASIC_ITEM, "pumpkin");
        this.register(new BlockConduit(), BASIC_ITEM_TEISR, "conduit");
        this.register(new BlockKelp(), BASIC_KELP_ITEM, "kelp");
        this.register(new BlockDef(Material.GRASS, MapColor.BLACK).setSoundType(SoundType.PLANT).setCreativeTab(CreativeTabs.BUILDING_BLOCKS), block -> new ItemBlockBurnable(block, 4000), "dried_kelp_block");
        this.register(new BlockSeaPickle(Material.GRASS, Material.GRASS.getMaterialMapColor()).setSoundType(SoundType.PLANT), BASIC_CLUSTERED_ITEM, "sea_pickle");
        this.register(new BlockDoubleSeagrass(Material.GRASS), null, "seagrass_double");
        this.register(new BlockPlantWater(Material.GRASS, Material.GRASS.getMaterialMapColor(), SEAGRASS_DOUBLE), BASIC_ITEM, "seagrass");
        this.register(new BlockTurtleEgg(Material.SPONGE, Material.SPONGE.getMaterialMapColor()), BASIC_CLUSTERED_ITEM, "turtle_egg");
        this.register(new BlockCoralImpl(Material.CORAL), BASIC_CORAL_ITEM, "coral");
        this.register(new BlockCoralBlockImpl(Material.CORAL), BASIC_CORAL_ITEM, "coral_block");
        this.register(new BlockCoralFanImpl(Material.CORAL, TUBE), BASIC_FAN_ITEM, "tube_coral_fan");
        this.register(new BlockCoralFanImpl(Material.CORAL, BRAIN), BASIC_FAN_ITEM, "brain_coral_fan");
        this.register(new BlockCoralFanImpl(Material.CORAL, BUBBLE), BASIC_FAN_ITEM, "bubble_coral_fan");
        this.register(new BlockCoralFanImpl(Material.CORAL, FIRE), BASIC_FAN_ITEM, "fire_coral_fan");
        this.register(new BlockCoralFanImpl(Material.CORAL, HORN), BASIC_FAN_ITEM, "horn_coral_fan");

        Block smoothSandstone = this.register(new BlockSmoothSandstone(Material.ROCK), BASIC_ITEM_SUBTYPE, "smooth_sandstone");
        Block smoothQuartz = this.register(new BlockDef(Material.ROCK).setHardness(2F).setResistance(6F).setCreativeTab(CreativeTabs.BUILDING_BLOCKS), BASIC_ITEM, "smooth_quartz");
        this.register(new BlockDef(Material.ROCK).setHardness(2F).setResistance(6F).setCreativeTab(CreativeTabs.BUILDING_BLOCKS), BASIC_ITEM, "smooth_stone");

        this.register(new BlockSlabPrismarine(Material.ROCK).setDoubleSlab(register(new BlockSlabPrismarine.Double(Material.ROCK), null, "prismarine_slab_double")), BASIC_SLAB_ITEM, "prismarine_slab");
        this.register(new BlockSlabSmoothSandstone(Material.ROCK).setDoubleSlab(register(new BlockSlabSmoothSandstone.Double(Material.ROCK), null, "smooth_sandstone_slab_double")), BASIC_SLAB_ITEM, "smooth_sandstone_slab");
        this.register(new BlockSlabImpl(Material.ROCK).setDoubleSlab(register(new BlockSlabImpl.Double(Material.ROCK).setHardness(2F).setResistance(6F), null, "smooth_quartz_slab_double")).setHardness(2F).setResistance(6F), BASIC_SLAB_ITEM, "smooth_quartz_slab");
        this.register(new BlockSlabImpl(Material.ROCK).setDoubleSlab(register(new BlockSlabImpl.Double(Material.ROCK).setHardness(2F).setResistance(6F), null, "smooth_stone_slab_double")).setHardness(2F).setResistance(6F), BASIC_SLAB_ITEM, "smooth_stone_slab");

        this.register(new BlockStairsDef(Blocks.PRISMARINE.getStateFromMeta(0)), BASIC_ITEM, "prismarine_stairs");
        this.register(new BlockStairsDef(Blocks.PRISMARINE.getStateFromMeta(1)), BASIC_ITEM, "prismarine_bricks_stairs");
        this.register(new BlockStairsDef(Blocks.PRISMARINE.getStateFromMeta(2)), BASIC_ITEM, "dark_prismarine_stairs");
        this.register(new BlockStairsDef(smoothSandstone.getDefaultState()), BASIC_ITEM, "smooth_sandstone_stairs");
        this.register(new BlockStairsDef(smoothSandstone.getStateFromMeta(1)), BASIC_ITEM, "smooth_red_sandstone_stairs");
        this.register(new BlockStairsDef(smoothQuartz.getDefaultState()), BASIC_ITEM, "smooth_quartz_stairs");

        this.register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "spruce_trapdoor");
        this.register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "birch_trapdoor");
        this.register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "jungle_trapdoor");
        this.register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "acacia_trapdoor");
        this.register(new BlockTrapDoorDef(Material.WOOD).setForce(3F), BASIC_ITEM, "dark_oak_trapdoor");

        this.register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "spruce_plate");
        this.register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "birch_plate");
        this.register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "jungle_plate");
        this.register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "acacia_plate");
        this.register(new BlockPressurePlateDef(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING).setForce(0.5F), BASIC_ITEM, "dark_oak_plate");

        this.register(new BlockButtonWoodDef(), BASIC_ITEM, "spruce_button");
        this.register(new BlockButtonWoodDef(), BASIC_ITEM, "birch_button");
        this.register(new BlockButtonWoodDef(), BASIC_ITEM, "jungle_button");
        this.register(new BlockButtonWoodDef(), BASIC_ITEM, "acacia_button");
        this.register(new BlockButtonWoodDef(), BASIC_ITEM, "dark_oak_button");
    }
}
