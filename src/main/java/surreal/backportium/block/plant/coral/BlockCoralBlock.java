package surreal.backportium.block.plant.coral;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import surreal.backportium.api.enums.CoralType;
import surreal.backportium.block.BlockDef;
import surreal.backportium.block.v13.BlockSeaPickle;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
public abstract class BlockCoralBlock extends BlockDef {

    public static final PropertyEnum<CoralType> VARIANT = BlockCoral.VARIANT;
    public static final PropertyBool ALIVE = BlockSeaPickle.ALIVE;

    public BlockCoralBlock(Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    public BlockCoralBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
        this.setHardness(1.5F).setResistance(6F);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setDefaultState(this.getDefaultState().withProperty(ALIVE, true));
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, this.getMetaFromState(state));
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).ordinal() | 8;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Nonnull
    @Override
    protected ItemStack getSilkTouchDrop(@Nonnull IBlockState state) {
        return new ItemStack(this, 1, this.getMetaFromState(state));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public int tickRate(World worldIn) {
        return 20 + worldIn.rand.nextInt(11);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!hasWater(worldIn, pos)) {
            worldIn.setBlockState(pos, state.withProperty(ALIVE, false));
        }
    }

    protected final boolean hasWater(World world, BlockPos pos) {
        for (int i = 0; i < 6; i++) {
            EnumFacing facing = EnumFacing.byIndex(i);
            if (WorldHelper.isWater(world, pos.offset(facing))) return true;
        }
        return false;
    }
}
