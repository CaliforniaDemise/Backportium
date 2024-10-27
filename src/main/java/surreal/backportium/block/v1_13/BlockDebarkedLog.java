package surreal.backportium.block.v1_13;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Random;

// Might use this instead
@SuppressWarnings("deprecation")
public class BlockDebarkedLog extends BlockLog {

    private final Block block;

    public BlockDebarkedLog(Block block) {
        this.block = block;
    }

    @Override
    public boolean isTopSolid(@Nonnull IBlockState state) {
        return this.block.isTopSolid(this.origFromMeta(state));
    }

    @Override
    public boolean isFullBlock(@Nonnull IBlockState state) {
        return this.block.isFullBlock(this.origFromMeta(state));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
        return this.block.canEntitySpawn(this.origFromMeta(state), entityIn);
    }

    @Override
    public int getLightOpacity(@Nonnull IBlockState state) {
        return this.block.getLightOpacity(this.origFromMeta(state));
    }

    @Override
    public boolean isTranslucent(@Nonnull IBlockState state) {
        return this.block.isTranslucent(this.origFromMeta(state));
    }

    @Override
    public int getLightValue(IBlockState state) {
        return this.block.getLightValue(this.origFromMeta(state));
    }

    @Override
    public boolean getUseNeighborBrightness(@Nonnull IBlockState state) {
        return this.block.getUseNeighborBrightness(this.origFromMeta(state));
    }

    @Nonnull
    @Override
    public Material getMaterial(@Nonnull IBlockState state) {
        return this.block.getMaterial(state);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return this.block.getMapColor(this.origFromMeta(state), worldIn, pos);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
       IBlockState origState = this.origFromMeta(meta);
       return this.debarkedFromOrig(origState);
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        return this.block.getMetaFromState(this.origFromProp(state));
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState actualState = this.block.getActualState(this.origFromProp(state), worldIn, pos);
        return this.debarkedFromOrig(actualState);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return this.debarkedFromOrig(this.block.withRotation(this.origFromProp(state), rot));
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return this.debarkedFromOrig(this.block.withMirror(this.origFromProp(state), mirrorIn));
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return this.block.getSoundType(this.origFromMeta(state), world, pos, entity);
    }

    @Override
    public boolean isBlockNormalCube(@Nonnull IBlockState state) {
        return this.block.isBlockNormalCube(this.origFromMeta(state));
    }

    @Override
    public boolean isNormalCube(@Nonnull IBlockState state) {
        return this.block.isNormalCube(this.origFromMeta(state));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.block.isNormalCube(this.origFromMeta(state), world, pos);
    }

    @Override
    public boolean causesSuffocation(@Nonnull IBlockState state) {
        return this.block.causesSuffocation(this.origFromMeta(state));
    }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state) {
        return this.block.isFullCube(this.origFromMeta(state));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasCustomBreakingProgress(@Nonnull IBlockState state) {
        return this.block.hasCustomBreakingProgress(this.origFromMeta(state));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return this.block.isPassable(worldIn, pos);
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return this.block.getRenderType(this.origFromMeta(state));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return this.block.isReplaceable(worldIn, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return this.block.getBlockHardness(this.origFromMeta(blockState), worldIn, pos);
    }

    @Override
    public boolean getTickRandomly() {
        return this.block.getTickRandomly();
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return this.block.hasTileEntity(this.origFromMeta(state));
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return this.block.createTileEntity(world, this.origFromMeta(state));
    }

    @Override
    public int quantityDropped(@Nonnull Random random) {
        return this.block.quantityDropped(random);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return this.block.quantityDropped(this.origFromMeta(state), fortune, random);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, @Nonnull Random random) {
        return this.block.quantityDroppedWithBonus(fortune, random);
    }

    @Override
    public int damageDropped(@Nonnull IBlockState state) {
        return this.block.damageDropped(this.origFromMeta(state));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return this.block.canCreatureSpawn(this.origFromMeta(state), world, pos, type);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos) {
        this.block.beginLeavesDecay(this.origFromMeta(state), world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.block.canSustainLeaves(this.origFromMeta(state), world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.block.isLeaves(this.origFromMeta(state), world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.block.canBeReplacedByLeaves(this.origFromMeta(state), world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return this.block.isWood(world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target) {
        return this.block.isReplaceableOreGen(this.origFromMeta(state), world, pos, target);
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return this.block.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        this.block.onBlockExploded(world, pos, explosion);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return this.block.canConnectRedstone(this.origFromMeta(state), world, pos, side);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.block.canPlaceTorchOnTop(this.origFromMeta(state), world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isFertile(World world, BlockPos pos) {
        return this.block.isFertile(world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isFoliage(IBlockAccess world, BlockPos pos) {
        return this.block.isFoliage(world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        return this.block.addLandingEffects(this.origFromMeta(state), worldObj, blockPosition, iblockstate, entity, numberOfParticles);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        return this.block.addRunningEffects(this.origFromMeta(state), world, pos, entity);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @ParametersAreNonnullByDefault
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return this.block.addHitEffects(this.origFromMeta(state), worldObj, target, manager);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @ParametersAreNonnullByDefault
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return this.block.addDestroyEffects(world, pos, manager);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return this.block.canSustainPlant(this.origFromMeta(state), world, pos, direction, plantable);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onPlantGrow(IBlockState state, World world, BlockPos pos, BlockPos source) {
        this.block.onPlantGrow(this.origFromMeta(state), world, pos, source);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.block.getLightOpacity(this.origFromMeta(state), world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return this.block.canEntityDestroy(this.origFromMeta(state), world, pos, entity);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
        return this.block.isBeaconBase(worldObj, pos, beacon);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return this.block.rotateBlock(world, pos, axis);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return this.block.getValidRotations(world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return this.block.getEnchantPowerBonus(world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        return this.block.recolorBlock(world, pos, side, color);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        return this.block.getExpDrop(this.origFromMeta(state), world, pos, fortune);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        this.block.onNeighborChange(world, pos, neighbor);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return this.block.shouldCheckWeakPower(this.origFromMeta(state), world, pos, side);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
        return this.block.getWeakChanges(world, pos);
    }

    @Nullable
    @Override
    public String getHarvestTool(@Nonnull IBlockState state) {
        return this.block.getHarvestTool(this.origFromMeta(state));
    }

    @Override
    public int getHarvestLevel(@Nonnull IBlockState state) {
        return this.block.getHarvestLevel(this.origFromMeta(state));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isToolEffective(String type, IBlockState state) {
        return this.block.isToolEffective(type, this.origFromMeta(state));
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState origState = this.origFromProp(state);
        IBlockState origExt = this.block.getExtendedState(origState, world, pos);
        return this.debarkedFromOrig(origExt);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
        return this.block.isEntityInsideMaterial(world, blockpos, this.origFromMeta(iblockstate), entity, yToTest, materialIn, testingHead);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Boolean isAABBInsideMaterial(World world, BlockPos pos, AxisAlignedBB boundingBox, Material materialIn) {
        return this.block.isAABBInsideMaterial(world, pos, boundingBox, materialIn);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Boolean isAABBInsideLiquid(World world, BlockPos pos, AxisAlignedBB boundingBox) {
        return this.block.isAABBInsideLiquid(world, pos, boundingBox);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return this.block.canRenderInLayer(this.origFromMeta(state), layer);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
        return this.block.getBeaconColorMultiplier(this.origFromMeta(state), world, pos, beaconPos);
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
        return this.block.getFogColor(world, pos, this.origFromMeta(state), entity, originalColor, partialTicks);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IBlockState getStateAtViewpoint(IBlockState state, IBlockAccess world, BlockPos pos, Vec3d viewpoint) {
        IBlockState origState = this.origFromProp(state);
        IBlockState stateAtView = this.block.getStateAtViewpoint(origState, world, pos, viewpoint);
        return this.block.getStateAtViewpoint(this.debarkedFromOrig(stateAtView), world, pos, viewpoint);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return this.block.canBeConnectedTo(world, pos, facing);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityLiving entity) {
        return this.block.getAiPathNodeType(this.origFromMeta(state), world, pos, entity);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean doesSideBlockChestOpening(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return this.block.doesSideBlockChestOpening(this.origFromMeta(blockState), world, pos, side);
    }

    @Override
    public boolean isStickyBlock(@Nonnull IBlockState state) {
        return this.block.isStickyBlock(this.origFromMeta(state));
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, this.b);
//    }

    private IBlockState origFromMeta(IBlockState state) {
        return this.origFromMeta(this.getMetaFromState(state));
    }

    private IBlockState origFromMeta(int meta) {
        return this.block.getStateFromMeta(meta);
    }

    private <T extends Comparable<T>, V extends T> IBlockState debarkedFromOrig(IBlockState state) {
        IBlockState out = this.getDefaultState();
        for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
            out = out.withProperty((IProperty<T>) entry.getKey(), (V) entry.getValue());
        }
        return out;
    }

    private <T extends Comparable<T>, V extends T> IBlockState origFromProp(IBlockState state) {
        IBlockState out = this.block.getDefaultState();
        for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
            out = out.withProperty((IProperty<T>) entry.getKey(), (V) entry.getValue());
        }
        return out;
    }
}
