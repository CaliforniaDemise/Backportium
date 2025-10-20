package surreal.backportium.world.gen.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import surreal.backportium.init.ModBlocks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class WorldGenIceberg extends WorldGenerator {

    private final IBlockState state;

    public WorldGenIceberg(boolean notify, IBlockState state) {
        super(notify);
        this.state = state;
    }

    public WorldGenIceberg(IBlockState state) {
        this(false, state);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean generate(World world, Random rand, BlockPos pos) {
        boolean bl = rand.nextDouble() > 0.7;
        double d = rand.nextDouble() * 2.0 * Math.PI;
        int i = 11 - rand.nextInt(5);
        int j = 3 + rand.nextInt(3);
        boolean bl2 = rand.nextDouble() > 0.7;
        int l = bl2 ? rand.nextInt(6) + 6 : rand.nextInt(15) + 3;
        if (!bl2 && rand.nextDouble() > 0.9) {
            l += rand.nextInt(19) + 7;
        }

        int m = Math.min(l + rand.nextInt(11), 18);
        int n = Math.min(l + rand.nextInt(7) - rand.nextInt(5), 11);
        int o = bl2 ? i : 11;

        for (int p = -o; p < o; p++) {
            for (int q = -o; q < o; q++) {
                for (int r = 0; r < l; r++) {
                    int s = bl2 ? this.method_13417(r, l, n) : this.method_13419(rand, r, l, n);
                    if (bl2 || p < s) {
                        this.placeAt(world, rand, pos, l, p, r, q, s, o, bl2, j, d, bl, state);
                    }
                }
            }
        }

        this.method_13418(world, pos, n, l, bl2, i);

        for (int p = -o; p < o; p++) {
            for (int q = -o; q < o; q++) {
                for (int rx = -1; rx > -m; rx--) {
                    int s = bl2 ? MathHelper.ceil((float)o * (1.0F - (float)Math.pow((double)rx, 2.0) / ((float)m * 8.0F))) : o;
                    int t = this.method_13427(rand, -rx, m, n);
                    if (p < t) {
                        this.placeAt(world, rand, pos, m, p, rx, q, t, s, bl2, j, d, bl, state);
                    }
                }
            }
        }

        boolean bl3 = bl2 ? rand.nextDouble() > 0.1 : rand.nextDouble() > 0.7;
        if (bl3) {
            this.method_13428(rand, world, n, l, pos, bl2, i, d, j);
        }

        return true;
    }

    private void method_13428(Random rand, World world, int i, int j, BlockPos pos, boolean bl, int k, double d, int l) {
        int m = rand.nextBoolean() ? -1 : 1;
        int n = rand.nextBoolean() ? -1 : 1;
        int o = rand.nextInt(Math.max(i / 2 - 2, 1));
        if (rand.nextBoolean()) {
            o = i / 2 + 1 - rand.nextInt(Math.max(i - i / 2 - 1, 1));
        }

        int p = rand.nextInt(Math.max(i / 2 - 2, 1));
        if (rand.nextBoolean()) {
            p = i / 2 + 1 - rand.nextInt(Math.max(i - i / 2 - 1, 1));
        }

        if (bl) {
            o = p = rand.nextInt(Math.max(k - 5, 1));
        }

        BlockPos blockPos = new BlockPos(m * o, 0, n * p);
        double e = bl ? d + (Math.PI / 2) : rand.nextDouble() * 2.0 * Math.PI;

        for (int q = 0; q < j - 3; q++) {
            int r = this.method_13419(rand, q, j, i);
            this.setSnowToWater(r, q, pos, world, false, e, blockPos, k, l);
        }

        for (int q = -1; q > -j + rand.nextInt(5); q--) {
            int r = this.method_13427(rand, -q, j, i);
            this.setSnowToWater(r, q, pos, world, true, e, blockPos, k, l);
        }
    }

    private void setSnowToWater(int i, int y, BlockPos pos, World world, boolean placeWater, double d, BlockPos blockPos, int j, int k) {
        int l = i + 1 + j / 3;
        int m = Math.min(i - 3, 3) + k / 2 - 1;
        for (int n = -l; n < l; n++) {
            for (int o = -l; o < l; o++) {
                double e = this.getDistance(n, o, blockPos, l, m, d);
                if (e < 0.0) {
                    BlockPos blockPos2 = pos.add(n, y, o);
                    IBlockState blockState = world.getBlockState(blockPos2);
                    if (isSnowOrIce(blockState) || blockState.getBlock() == Blocks.SNOW) {
                        if (placeWater) {
                            this.setBlockAndNotifyAdequately(world, blockPos2, Blocks.WATER.getDefaultState());
                        } else {
                            this.setBlockAndNotifyAdequately(world, blockPos2, Blocks.AIR.getDefaultState());
                            this.clearSnowAbove(world, blockPos2);
                        }
                    }
                }
            }
        }
    }

    private void clearSnowAbove(World world, BlockPos pos) {
        BlockPos up = pos.up();
        if (world.getBlockState(up).getBlock() == Blocks.SNOW) {
            this.setBlockAndNotifyAdequately(world, up, Blocks.AIR.getDefaultState());
        }
    }

    private void placeAt(World world, Random rand, BlockPos pos, int height, int offsetX, int offsetY, int offsetZ, int i, int j, boolean bl, int k, double randSine, boolean placeSnow, IBlockState state) {
        double d = bl ? this.getDistance(offsetX, offsetZ, BlockPos.ORIGIN, j, this.decreaseValueNearTop(offsetY, height, k), randSine) : this.method_13421(offsetX, offsetZ, BlockPos.ORIGIN, i, rand);
        if (d < 0.0) {
            BlockPos blockPos = pos.add(offsetX, offsetY, offsetZ);
            double e = bl ? -0.5 : (double)(-6 - rand.nextInt(3));
            if (d > e && rand.nextDouble() > 0.9) {
                return;
            }
            this.placeBlockOrSnow(blockPos, world, rand, height - offsetY, height, bl, placeSnow, state);
        }
    }

    private void placeBlockOrSnow(BlockPos pos, World world, Random rand, int heightRemaining, int height, boolean lessSnow, boolean placeSnow, IBlockState state) {
        IBlockState blockState = world.getBlockState(pos);
        if (world.isAirBlock(pos) || blockState.getBlock() == Blocks.SNOW || blockState.getBlock() == Blocks.ICE || blockState.getBlock() == Blocks.WATER) {
            boolean bl = !lessSnow || rand.nextDouble() > 0.05;
            int i = lessSnow ? 3 : 2;
            if (placeSnow && blockState.getBlock() != Blocks.WATER && (double)heightRemaining <= (double)rand.nextInt(Math.max(1, height / i)) + (double)height * 0.6 && bl) {
                this.setBlockAndNotifyAdequately(world, pos, Blocks.SNOW.getDefaultState());
            } else {
                this.setBlockAndNotifyAdequately(world, pos, state);
            }
        }
    }

    private int decreaseValueNearTop(int y, int height, int value) {
        int i = value;
        if (y > 0 && height - y <= 3) {
            i = value - (4 - (height - y));
        }
        return i;
    }

    private double method_13421(int x, int z, BlockPos pos, int i, Random rand) {
        float f = 10.0F * MathHelper.clamp(rand.nextFloat(), 0.2F, 0.8F) / (float)i;
        return (double)f + Math.pow((x - pos.getX()), 2.0) + Math.pow((z - pos.getZ()), 2.0) - Math.pow((double)i, 2.0);
    }

    private double getDistance(int x, int z, BlockPos pos, int divisor1, int divisor2, double randSine) {
        return Math.pow(((double)(x - pos.getX()) * Math.cos(randSine) - (double)(z - pos.getZ()) * Math.sin(randSine)) / (double)divisor1, 2.0) + Math.pow(((double)(x - pos.getX()) * Math.sin(randSine) + (double)(z - pos.getZ()) * Math.cos(randSine)) / (double)divisor2, 2.0) - 1.0;
    }

    private int method_13419(Random rand, int y, int height, int factor) {
        float f = 3.5F - rand.nextFloat();
        float g = (1.0F - (float)Math.pow(y, 2.0) / ((float)height * f)) * (float)factor;
        if (height > 15 + rand.nextInt(5)) {
            int i = y < 3 + rand.nextInt(6) ? y / 2 : y;
            g = (1.0F - (float)i / ((float)height * f * 0.4F)) * (float)factor;
        }
        return MathHelper.ceil(g / 2.0F);
    }

    private int method_13417(int y, int height, int factor) {
        float g = (1.0F - (float)Math.pow(y, 2.0) / ((float)height * 1.0F)) * (float)factor;
        return MathHelper.ceil(g / 2.0F);
    }

    private int method_13427(Random rand, int y, int height, int factor) {
        float f = 1.0F + rand.nextFloat() / 2.0F;
        float g = (1.0F - (float)y / ((float)height * f)) * (float)factor;
        return MathHelper.ceil(g / 2.0F);
    }

    private static boolean isSnowOrIce(IBlockState state) {
        return state.getBlock() == Blocks.PACKED_ICE || state.getBlock() == Blocks.SNOW || state.getBlock() == ModBlocks.BLUE_ICE;
    }

    private boolean isAirBelow(World world, BlockPos pos) {
        return world.isAirBlock(pos.down());
    }

    private void method_13418(World world, BlockPos pos, int i, int height, boolean bl, int j) {
        int k = bl ? j : i / 2;
        for (int l = -k; l <= k; l++) {
            for (int m = -k; m <= k; m++) {
                for (int n = 0; n <= height; n++) {
                    BlockPos blockPos = pos.add(l, n, m);
                    IBlockState blockState = world.getBlockState(blockPos);
                    if (isSnowOrIce(blockState) || blockState.getBlock() == Blocks.SNOW) {
                        if (this.isAirBelow(world, blockPos)) {
                            this.setBlockAndNotifyAdequately(world, blockPos, Blocks.AIR.getDefaultState());
                            this.setBlockAndNotifyAdequately(world, blockPos.up(), Blocks.AIR.getDefaultState());
                        } else if (isSnowOrIce(blockState)) {
                            IBlockState[] blockStates = new IBlockState[]{
                                world.getBlockState(blockPos.west()),
                                world.getBlockState(blockPos.east()),
                                world.getBlockState(blockPos.north()),
                                world.getBlockState(blockPos.south())
                            };
                            int o = 0;
                            for (IBlockState blockState2 : blockStates) {
                                if (!isSnowOrIce(blockState2)) {
                                    o++;
                                }
                            }
                            if (o >= 3) {
                                this.setBlockAndNotifyAdequately(world, blockPos, Blocks.AIR.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }
}
