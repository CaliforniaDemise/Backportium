package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.init.ModSoundTypes;
import surreal.backportium.util.FluidUtil;

import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockCoralBlock extends Block {

    public BlockCoralBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setSoundType(ModSoundTypes.CORAL);
    }

    @NotNull
    @Override
    public SoundType getSoundType(@NotNull IBlockState state, @NotNull World world, @NotNull BlockPos pos, @Nullable Entity entity) {
        if (this.getAliveVariant() != Blocks.AIR) return SoundType.STONE;
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public void onBlockAdded(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public int tickRate(World worldIn) {
        return 20 + worldIn.rand.nextInt(11);
    }

    @Override
    public void updateTick(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @Nullable Random rand) {
        Block deadVariant = getDeadVariant();
        if (deadVariant != Blocks.AIR && !FluidUtil.surroundedByWater(worldIn, pos)) {
            worldIn.setBlockState(pos, deadVariant.getDefaultState());
        }
    }

    @NotNull
    public Block getDeadVariant() {
        if (this == ModBlocks.TUBE_CORAL_BLOCK) return ModBlocks.DEAD_TUBE_CORAL_BLOCK;
        if (this == ModBlocks.BRAIN_CORAL_BLOCK) return ModBlocks.DEAD_BRAIN_CORAL_BLOCK;
        if (this == ModBlocks.BUBBLE_CORAL_BLOCK) return ModBlocks.DEAD_BUBBLE_CORAL_BLOCK;
        if (this == ModBlocks.FIRE_CORAL_BLOCK) return ModBlocks.DEAD_FIRE_CORAL_BLOCK;
        if (this == ModBlocks.HORN_CORAL_BLOCK) return ModBlocks.DEAD_HORN_CORAL_BLOCK;
        return Blocks.AIR;
    }

    @NotNull
    public Block getAliveVariant() {
        if (this == ModBlocks.DEAD_TUBE_CORAL_BLOCK) return ModBlocks.TUBE_CORAL_BLOCK;
        if (this == ModBlocks.DEAD_BRAIN_CORAL_BLOCK) return ModBlocks.BRAIN_CORAL_BLOCK;
        if (this == ModBlocks.DEAD_BUBBLE_CORAL_BLOCK) return ModBlocks.BUBBLE_CORAL_BLOCK;
        if (this == ModBlocks.DEAD_FIRE_CORAL_BLOCK) return ModBlocks.FIRE_CORAL_BLOCK;
        if (this == ModBlocks.DEAD_HORN_CORAL_BLOCK) return ModBlocks.HORN_CORAL_BLOCK;
        return Blocks.AIR;
    }
}
