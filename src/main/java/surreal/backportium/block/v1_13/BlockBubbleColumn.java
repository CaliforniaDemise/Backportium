package surreal.backportium.block.v1_13;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.client.particle.ParticleBubbleColumnUp;
import surreal.backportium.client.particle.ParticleCurrentDown;
import surreal.backportium.entity.v1_13.EntityTrident;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

// From Aqua Acrobatics
public class BlockBubbleColumn extends BlockStaticLiquid {

    public static final PropertyBool DRAG = PropertyBool.create("drag"); /* true: upwards, false: downwards */

    public BlockBubbleColumn() {
        super(Material.WATER);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0).withProperty(DRAG, false));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this).add(LEVEL, DRAG);
        if(Loader.isModLoaded("fluidlogged_api")) builder = builder.add(BlockFluidBase.FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0]));
        return builder.build();
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        IBlockState upState = worldIn.getBlockState(pos.up());
        boolean downwards = !state.getValue(DRAG);
        if (entityIn instanceof EntityBoat) {
            // TODO Handle rocking... With POSE??!?!?!?
        }
        else if (!(entityIn instanceof EntityTrident)) {
            if (upState.getBlock() == Blocks.AIR) {
                if (downwards) entityIn.motionY = Math.max(-0.9, entityIn.motionY - 0.03D);
                else entityIn.motionY = Math.min(1.8D, entityIn.motionY + 0.1D);
            }
            else {
                if (!downwards) entityIn.motionY = Math.min(0.7D, entityIn.motionY + 0.06D);
                else entityIn.motionY = Math.max(-0.3D, entityIn.motionY - 0.03D);
                entityIn.fallDistance = 0.0F;
            }
        }
//        IBubbleColumnInteractable bubbleEntity = (IBubbleColumnInteractable)entityIn;
//        boolean downwards = !state.getValue(DRAG);
//        if (iblockstate.getBlock() == Blocks.AIR) {
//            bubbleEntity.onEnterBubbleColumnWithAirAbove(downwards);
//        } else {
//            bubbleEntity.onEnterBubbleColumn(downwards);
//        }
    }

    public static void placeBubbleColumn(World world, BlockPos pos, boolean isUpwards) {
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

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, @Nonnull World worldIn, BlockPos pos, @Nonnull Random rand) {
        double d0 = pos.getX();
        double d1 = pos.getY();
        double d2 = pos.getZ();
        Minecraft mc = Minecraft.getMinecraft();
        if (!stateIn.getValue(DRAG)) {
            mc.effectRenderer.addEffect(new ParticleCurrentDown(worldIn, d0 + 0.5D, d1 + 0.8D, d2));
        } else {
            mc.effectRenderer.addEffect(new ParticleBubbleColumnUp(worldIn, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D));
            mc.effectRenderer.addEffect(new ParticleBubbleColumnUp(worldIn, d0 + (double)rand.nextFloat(), d1 + (double)rand.nextFloat(), d2 + (double)rand.nextFloat(), 0.0D, 0.04D, 0.0D));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
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
        if (block == ModBlocks.BUBBLE_COLUMN) return iblockstate.getValue(DRAG);
        else return block == Blocks.SOUL_SAND;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos.down()));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        placeBubbleColumn(worldIn, pos.up(), getDrag(worldIn, pos));
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, 0).withProperty(DRAG, (meta & 1) == 1);
    }

    @Override
    public int tickRate(@Nonnull World worldIn) {
        return 5;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DRAG) ? 1 : 0;
    }
}
