package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium.api.block.Loggable;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.init.ModSoundTypes;
import surreal.backportium.util.BlockUtil;
import surreal.backportium.util.FluidUtil;

import java.util.Objects;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockCoral extends BlockBush implements Loggable, ModelProvider {

    public static final AxisAlignedBB CORAL_AABB = new AxisAlignedBB(1.0 / 8.0, 0.0, 1.0 / 8.0, 1.0 - 1.0 / 8.0, 1.0 - 1.0 / 16.0, 1.0 - 1.0 / 16.0);

    public BlockCoral(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setSoundType(ModSoundTypes.CORAL);
    }

    @NotNull
    @Override
    public SoundType getSoundType(@NotNull IBlockState state, @NotNull World world, @NotNull BlockPos pos, @Nullable Entity entity) {
        if (this.getAliveVariant() != Blocks.AIR) return SoundType.STONE;
        return super.getSoundType(state, world, pos, entity);
    }

    @NotNull
    @Override
    public ItemStack getPickBlock(@NotNull IBlockState state, @Nullable RayTraceResult target, @Nullable World world, @Nullable BlockPos pos, @Nullable EntityPlayer player) {
        return new ItemStack(this, 1, this.getMetaFromState(state));
    }

    @NotNull
    @Override
    public AxisAlignedBB getBoundingBox(@Nullable IBlockState state, @Nullable IBlockAccess source, @Nullable BlockPos pos) {
        return CORAL_AABB;
    }

    @NotNull
    @Override
    public Item getItemDropped(@Nullable IBlockState state, @Nullable Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public void onBlockAdded(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.checkAndDropBlock(worldIn, pos, state);
        }
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public int tickRate(World worldIn) {
        return 20 + worldIn.rand.nextInt(11);
    }

    @Override
    public void updateTick(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @Nullable Random rand) {
        Block deadVariant = this.getDeadVariant();
        if (deadVariant != Blocks.AIR && FluidUtil.getFluid(Loggable.cast(this).getLoggedState(worldIn, pos, state)) != FluidRegistry.WATER) {
            worldIn.setBlockState(pos, deadVariant.getDefaultState());
        }
    }

    @Override
    public boolean canBlockStay(@NotNull World worldIn, BlockPos pos, @NotNull IBlockState state) {
        BlockPos downPos = pos.down();
        return super.canBlockStay(worldIn, pos, state) && worldIn.getBlockState(downPos).isSideSolid(worldIn, downPos, EnumFacing.UP);
    }

    @Override
    public boolean isSideSolid(@Nullable IBlockState base_state, @Nullable IBlockAccess world, @Nullable BlockPos pos, @Nullable EnumFacing side) {
        return true;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.isTopSolid();
    }

    @Override
    public void registerModels() {
        Item item = BlockUtil.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory"));
    }

    @NotNull
    public Block getDeadVariant() {
        if (this == ModBlocks.TUBE_CORAL) return ModBlocks.DEAD_TUBE_CORAL;
        if (this == ModBlocks.BRAIN_CORAL) return ModBlocks.DEAD_BRAIN_CORAL;
        if (this == ModBlocks.BUBBLE_CORAL) return ModBlocks.DEAD_BUBBLE_CORAL;
        if (this == ModBlocks.FIRE_CORAL) return ModBlocks.DEAD_FIRE_CORAL;
        if (this == ModBlocks.HORN_CORAL) return ModBlocks.DEAD_HORN_CORAL;
        return Blocks.AIR;
    }

    @NotNull
    public Block getAliveVariant() {
        if (this == ModBlocks.DEAD_TUBE_CORAL) return ModBlocks.TUBE_CORAL;
        if (this == ModBlocks.DEAD_BRAIN_CORAL) return ModBlocks.BRAIN_CORAL;
        if (this == ModBlocks.DEAD_BUBBLE_CORAL) return ModBlocks.BUBBLE_CORAL;
        if (this == ModBlocks.DEAD_FIRE_CORAL) return ModBlocks.FIRE_CORAL;
        if (this == ModBlocks.DEAD_HORN_CORAL) return ModBlocks.HORN_CORAL;
        return Blocks.AIR;
    }
}