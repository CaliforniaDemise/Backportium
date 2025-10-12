package surreal.backportium._internal.block;

import net.minecraft.block.BlockSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium.block.BlockClustered;
import surreal.backportium.init.ModSounds;
import surreal.backportium.util.BlockUtil;
import surreal.backportium.util.WorldUtil;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockTurtleEgg extends BlockClustered implements ModelProvider {

    public static final PropertyInteger HATCH = PropertyInteger.create("hatch", 0, 2);

    public static final AxisAlignedBB ONE_AABB, MORE_AABB;

    public BlockTurtleEgg() {
        super(Material.ROCK, MapColor.SAND);
        this.setTickRandomly(true);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(getDefaultState().withProperty(HATCH, 0));
        this.setHardness(0.5F).setResistance(0.5F);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public void randomTick(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random random) {
        int hatch = state.getValue(HATCH);
        if (hatch < 2) {
            long time = worldIn.getWorldTime();
            if (((time >= 21600 && time <= 22550) || random.nextInt(500) == 1) && worldIn.getBlockState(pos.down()).getBlock() instanceof BlockSand) {
                worldIn.setBlockState(pos, state.withProperty(AMOUNT, hatch + 1));
                spawnParticles(worldIn, pos, state);
                if (worldIn.isRemote) worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, worldIn.rand.nextBoolean() ? 1.1F : 0.9F, false);
            }
        }
    }

    @Override
    public void updateTick(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
        List<EntityLivingBase> list = WorldUtil.getEntitiesInBlock(worldIn, pos, EntityLivingBase.class);
        boolean trampled = false;
        for (EntityLivingBase entity : list) {
            if (!entity.isSneaking()) {
                trampled = true;
                this.trample(worldIn, state, pos, entity);
                break;
            }
        }
        if (trampled) {
            list.forEach(e -> e.posY += 0.05);
            if (worldIn.getBlockState(pos).getBlock() == this) worldIn.scheduleUpdate(pos, this, 100);
        }
    }

    @Override
    public void onEntityWalk(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn) {
        if (!entityIn.isSneaking() && entityIn instanceof EntityLivingBase) worldIn.scheduleUpdate(pos, this, 100);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, @NotNull IBlockState state, @NotNull EntityLivingBase placer, @NotNull ItemStack stack) {
        if (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockSand) {
            spawnParticles(worldIn, pos, state);
        }
    }

    private void spawnParticles(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isRemote) {
            for (int i = 0; i < 12 + worldIn.rand.nextInt(4); i++) {
                worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() - 0.1F + worldIn.rand.nextFloat(), pos.getY() + worldIn.rand.nextFloat() / 2, pos.getZ() - 0.1F + worldIn.rand.nextFloat(), (worldIn.rand.nextFloat() / 2) - 0.1F, (worldIn.rand.nextFloat() / 2) - 0.1F, (worldIn.rand.nextFloat() / 2) - 0.1F);
            }
        }
    }

    @Override
    public @NotNull BlockFaceShape getBlockFaceShape(@NotNull IBlockAccess worldIn, @NotNull IBlockState state, @NotNull BlockPos pos, @NotNull EnumFacing face) {
        if (face == EnumFacing.DOWN) return state.getValue(AMOUNT) > 0 ? BlockFaceShape.CENTER : BlockFaceShape.CENTER_SMALL;
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isSideSolid(@NotNull IBlockState base_state, @NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull EnumFacing side) {
        return false;
    }

    @NotNull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos) {
        return state.getValue(AMOUNT) > 0 ? MORE_AABB : ONE_AABB;
    }

    @NotNull
    @Override
    public Item getItemDropped(@NotNull IBlockState state, @NotNull Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean removedByPlayer(@NotNull IBlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull EntityPlayer player, boolean willHarvest) {
        boolean ret = super.removedByPlayer(state, world, pos, player, willHarvest);
        if (!player.isCreative()) {
            int amount = state.getValue(AMOUNT);
            if (amount > 0) {
                ret = world.setBlockState(pos, state.withProperty(AMOUNT, amount - 1));
            }
        }
        return ret;
    }

    @Override
    public void onFallenUpon(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn, float fallDistance) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        IBlockState state = worldIn.getBlockState(pos);
        if (fallDistance < 1.0F || state.getBlock() != this) return;
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entityIn;
            if (EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(Enchantments.FEATHER_FALLING), entityLiving.getItemStackFromSlot(EntityEquipmentSlot.FEET)) != 0) return;
            entityIn.posY += 0.05;
            this.trample(worldIn, state, pos, entityIn);
        }
    }

    private void trample(World worldIn, IBlockState state, BlockPos pos, Entity entityIn) {
        int amount = state.getValue(AMOUNT);
        if (!worldIn.isRemote) worldIn.destroyBlock(pos, false); // TODO Handle it's own breaking
        if (amount > 0) {
            worldIn.setBlockState(pos, state.withProperty(AMOUNT, amount - 1));
        }
        if (worldIn.isRemote) worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, (worldIn.rand.nextBoolean() ? 1.1F : 0.9F), false);
    }

    @Override
    public boolean canSilkHarvest(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer player) {
        return true;
    }

    @NotNull
    @Override
    protected ItemStack getSilkTouchDrop(@NotNull IBlockState state) {
        return new ItemStack(BlockUtil.getItemFromBlock(state.getBlock()));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int hatch = state.getValue(HATCH);
        int amount = state.getValue(AMOUNT);
        return amount + hatch * 4;
    }

    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        int hatch = meta / 4;
        int amount = meta & 3;
        return getDefaultState().withProperty(AMOUNT, amount).withProperty(HATCH, hatch);
    }

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AMOUNT, HATCH);
    }

    @Override
    public void registerModels() {
        Item item = BlockUtil.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory"));
    }

    static {
        float aPix = 1F / 16;
        float threePix = aPix * 3;
        float twelvePix = threePix * 4;
        float fifteenPix = 1F - aPix;
        float length = aPix * 7;
        ONE_AABB = new AxisAlignedBB(aPix, 0, aPix, twelvePix, length, twelvePix);
        MORE_AABB = new AxisAlignedBB(aPix, 0, aPix, fifteenPix, length, fifteenPix);
    }
}
