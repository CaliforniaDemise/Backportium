package surreal.backportium.block.plant.coral;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.enums.CoralType;
import surreal.backportium.block.v1_13.BlockSeaPickle;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockCoral extends BlockBush implements FluidLogged {

    public static final PropertyEnum<CoralType> VARIANT = PropertyEnum.create("variant", CoralType.class);
    public static final PropertyBool ALIVE = BlockSeaPickle.ALIVE;

    public static final AxisAlignedBB CORAL_AABB;

    public BlockCoral(Material material) {
        this(material, material.getMaterialMapColor());
    }

    public BlockCoral(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CORAL_AABB;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
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
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public int tickRate(World worldIn) {
        return 20 + worldIn.rand.nextInt(11);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!WorldHelper.inWater(worldIn, pos)) {
            worldIn.setBlockState(pos, state.withProperty(ALIVE, false));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected boolean canSustainBush(IBlockState state) {
        return state.isNormalCube();
    }

    static {
        float twoPix = 1F / 8;
        float fourteenPix = 1F - twoPix;
        float fifteenPix = 1F - (1F / 16);

        CORAL_AABB = new AxisAlignedBB(twoPix, 0, twoPix, fourteenPix, fifteenPix, fourteenPix);
    }
}
