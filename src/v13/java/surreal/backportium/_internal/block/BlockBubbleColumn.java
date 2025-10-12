package surreal.backportium._internal.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.client.renderer.model.StateMapProvider;
import surreal.backportium.api.entity.BubbleColumnInteractable;
import surreal.backportium.client.particle.ParticleBubbleColumnDown;
import surreal.backportium.client.particle.ParticleBubbleColumnUp;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.integration.ModList;

import java.util.Random;

public class BlockBubbleColumn extends BlockStaticLiquid implements StateMapProvider {

    public static final PropertyBool DRAG = PropertyBool.create("drag"); /* true: upwards, false: downwards */

    public BlockBubbleColumn() {
        super(Material.WATER);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0).withProperty(DRAG, false));
    }

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this).add(LEVEL, DRAG);
        if(ModList.FLUIDLOGGED) builder = builder.add(BlockFluidBase.FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0]));
        return builder.build();
    }


    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, @NotNull Entity entityIn) {
        IBlockState iblockstate = worldIn.getBlockState(pos.up());
        BubbleColumnInteractable bubbleEntity = BubbleColumnInteractable.cast(entityIn);
        boolean downwards = !state.getValue(DRAG);
        if (iblockstate.getBlock() == Blocks.AIR) bubbleEntity.onBubbleColumn(state, downwards);
        else bubbleEntity.inBubbleColumn(state, downwards);
    }

    private static void placeBubbleColumn(World world, BlockPos pos, boolean isUpwards) {
        if (canHoldBubbleColumn(world, pos)) {
            world.setBlockState(pos, ModBlocks.BUBBLE_COLUMN.getDefaultState().withProperty(DRAG, isUpwards), Constants.BlockFlags.SEND_TO_CLIENTS);
        }
    }

    public static boolean canHoldBubbleColumn(World world, BlockPos pos) {
        if(world.provider.doesWaterVaporize()) return false;
        IBlockState self = world.getBlockState(pos);
        if(self.getMaterial() != Material.WATER) return false;
        if(!(self.getBlock() instanceof BlockLiquid)) return false;
        return self.getValue(LEVEL) == 0;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(@NotNull IBlockState stateIn, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Random rand) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!stateIn.getValue(DRAG)) {
            mc.effectRenderer.addEffect(new ParticleBubbleColumnDown(worldIn, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ()));
        } else {
            mc.effectRenderer.addEffect(new ParticleBubbleColumnUp(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0D, 0.04D, 0.0D));
            mc.effectRenderer.addEffect(new ParticleBubbleColumnUp(worldIn, pos.getX() + (double) rand.nextFloat(), pos.getY() + (double) rand.nextFloat(), pos.getZ() + (double) rand.nextFloat(), 0.0D, 0.04D, 0.0D));
        }
    }

    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos) {
        if (!this.isValidPosition(worldIn, pos)) {
            worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
            return;
        }
        if (fromPos.up().equals(pos)) {
            worldIn.setBlockState(pos, ModBlocks.BUBBLE_COLUMN.getDefaultState().withProperty(DRAG, getDrag(worldIn, fromPos)), Constants.BlockFlags.SEND_TO_CLIENTS);
        } else if (fromPos.down().equals(pos) && worldIn.getBlockState(fromPos).getBlock() != this && canHoldBubbleColumn(worldIn, fromPos)) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }
        if(fromPos.getX() != pos.getX() || fromPos.getZ() != pos.getZ())
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    public boolean isValidPosition(World worldIn, BlockPos pos) {
        Block block = worldIn.getBlockState(pos.down()).getBlock();
        return block == this || block == (worldIn.getBlockState(pos).getValue(DRAG) ? Blocks.SOUL_SAND : Blocks.MAGMA);
    }

    private static boolean getDrag(IBlockAccess p_203157_0_, BlockPos p_203157_1_) {
        IBlockState iblockstate = p_203157_0_.getBlockState(p_203157_1_);
        Block block = iblockstate.getBlock();
        if (block == ModBlocks.BUBBLE_COLUMN) {
            return iblockstate.getValue(DRAG);
        } else {
            return block == Blocks.SOUL_SAND;
        }
    }

    @Override
    public void onBlockAdded(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos.down()));
    }

    @Override
    public int tickRate(@NotNull World worldIn) {
        return 5;
    }

    @Override
    public void updateTick(@NotNull World worldIn, BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
        placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos));
    }

    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, 0).withProperty(DRAG, (meta & 1) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DRAG) ? 1 : 0;
    }

    @Override
    public void registerStateMap() {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(BlockBubbleColumn.DRAG, BlockBubbleColumn.LEVEL).build());
    }
}
