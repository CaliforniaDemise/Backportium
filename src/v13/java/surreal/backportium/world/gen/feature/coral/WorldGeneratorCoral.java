package surreal.backportium.world.gen.feature.coral;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.block.BlockSeaPickle;
import surreal.backportium.block.BlockCoral;
import surreal.backportium.block.BlockCoralFan;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.util.FluidUtil;
import surreal.backportium.world.gen.feature.WorldGenFoliage;

import java.util.Random;

public abstract class WorldGeneratorCoral extends WorldGenerator {

    @Override
    public boolean generate(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos pos) {
        IBlockState state = this.getCoralState(worldIn, rand, pos);
        return this.generateCoral(worldIn, rand, pos, state);
    }

    protected abstract boolean generateCoral(World world, Random random, BlockPos pos, IBlockState state);

    protected IBlockState getCoralState(World world, Random random, BlockPos pos) {
        Block coralBlock = getCoralBlock(random.nextInt(5));
        return coralBlock.getDefaultState();
    }

    protected boolean generateCoralPiece(World world, Random random, BlockPos pos, IBlockState state) {
        BlockPos blockPos = pos.up();
        IBlockState blockState = world.getBlockState(pos);
        if ((blockState.getMaterial() == Material.WATER || blockState.getBlock() instanceof BlockCoralFan || blockState.getBlock() instanceof BlockCoral) && world.getBlockState(blockPos).getMaterial() == Material.WATER) {
            world.setBlockState(pos, state);
            if (FluidUtil.getFluid(world.getBlockState(blockPos)) == FluidRegistry.WATER) {
                if (random.nextFloat() < 0.25F) world.setBlockState(blockPos, this.getCoralPiece(random));
                else if (random.nextFloat() < 0.05F) world.setBlockState(blockPos, ModBlocks.SEA_PICKLE.getDefaultState().withProperty(BlockSeaPickle.AMOUNT, random.nextInt(4)));
            }
            for (EnumFacing direction : EnumFacing.HORIZONTALS) {
                if (random.nextFloat() < 0.2F) {
                    BlockPos blockPos2 = pos.offset(direction);
                    if (FluidUtil.getFluid(world.getBlockState(blockPos2)) == FluidRegistry.WATER) {
                        world.setBlockState(blockPos2, getCoralFan(random.nextInt(5)).getDefaultState().withProperty(BlockDirectional.FACING, direction));
                    }
                }
            }
            return true;
        } else return false;
    }

    protected IBlockState getCoralPiece(Random random) {
        int ordinal = random.nextInt(5);
        if (random.nextBoolean()) {
            return getCoralFan(ordinal).getDefaultState();
        }
        else {
            return getCoral(ordinal).getDefaultState();
        }
    }

    public static class WarmVegetation extends WorldGenFoliage {

        private final WorldGeneratorCoral[] coralGens;

        public WarmVegetation(int noiseToCountRatio, double noiseFactor, double noiseOffset, WorldGeneratorCoral... coralGens) {
            super(noiseToCountRatio, noiseFactor, noiseOffset);
            this.coralGens = coralGens;
        }

        @Override
        public boolean generate(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos position) {
            BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
            WorldGeneratorCoral coral = this.coralGens[rand.nextInt(this.coralGens.length)];
            boolean b = false;
            for (int i = 0; i < this.getCount(rand, position); i++) {
                mutPos.setPos(position.getX() + rand.nextInt(16), position.getY() - 1, position.getZ() + rand.nextInt(16));
                IBlockState state;
                while ((state = worldIn.getBlockState(mutPos)).getMaterial() == Material.WATER || !state.isSideSolid(worldIn, mutPos, EnumFacing.UP)) {
                    mutPos.move(EnumFacing.DOWN);
                }
                mutPos.move(EnumFacing.UP);
                b = true;
                coral.generate(worldIn, rand, mutPos.toImmutable());
            }
            return b;
        }
    }

    private static Block getCoral(int ordinal) {
        switch (ordinal) {
            case 1: return ModBlocks.BRAIN_CORAL;
            case 2: return ModBlocks.BUBBLE_CORAL;
            case 3: return ModBlocks.FIRE_CORAL;
            case 4: return ModBlocks.HORN_CORAL;
            default: return ModBlocks.TUBE_CORAL;
        }
    }

    private static Block getCoralFan(int ordinal) {
        switch (ordinal) {
            case 1: return ModBlocks.BRAIN_CORAL_FAN;
            case 2: return ModBlocks.BUBBLE_CORAL_FAN;
            case 3: return ModBlocks.FIRE_CORAL_FAN;
            case 4: return ModBlocks.HORN_CORAL_FAN;
            default: return ModBlocks.TUBE_CORAL_FAN;
        }
    }

    private static Block getCoralBlock(int ordinal) {
        switch (ordinal) {
            case 1: return ModBlocks.BRAIN_CORAL_BLOCK;
            case 2: return ModBlocks.BUBBLE_CORAL_BLOCK;
            case 3: return ModBlocks.FIRE_CORAL_BLOCK;
            case 4: return ModBlocks.HORN_CORAL_BLOCK;
            default: return ModBlocks.TUBE_CORAL_BLOCK;
        }
    }
}