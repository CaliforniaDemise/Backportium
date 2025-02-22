package surreal.backportium.world.gen.feature.coral;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.enums.CoralType;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.plant.coral.BlockCoral;
import surreal.backportium.block.plant.coral.BlockCoralBlock;
import surreal.backportium.block.plant.coral.BlockCoralFan;
import surreal.backportium.block.v13.BlockSeaPickle;
import surreal.backportium.util.MutBlockPos;
import surreal.backportium.world.gen.feature.WorldGeneratorFoliage;

import java.util.Random;

public abstract class WorldGeneratorCoral extends WorldGenerator {

    @Override
    public boolean generate(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos pos) {
        IBlockState state = this.getCoralState(worldIn, rand, pos);
        return this.generateCoral(worldIn, rand, pos, state);
    }

    protected abstract boolean generateCoral(World world, Random random, BlockPos pos, IBlockState state);

    protected IBlockState getCoralState(World world, Random random, BlockPos pos) {
        CoralType type = CoralType.byMetadata(random.nextInt(5));
        return ModBlocks.CORAL_BLOCK.getDefaultState().withProperty(BlockCoralBlock.VARIANT, type);
    }

    protected boolean generateCoralPiece(World world, Random random, BlockPos pos, IBlockState state) {
        BlockPos blockPos = pos.up();
        IBlockState blockState = world.getBlockState(pos);
        if ((blockState.getMaterial() == Material.WATER || blockState.getBlock() instanceof BlockCoralFan || blockState.getBlock() instanceof BlockCoral) && world.getBlockState(blockPos).getMaterial() == Material.WATER) {
            world.setBlockState(pos, state);
            if (random.nextFloat() < 0.25F) world.setBlockState(blockPos, this.getCoralPiece(random));
            else if (random.nextFloat() < 0.05F) world.setBlockState(blockPos, ModBlocks.SEA_PICKLE.getDefaultState().withProperty(BlockSeaPickle.AMOUNT, random.nextInt(4)));
            for (EnumFacing direction : EnumFacing.HORIZONTALS) {
                if (random.nextFloat() < 0.2F) {
                    BlockPos blockPos2 = pos.offset(direction);
                    if (world.getBlockState(blockPos2).getMaterial() == Material.WATER) {
                        world.setBlockState(blockPos2, ModBlocks.getCoralFan(CoralType.byMetadata(random.nextInt(5))).getDefaultState().withProperty(BlockDirectional.FACING, direction));
                    }
                }
            }
            return true;
        } else return false;
    }

    protected IBlockState getCoralPiece(Random random) {
        CoralType type = CoralType.byMetadata(random.nextInt(5));
        boolean isFan = random.nextBoolean();
        if (!isFan) return ModBlocks.CORAL.getDefaultState().withProperty(BlockCoral.VARIANT, type);
        else return ModBlocks.getCoralFan(type).getDefaultState();
    }

    public static class WarmVegetation extends WorldGeneratorFoliage {

        private final WorldGeneratorCoral[] coralGens;

        public WarmVegetation(int noiseToCountRatio, double noiseFactor, double noiseOffset, WorldGeneratorCoral... coralGens) {
            super(noiseToCountRatio, noiseFactor, noiseOffset);
            this.coralGens = coralGens;
        }

        @Override
        public boolean generate(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos position) {
            MutBlockPos mutPos = new MutBlockPos();
            WorldGeneratorCoral coral = this.coralGens[rand.nextInt(this.coralGens.length)];
            boolean b = false;
            for (int i = 0; i < this.getCount(rand, position); i++) {
                mutPos.setPos(position.getX() + rand.nextInt(16), position.getY() - 1, position.getZ() + rand.nextInt(16));
                IBlockState state;
                while ((state = worldIn.getBlockState(mutPos)).getMaterial() == Material.WATER || !state.isSideSolid(worldIn, mutPos, EnumFacing.UP)) mutPos.move(EnumFacing.DOWN);
                mutPos.move(EnumFacing.UP);
                b = true;
                coral.generate(worldIn, rand, mutPos.toImmutable());
            }
            return b;
        }
    }
}
