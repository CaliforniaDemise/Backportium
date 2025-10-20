package surreal.backportium._internal;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.Event;
import surreal.backportium.block.BlockPlant;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.init.ModPotions;
import surreal.backportium.tag.AllTags;
import surreal.backportium.util.FluidUtil;
import surreal.backportium.world.gen.feature.WorldGenIceberg;
import surreal.backportium.world.gen.feature.WorldGenKelp;
import surreal.backportium.world.gen.feature.WorldGenSeagrass;
import surreal.backportium.world.gen.feature.WorldGenStandardOre1;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoral;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralClaw;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralMushroom;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralTree;
import surreal.backportium.world.gen.layer.GenLayerOceans;

import java.util.List;
import java.util.Random;

import static surreal.backportium.tag.AllTags.*;

public class EventHandlerV13 {

    private static WorldGenKelp KELP_GEN;
    private static WorldGenSeagrass SEAGRASS_GEN;
    private static WorldGeneratorCoral.WarmVegetation WARM_VEGETATION_GEN;
    private static WorldGenIceberg ICEBERG_PACKED_ICE_GEN;
    private static WorldGenIceberg ICEBERG_BLUE_ICE_GEN;
    private static WorldGenStandardOre1 BLUE_ICE_GEN;

    public static WorldGenerator getKelpGen() { return KELP_GEN; }
    public static WorldGenerator getSeagrassGen() { return SEAGRASS_GEN; }
    public static WorldGenerator getWarmVegetationGen() { return WARM_VEGETATION_GEN; }
    public static WorldGenerator getIcebergPackedIceGen() { return ICEBERG_PACKED_ICE_GEN; }
    public static WorldGenerator getIcebergBlueIceGen() {  return ICEBERG_BLUE_ICE_GEN; }
    public static WorldGenerator getBlueIceGen() { return BLUE_ICE_GEN; }

    protected static void getSplashTexts(List<String> list) {
        list.add("All rumors are true!");
        list.add("Thanks for the fish!");
        list.add("Truly gone fishing!");
    }

    protected static void isPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if (effect.getPotion() == ModPotions.SLOW_FALLING) {
            EntityLivingBase entity = event.getEntityLiving();
            if (!entity.isNonBoss() && !(entity instanceof EntityShulker)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    protected static void playNoteBlock(NoteBlockEvent.Play event) {
        World world = event.getWorld();
        BlockPos downPos = event.getPos().down();
        IBlockState downState = world.getBlockState(downPos);
        if (downState.getMaterial() == Material.CORAL) {
            event.setInstrument(NoteBlockEvent.Instrument.BASSDRUM);
        }
    }

    protected static void applyBonemeal(BonemealEvent event) {
        World worldIn = event.getWorld();
        Random rand = worldIn.rand;
        BlockPos pos = event.getPos();
        BlockPos blockpos = pos.up();
        if (FluidUtil.getFluid(worldIn.getBlockState(blockpos)) == FluidRegistry.WATER && worldIn.isSideSolid(pos, EnumFacing.UP)) {
            event.setResult(Event.Result.ALLOW);
            if (worldIn.isRemote) return;
            for (int i = 0; i < 64; ++i) {
                BlockPos blockpos1 = blockpos;
                int j = 0;
                while (true) {
                    if (j >= i / 16) {
                        IBlockState check = worldIn.getBlockState(blockpos1);
                        if (worldIn.rand.nextInt(8) == 0 && ModBlocks.TALL_SEAGRASS.canPlaceBlockAt(worldIn, blockpos1)) {
                            ((BlockDoublePlant) ModBlocks.TALL_SEAGRASS).placeAt(worldIn, blockpos1, BlockDoublePlant.EnumPlantType.GRASS, 2);
                        }
                        else if (check.getBlock() != ModBlocks.TALL_SEAGRASS) {
                            IBlockState state = ModBlocks.SEAGRASS.getDefaultState();
                            if (((BlockPlant) ModBlocks.SEAGRASS).canBlockStay(worldIn, blockpos1, state)) {
                                worldIn.setBlockState(blockpos1, state, 3);
                            }
                        }
                        break;
                    }
                    blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
                    BlockPos downPos = blockpos1.down();
                    if (!worldIn.getBlockState(downPos).isSideSolid(worldIn, downPos, EnumFacing.UP) || worldIn.isSideSolid(blockpos1, EnumFacing.UP)) {
                        break;
                    }
                    ++j;
                }
            }
        }
    }

    protected static void generateOresPost(OreGenEvent.Post event) {
        initOreGenerators();
        Biome biome = event.getWorld().getBiome(event.getPos());
        if (BIOME_TAG.contains(BIOME_GENERATION_BLUE_ICE, biome)) {
            BLUE_ICE_GEN.genStandardOre1(event.getWorld(), event.getRand(), event.getPos());
        }
    }

    protected static void decorateBiomePre(DecorateBiomeEvent.Pre event) {
        initWorldGenerators();
        BlockPos pos = event.getChunkPos().getBlock(8 + event.getRand().nextInt(12), event.getWorld().getSeaLevel(), 8 + event.getRand().nextInt(12));
        Biome biome = event.getWorld().getBiome(pos);
        float rand = event.getRand().nextFloat();
        if (BIOME_TAG.contains(BIOME_GENERATION_ICEBERG_BLUE, biome) && event.getRand().nextFloat() < 1.0F / 200.0F) {
            ICEBERG_BLUE_ICE_GEN.generate(event.getWorld(), event.getRand(), pos);
        }
        else if (BIOME_TAG.contains(BIOME_GENERATION_ICEBERG, biome) && rand < 1.0F / 16.0F) {
            ICEBERG_PACKED_ICE_GEN.generate(event.getWorld(), event.getRand(), pos);
        }
    }

    protected static void decorateBiomePost(DecorateBiomeEvent.Post event) {
        World world = event.getWorld();
        ChunkPos chunkPos = event.getChunkPos();
        BlockPos pos = chunkPos.getBlock(0, 255, 0);
        Biome biome = world.getBiome(pos);
        if (BIOME_TAG.contains(BIOME_GENERATION_WARM_VEGETATION, biome)) {
            WARM_VEGETATION_GEN.generate(world, event.getRand(), chunkPos.getBlock(8, world.getSeaLevel(), 8));
        }
        if (BIOME_TAG.contains(BIOME_GENERATION_KELP, biome)) {
            KELP_GEN.generate(world, event.getRand(), chunkPos.getBlock(8, world.getSeaLevel(), 8));
        }
        if (BIOME_TAG.contains(BIOME_GENERATION_SEAGRASS, biome)) {
            SEAGRASS_GEN.generate(world, event.getRand(), chunkPos.getBlock(8, world.getSeaLevel(), 8));
        }
    }

    protected static void initGenLayers(WorldTypeEvent.InitBiomeGens event) {
        GenLayer[] layers = event.getNewBiomeGens();
        GenLayer[] replacement = new GenLayer[layers.length];
        for (int i = 0; i < layers.length; i++) {
            GenLayer oceans = new GenLayerOceans(2L, layers[i]);
            oceans.initWorldGenSeed(event.getSeed());
            replacement[i] = oceans;
        }
        event.setNewBiomeGens(replacement);
    }

    private static void initWorldGenerators() {
        if (KELP_GEN == null) {
            KELP_GEN = new WorldGenKelp(80, 80.0D, 0.0D);
            SEAGRASS_GEN = new WorldGenSeagrass();
            WARM_VEGETATION_GEN = new WorldGeneratorCoral.WarmVegetation(20, 400.0D, 0.0D, new WorldGeneratorCoralMushroom(), new WorldGeneratorCoralTree(), new WorldGeneratorCoralClaw());
            ICEBERG_PACKED_ICE_GEN = new WorldGenIceberg(Blocks.PACKED_ICE.getDefaultState());
            ICEBERG_BLUE_ICE_GEN = new WorldGenIceberg(ModBlocks.BLUE_ICE.getDefaultState());
        }
    }

    private static void initOreGenerators() {
        if (BLUE_ICE_GEN == null) {
            BLUE_ICE_GEN = new WorldGenStandardOre1(ModBlocks.BLUE_ICE.getDefaultState(), 33, 10, 0, 64, state -> state.getBlock() == Blocks.PACKED_ICE);
        }
    }
}
