package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.util.BlockUtil;

import java.util.Objects;

public class BlockCoralFan extends BlockCoral implements ModelProvider {

    protected static final PropertyDirection FACING = BlockDirectional.FACING;

    public static final AxisAlignedBB DOWN_AABB, UP_AABB, NORTH_AABB, SOUTH_AABB, EAST_AABB, WEST_AABB;

    public BlockCoralFan(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.UP));
    }

    @NotNull
    @Override
    protected ItemStack getSilkTouchDrop(@NotNull IBlockState state) {
        return new ItemStack(this);
    }

    @NotNull
    @Override
    public AxisAlignedBB getBoundingBox(@Nullable IBlockState state, @Nullable IBlockAccess source, @Nullable BlockPos pos) {
        if (state == null && (source == null || pos == null)) return Block.FULL_BLOCK_AABB;
        else if (state == null) state = source.getBlockState(pos);
        EnumFacing facing = state.getValue(FACING);
        switch (facing) {
            case DOWN: return DOWN_AABB;
            case WEST: return WEST_AABB;
            case EAST: return EAST_AABB;
            case NORTH: return NORTH_AABB;
            case SOUTH: return SOUTH_AABB;
            case UP:
            default: return UP_AABB;
        }
    }

    @NotNull
    @Override
    public IBlockState getStateForPlacement(@Nullable World world, @Nullable BlockPos pos, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nullable EntityLivingBase placer, @Nullable EnumHand hand) {
        IBlockState state = this.getDefaultState();
        state = state.withProperty(FACING, facing);
        return state;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        BlockPos position = pos.offset(side.getOpposite());
        IBlockState state = worldIn.getBlockState(position);
        return state.getBlock().isReplaceable(worldIn, position) || (state.getBlockFaceShape(worldIn, position, side) == BlockFaceShape.SOLID && state.getBlock().canSustainPlant(state, worldIn, position, side, this));
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos position = pos.offset(facing.getOpposite());
        IBlockState soil = worldIn.getBlockState(position);
        return soil.getBlock().canSustainPlant(soil, worldIn, position, facing, this) && soil.getBlockFaceShape(worldIn, position, facing) == BlockFaceShape.SOLID;
    }

    @NotNull
    @Override
    public ItemStack getPickBlock(@NotNull IBlockState state, @Nullable RayTraceResult target, @Nullable World world, @Nullable BlockPos pos, @Nullable EntityPlayer player) {
        return new ItemStack(this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        return facing.getIndex();
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public void registerModels() {
        Item item = BlockUtil.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
    }

    @NotNull
    @Override
    public Block getDeadVariant() {
        if (this == ModBlocks.TUBE_CORAL_FAN) return ModBlocks.DEAD_TUBE_CORAL_FAN;
        if (this == ModBlocks.BRAIN_CORAL_FAN) return ModBlocks.DEAD_BRAIN_CORAL_FAN;
        if (this == ModBlocks.BUBBLE_CORAL_FAN) return ModBlocks.DEAD_BUBBLE_CORAL_FAN;
        if (this == ModBlocks.FIRE_CORAL_FAN) return ModBlocks.DEAD_FIRE_CORAL_FAN;
        if (this == ModBlocks.HORN_CORAL_FAN) return ModBlocks.DEAD_HORN_CORAL_FAN;
        return Blocks.AIR;
    }

    @NotNull
    @Override
    public Block getAliveVariant() {
        if (this == ModBlocks.DEAD_TUBE_CORAL_FAN) return ModBlocks.TUBE_CORAL_FAN;
        if (this == ModBlocks.DEAD_BRAIN_CORAL_FAN) return ModBlocks.BRAIN_CORAL_FAN;
        if (this == ModBlocks.DEAD_BUBBLE_CORAL_FAN) return ModBlocks.BUBBLE_CORAL_FAN;
        if (this == ModBlocks.DEAD_FIRE_CORAL_FAN) return ModBlocks.FIRE_CORAL_FAN;
        if (this == ModBlocks.DEAD_HORN_CORAL_FAN) return ModBlocks.HORN_CORAL_FAN;
        return Blocks.AIR;
    }

    static {
        double twoPix = 1.0 / 8.0;
        double aPix = twoPix / 2.0;
        double fourPix = twoPix * 2.0;
        double fivePix = aPix * 5.0;
        double twelvePix = 1.0 - fourPix;
        double fourteenPix = 1.0 - twoPix;
        UP_AABB = new AxisAlignedBB(twoPix, 0.0, twoPix, fourteenPix, fourPix, fourteenPix);
        DOWN_AABB = new AxisAlignedBB(twoPix, 1.0, twoPix, fourteenPix, twelvePix, fourteenPix);
        NORTH_AABB = new AxisAlignedBB(0.0, fourPix, fivePix, 1.0, twelvePix, 1.0);
        SOUTH_AABB = new AxisAlignedBB(0.0, fourPix, 0.0, 1.0, twelvePix, 1.0 - fivePix);
        EAST_AABB = new AxisAlignedBB(0.0, fourPix, 0.0, 1.0 - fivePix, twelvePix, 1.0);
        WEST_AABB = new AxisAlignedBB(fivePix, fourPix, 0.0, 1.0, twelvePix, 1.0);
    }
}
