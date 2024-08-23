package surreal.backportium.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockPlantDouble extends BlockBush implements IShearable {

    protected static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = BlockDoublePlant.HALF;

    public BlockPlantDouble(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setTickRandomly(false);
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER));
    }

    public BlockPlantDouble(Material material) {
        this(material, material.getMaterialMapColor());
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            boolean isTop = state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
            if (isTop) {
                worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
            }
            else if (worldIn.getBlockState(pos.up()).getBlock() != this || !worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(), 3);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getBlock() != this) return super.canBlockStay(worldIn, pos, state); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            return worldIn.getBlockState(pos.down()).getBlock() == this;
        } else {
            IBlockState iblockstate = worldIn.getBlockState(pos.up());
            return iblockstate.getBlock() == this && super.canBlockStay(worldIn, pos.up(), iblockstate) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        if (state.getBlock() == this) {
            return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.LOWER;
        }
        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    @Override
    protected boolean canSustainBush(@Nonnull IBlockState state) {
        if (state.getBlock() == this) {
            return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.LOWER;
        }
        return true;
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, @Nonnull Random rand, int fortune) {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            return Items.AIR;
        } else return super.getItemDropped(state, rand, fortune);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    }

    public void place(World world, BlockPos pos, IBlockState state) {
        if (world.setBlockState(pos, this.getDefaultState(), 3))
            world.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta == 1 ? getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER) : getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? 1 : 0;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF);
    }

    @Nonnull
    @Override
    public EnumOffsetType getOffsetType() {
        return EnumOffsetType.XZ;
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this));
    }
}
