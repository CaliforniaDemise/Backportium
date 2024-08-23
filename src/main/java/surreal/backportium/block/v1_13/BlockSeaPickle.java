package surreal.backportium.block.v1_13;

import net.minecraft.block.IGrowable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import surreal.backportium.api.block.CoralBlock;
import surreal.backportium.block.BlockClustered;
import surreal.backportium.util.MutBlockPos;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockSeaPickle extends BlockClustered implements IGrowable {

    public static final PropertyBool ALIVE = PropertyBool.create("alive");

    public static AxisAlignedBB ONE_AABB, TWO_AABB, THREE_AABB;

    public BlockSeaPickle(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setTickRandomly(true);
        this.setDefaultState(getDefaultState().withProperty(ALIVE, true));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!WorldHelper.inWater(worldIn, pos)) {
            worldIn.setBlockState(pos, state.withProperty(ALIVE, false), 3);
        }
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int amount = state.getValue(AMOUNT);
        switch (amount) {
            case 0: return ONE_AABB;
            case 1: return TWO_AABB;
            default: return THREE_AABB;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (!state.getValue(ALIVE)) return 0;
        return 6 + state.getValue(AMOUNT) * 3;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(ALIVE) && WorldHelper.inWater(worldIn, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        IBlockState stateDown = worldIn.getBlockState(pos.down());

        // TODO Replace with coral block
        if (stateDown.getBlock() instanceof CoralBlock) {
            CoralBlock coralBlock = (CoralBlock) state.getBlock();
            if (!coralBlock.canPickleGrow(worldIn, pos, stateDown)) return;

            if (state.getValue(AMOUNT) < 3) {
                handleEntities(worldIn, rand, pos);
                worldIn.setBlockState(pos, state.withProperty(AMOUNT, 3));
            }

            MutBlockPos mutPos = new MutBlockPos(pos);
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                tryGrow(worldIn, rand, mutPos.move(facing));
                if (facing.getAxis() == EnumFacing.Axis.Z) {
                    tryGrow(worldIn, rand, mutPos.offset(EnumFacing.EAST));
                    tryGrow(worldIn, rand, mutPos.offset(EnumFacing.WEST));
                }
                tryGrow(worldIn, rand, mutPos.move(facing));
                mutPos.setPos(pos);
            }
        }
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        boolean alive = WorldHelper.inWater(world, pos);
        return getDefaultState().withProperty(ALIVE, alive);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int amount = state.getValue(ALIVE) ? 0 : 4;
        return super.getMetaFromState(state) | amount;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ALIVE, (meta & 4) == 0).withProperty(AMOUNT, meta & 3);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
       return new BlockStateContainer(this, ALIVE, BlockClustered.AMOUNT);
    }

    private void handleEntities(World world, Random rand, BlockPos pos) {
        List<Entity> list = WorldHelper.getEntitiesInBlock(world, pos, Entity.class);
        if (!list.isEmpty()) {
            list.forEach(e -> e.posY += 0.1D);
        }
    }

    private void tryGrow(World worldIn, Random rand, BlockPos pos) {
        if (!isPlaceable(worldIn, pos)) pos = pos.down();
        if (!isPlaceable(worldIn, pos)) return;

        IBlockState stateDown = worldIn.getBlockState(pos.down());

        if (stateDown.getBlock() instanceof CoralBlock) {
            CoralBlock coralBlock = (CoralBlock) stateDown.getBlock();
            if (!coralBlock.canPickleGrow(worldIn, pos, stateDown)) return;
            int amount = rand.nextInt(5);
            if (amount > 0) {
                handleEntities(worldIn, rand, pos);
                worldIn.setBlockState(pos, getDefaultState().withProperty(AMOUNT, amount - 1));
            }
        }
    }

    private boolean isPlaceable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isReplaceable(world, pos) && WorldHelper.inWater(world, pos) && canPlaceBlockAt(world, pos);
    }

    static {
        double twoPix = 1F / 8;

        double sixPix = twoPix * 3;
        double tenPix = 1F - sixPix;

        double threePix = sixPix / 2;
        double thirteenPix = 1F - threePix;

        double fourteenPix = 1F - twoPix;

        ONE_AABB = new AxisAlignedBB(sixPix, 0F, sixPix, tenPix, sixPix, tenPix);
        TWO_AABB = new AxisAlignedBB(threePix, 0F, threePix, thirteenPix, sixPix, thirteenPix);
        THREE_AABB = new AxisAlignedBB(twoPix, 0F, twoPix, fourteenPix, sixPix, fourteenPix);
    }
}