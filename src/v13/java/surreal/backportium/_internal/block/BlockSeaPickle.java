package surreal.backportium._internal.block;

import net.minecraft.block.IGrowable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium.api.block.Loggable;
import surreal.backportium.block.BlockClustered;
import surreal.backportium.integration.ModList;
import surreal.backportium.tag.AllTags;
import surreal.backportium.util.BlockUtil;
import surreal.backportium.util.FluidUtil;
import surreal.backportium.util.WorldUtil;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockSeaPickle extends BlockClustered implements IGrowable, ModelProvider {

    public static final PropertyBool ALIVE = PropertyBool.create("alive");

    public static AxisAlignedBB ONE_AABB, TWO_AABB, THREE_AABB;

    public BlockSeaPickle() {
        super(ModList.FLUIDLOGGED ? Material.PLANTS : Material.GRASS, MapColor.GREEN);
        this.setTickRandomly(true);
        this.setDefaultState(getDefaultState().withProperty(ALIVE, true));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public void updateTick(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
        if (FluidUtil.getFluid(Loggable.cast(this).getLoggedState(worldIn, pos, state)) != FluidRegistry.WATER) {
            worldIn.setBlockState(pos, state.withProperty(ALIVE, false), 3);
        }
    }

    @NotNull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos) {
        int amount = state.getValue(AMOUNT);
        switch (amount) {
            case 0: return ONE_AABB;
            case 1: return TWO_AABB;
            default: return THREE_AABB;
        }
    }

    @Override
    public int getLightValue(IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
        if (!state.getValue(ALIVE)) return 0;
        return 6 + state.getValue(AMOUNT) * 3;
    }

    @Override
    public boolean canGrow(@NotNull World worldIn, @NotNull BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(ALIVE) && FluidUtil.getFluid(Loggable.cast(this).getLoggedState(worldIn, pos, state)) == FluidRegistry.WATER;
    }

    @Override
    public boolean canUseBonemeal(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos pos, @NotNull IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, @NotNull Random rand, BlockPos pos, @NotNull IBlockState state) {
        IBlockState stateDown = worldIn.getBlockState(pos.down());
        if (stateDown.getMaterial() == Material.CORAL) {
            if (state.getValue(AMOUNT) < 3) {
                handleEntities(worldIn, rand, pos);
                worldIn.setBlockState(pos, state.withProperty(AMOUNT, 3));
            }
            BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(pos);
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

    @NotNull
    @Override
    public IBlockState getStateForPlacement(@NotNull World world, @NotNull BlockPos pos, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @NotNull EntityLivingBase placer, @NotNull EnumHand hand) {
        boolean alive = FluidUtil.getFluid(Loggable.cast(this).getLoggedState(world, pos, world.getBlockState(pos))) == FluidRegistry.WATER;
        return getDefaultState().withProperty(ALIVE, alive);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int amount = state.getValue(ALIVE) ? 0 : 4;
        return super.getMetaFromState(state) | amount;
    }

    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ALIVE, (meta & 4) == 0).withProperty(AMOUNT, meta & 3);
    }

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ALIVE, BlockClustered.AMOUNT);
    }

    private void handleEntities(World world, Random rand, BlockPos pos) {
        List<Entity> list = WorldUtil.getEntitiesInBlock(world, pos, Entity.class);
        if (!list.isEmpty()) {
            list.forEach(e -> e.posY += 0.1D);
        }
    }

    private void tryGrow(World worldIn, Random rand, BlockPos pos) {
        if (!isPlaceable(worldIn, pos)) pos = pos.down();
        if (!isPlaceable(worldIn, pos)) return;
        IBlockState stateDown = worldIn.getBlockState(pos.down());
        if (AllTags.BLOCK_TAG.contains(AllTags.BLOCK_CAN_GROW_SEA_PICKLE, stateDown)) {
            int amount = rand.nextInt(5);
            if (amount > 0) {
                handleEntities(worldIn, rand, pos);
                worldIn.setBlockState(pos, getDefaultState().withProperty(AMOUNT, amount - 1));
            }
        }
    }

    private boolean isPlaceable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isReplaceable(world, pos) && FluidUtil.getFluid(Loggable.cast(this).getLoggedState(world, pos, state)) == FluidRegistry.WATER && canPlaceBlockAt(world, pos);
    }

    @Override
    public void registerModels() {
        Item item = BlockUtil.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory"));
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
