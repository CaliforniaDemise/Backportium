package surreal.backportium.block.plant.coral;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class BlockCoralFan extends BlockCoral {

    protected static final PropertyDirection FACING = BlockDirectional.FACING;

    public static final AxisAlignedBB DOWN_AABB, UP_AABB, NORTH_AABB, SOUTH_AABB, EAST_AABB, WEST_AABB;

    public BlockCoralFan(Material material) {
        this(material, material.getMaterialMapColor());
    }

    public BlockCoralFan(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.UP));
    }

    @Nonnull
    @Override
    protected ItemStack getSilkTouchDrop(@Nonnull IBlockState state) {
        return new ItemStack(this, 1, state.getValue(ALIVE) ? 0 : 8);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        switch (facing) {
            default: return UP_AABB;
            case DOWN: return DOWN_AABB;
            case WEST: return WEST_AABB;
            case EAST: return EAST_AABB;
            case NORTH: return NORTH_AABB;
            case SOUTH: return SOUTH_AABB;
        }
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = getStateFromMeta(meta);
        state = state.withProperty(FACING, facing);
        return state;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        BlockPos position = pos.offset(side.getOpposite());
        IBlockState state = worldIn.getBlockState(position);
        return state.getBlock().isReplaceable(worldIn, position) || (state.getBlockFaceShape(worldIn, position, side) == BlockFaceShape.SOLID && state.getBlock().canSustainPlant(state, worldIn, position, side, this));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos position = pos.offset(facing.getOpposite());
        IBlockState soil = worldIn.getBlockState(position);
        return soil.getBlock().canSustainPlant(soil, worldIn, position, facing, this) && soil.getBlockFaceShape(worldIn, position, facing) == BlockFaceShape.SOLID;
    }

    protected final EnumFacing getFaceFromPos(BlockPos pos, BlockPos fromPos) {
        if (fromPos.getY() > pos.getY()) return EnumFacing.UP;
        if (fromPos.getY() < pos.getY()) return EnumFacing.DOWN;
        if (fromPos.getX() > pos.getX()) return EnumFacing.SOUTH;
        if (fromPos.getX() < pos.getX()) return EnumFacing.NORTH;
        if (fromPos.getZ() > pos.getZ()) return EnumFacing.EAST;
        if (fromPos.getZ() < pos.getZ()) return EnumFacing.WEST;
        return EnumFacing.UP;
    }

    static {
        float twoPix = 1F / 8;
        float aPix = twoPix / 2;

        float fourPix = twoPix * 2;
        float fivePix = aPix * 5;

        float twelvePix = 1F - fourPix;
        float fourteenPix = 1F - twoPix;

        UP_AABB = new AxisAlignedBB(twoPix, 0, twoPix, fourteenPix, fourPix, fourteenPix);
        DOWN_AABB = new AxisAlignedBB(twoPix, 1F, twoPix, fourteenPix, twelvePix, fourteenPix);
        NORTH_AABB = new AxisAlignedBB(0, fourPix, fivePix, 1F, twelvePix, 1F);
        SOUTH_AABB = new AxisAlignedBB(0, fourPix, 0, 1F, twelvePix, 1F - fivePix);
        EAST_AABB = new AxisAlignedBB(0, fourPix, 0, 1F - fivePix, twelvePix, 1F);
        WEST_AABB = new AxisAlignedBB(fivePix, fourPix, 0, 1F, twelvePix, 1F);
    }
}
