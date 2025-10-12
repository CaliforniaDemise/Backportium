package surreal.backportium._internal;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.Event;
import surreal.backportium.block.BlockPlant;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.init.ModPotions;
import surreal.backportium.util.FluidUtil;
import surreal.backportium.world.biome.BiomeOceanFrozen;
import surreal.backportium.world.biome.BiomeOceanWarm;
import surreal.backportium.world.gen.feature.WorldGenKelp;
import surreal.backportium.world.gen.feature.WorldGenSeagrass;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoral;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralClaw;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralMushroom;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralTree;
import surreal.backportium.world.gen.layer.GenLayerOceans;

import java.util.List;
import java.util.Random;

public class EventHandlerV13 {

    public static final WorldGenKelp KELP_GEN = new WorldGenKelp(80, 80.0D, 0.0D);
    public static final WorldGenSeagrass SEAGRASS_GEN = new WorldGenSeagrass();
    public static final WorldGeneratorCoral.WarmVegetation WARM_VEGETATION_GEN = new WorldGeneratorCoral.WarmVegetation(20, 400.0D, 0.0D, new WorldGeneratorCoralMushroom(), new WorldGeneratorCoralTree(), new WorldGeneratorCoralClaw());

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

    protected static void decorateBiomePost(DecorateBiomeEvent.Post event) {
        World world = event.getWorld();
        if (world.provider.getDimensionType() != DimensionType.OVERWORLD) return;
        ChunkPos chunkPos = event.getChunkPos();
        BlockPos pos = chunkPos.getBlock(0, 255, 0);
        Biome biome = world.getBiome(pos);
        boolean isCold = BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD);
        boolean isWarm = biome instanceof BiomeOceanWarm;
        boolean isOcean = BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN);
        boolean isRiver = BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER);
        boolean isSwamp = BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP);
        if (isOcean && isWarm) {
            WARM_VEGETATION_GEN.generate(world, world.rand, chunkPos.getBlock(8, world.getSeaLevel(), 8));
        }
        if ((isOcean || isRiver || isSwamp)) {
            if (!isCold && !isWarm) KELP_GEN.generate(world, event.getRand(), chunkPos.getBlock(8, world.getSeaLevel(), 8));
            if (!(biome instanceof BiomeOceanFrozen)) SEAGRASS_GEN.generate(event.getWorld(), event.getRand(), chunkPos.getBlock(8, world.getSeaLevel(), 8));
        }
    }

    protected static void initGenLayers(WorldTypeEvent.InitBiomeGens event) {
        GenLayer[] layers = event.getNewBiomeGens();
        GenLayer[] replacement = new GenLayer[layers.length];
        for (int i = 0; i < layers.length; i++) {
            GenLayer test = new GenLayerOceans(2L, layers[i]);
            test.initWorldGenSeed(event.getSeed());
            replacement[i] = test;
        }
        event.setNewBiomeGens(replacement);
    }
}
