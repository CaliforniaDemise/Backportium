package surreal.backportium;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import surreal.backportium.api.block.StrippableLog;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.plant.BlockPlant;
import surreal.backportium.block.plant.BlockPlantDouble;
import surreal.backportium.item.ModItems;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.sound.ModSounds;
import surreal.backportium.util.WorldHelper;
import surreal.backportium.world.biome.BiomeOceanFrozen;
import surreal.backportium.world.biome.BiomeOceanWarm;
import surreal.backportium.world.biome.ModBiomes;
import surreal.backportium.world.gen.feature.WorldGenKelp;
import surreal.backportium.world.gen.feature.WorldGenSeagrass;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoral;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralClaw;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralMushroom;
import surreal.backportium.world.gen.feature.coral.WorldGeneratorCoralTree;

import java.util.Objects;
import java.util.Random;

public class EventHandler {

    public static final WorldGenKelp KELP_GEN = new WorldGenKelp(80, 80.0D, 0.0D);
    public static final WorldGenSeagrass SEAGRASS_GEN = new WorldGenSeagrass();
    public static final WorldGeneratorCoral.WarmVegetation WARM_VEGETATION_GEN = new WorldGeneratorCoral.WarmVegetation(20, 400.0D, 0.0D, new WorldGeneratorCoralMushroom(), new WorldGeneratorCoralTree(), new WorldGeneratorCoralClaw());

    public static void loadLootTables(LootTableLoadEvent event) {
        if (event.getName().equals(LootTableList.GAMEPLAY_FISHING_TREASURE)) {
            LootTable table = event.getTable();
            LootPool mainPool = table.getPool("main");
            mainPool.addEntry(new LootEntryItem(ModItems.NAUTILUS_SHELL, 1, 0, new LootFunction[0], new LootCondition[0], Objects.requireNonNull(ModItems.NAUTILUS_SHELL.getRegistryName()).toString()));
        }
    }

    public static void isPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if (effect.getPotion() == ModPotions.SLOW_FALLING) {
            EntityLivingBase entity = event.getEntityLiving();
            if (!entity.isNonBoss() && !(entity instanceof EntityShulker)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    public static void playNoteBlock(NoteBlockEvent.Play event) {
        World world = event.getWorld();
        BlockPos downPos = event.getPos().down();
        IBlockState downState = world.getBlockState(downPos);
        if (downState.getMaterial() == Material.CORAL) {
            event.setInstrument(NoteBlockEvent.Instrument.BASSDRUM);
        }
    }

    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        {
            if (block instanceof StrippableLog) {
                if (stack.getItem().getHarvestLevel(stack, "axe", player, state) <= -1) return;
                StrippableLog log = (StrippableLog) block;
                if (log.onStrip(event.getWorld(), event.getEntityPlayer(), event.getHand(), event.getPos(), event.getWorld().getBlockState(event.getPos()), event.getFace(), event.getHitVec())) {
                    if (!world.isRemote) world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.ITEM_AXE_STRIP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    player.swingArm(event.getHand());
                    event.setUseItem(Event.Result.ALLOW);
                    event.setResult(Event.Result.ALLOW);
                    event.setCanceled(true);
                }
            }
        }
    }

    // TODO Make it better... Seagrass works grows on all solid blocks which makes normal grass block implementation not work........
    public static void applyBonemeal(BonemealEvent event) {
        World worldIn = event.getWorld();

        Random rand = worldIn.rand;
        BlockPos pos = event.getPos();

        BlockPos blockpos = pos.up();

        if (WorldHelper.inWater(worldIn, blockpos) && worldIn.isSideSolid(pos, EnumFacing.UP)) {
            event.setResult(Event.Result.ALLOW);
            if (worldIn.isRemote) return;
            for (int i = 0; i < 64; ++i) {
                BlockPos blockpos1 = blockpos;
                int j = 0;

                while (true) {
                    if (j >= i / 16) {
                        IBlockState check = worldIn.getBlockState(blockpos1);
                        if (check.getBlock() == ModBlocks.SEAGRASS && worldIn.rand.nextInt(8) == 0) {
                            BlockPos upPos = blockpos1.up();
                            if (WorldHelper.inWater(worldIn, upPos)) {
                                ((BlockPlantDouble) ModBlocks.SEAGRASS_DOUBLE).place(worldIn, blockpos1, ModBlocks.SEAGRASS.getDefaultState());
                            }
                        }
                        else if (check.getBlock() != ModBlocks.SEAGRASS_DOUBLE) {
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

    public static void decorateBiome(DecorateBiomeEvent.Post event) {
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
}
