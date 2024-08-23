package surreal.backportium.block.v1_13;

import net.minecraft.block.BlockSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import surreal.backportium.block.BlockClustered;
import surreal.backportium.util.RandomHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockTurtleEgg extends BlockClustered {

    public static final PropertyInteger HATCH = PropertyInteger.create("hatch", 0, 2);

    public static final AxisAlignedBB ONE_AABB, TWO_AABB;

    public BlockTurtleEgg(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setTickRandomly(true);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(getDefaultState().withProperty(HATCH, 0));
        this.setForce(0.5F);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int hatch = state.getValue(HATCH);
        if (hatch < 2) {
            long time = worldIn.getWorldTime();
            if (((time >= 21600 && time <= 22550) || rand.nextInt(500) == 1) && worldIn.getBlockState(pos.down()).getBlock() instanceof BlockSand) {
                worldIn.setBlockState(pos, state.withProperty(AMOUNT, hatch + 1));
                spawnParticles(worldIn, pos, state);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
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

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(AMOUNT) > 0 ? TWO_AABB : ONE_AABB;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        boolean ret = super.removedByPlayer(state, world, pos, player, willHarvest);
        if (!player.isCreative()) {
            int amount = state.getValue(AMOUNT);
            if (amount > 0) {
                ret = world.setBlockState(pos, state.withProperty(AMOUNT, amount - 1));
            }
        }
        return ret;
    }

    //    @Override
//    @ParametersAreNonnullByDefault
//    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
//        player.addStat(StatList.getBlockStats(this));
//        player.addExhaustion(0.005F);
//
//        if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(Enchantments.SILK_TOUCH), stack) > 0) {
//            List<ItemStack> items = new java.util.ArrayList<>();
//            ItemStack itemstack = this.getSilkTouchDrop(state);
//
//            if (!itemstack.isEmpty()) {
//                items.add(itemstack);
//            }
//
//            ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
//
//            for (ItemStack item : items) {
//                spawnAsEntity(worldIn, pos, item);
//            }
//
//            int amount = state.getValue(AMOUNT);
//            if (amount > 0) {
//                worldIn.setBlockState(pos, state.withProperty(AMOUNT, amount - 1));
//            }
//        }
//        else {
//            harvesters.set(player);
//            int i = EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(Enchantments.FORTUNE), stack);
//            this.dropBlockAsItem(worldIn, pos, state, i);
//            harvesters.set(null);
//        }
//    }

    @Override
    @ParametersAreNonnullByDefault
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        IBlockState state = worldIn.getBlockState(pos);
        if (fallDistance < 1.0F || state.getBlock() != this) return;
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entityIn;
            int amount = state.getValue(AMOUNT);
            if (EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(Enchantments.FEATHER_FALLING), entityLiving.getItemStackFromSlot(EntityEquipmentSlot.FEET)) != 0) return;
            if (!worldIn.isRemote) worldIn.destroyBlock(pos, false); // TODO Handle it's own breaking
            if (amount > 0) {
                entityIn.posY += 0.05F;
                worldIn.setBlockState(pos, state.withProperty(AMOUNT, amount - 1));
            }
        }
    }

    // TODO Handle this properly
    //    @Override
//    @ParametersAreNonnullByDefault
//    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
//        IBlockState state = worldIn.getBlockState(pos);
//        if (state.getBlock() != this) return;
//        if (worldIn.rand.nextFloat() < 0.01F) {
//            int amount = state.getValue(AMOUNT);
//            worldIn.destroyBlock(pos, false);
//            if (amount > 0) {
//                entityIn.posY += 0.05F;
//                worldIn.setBlockState(pos, state.withProperty(AMOUNT, amount - 1));
//            }
//        }
//    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Nonnull
    @Override
    protected ItemStack getSilkTouchDrop(@Nonnull IBlockState state) {
        return new ItemStack(RandomHelper.getItemFromBlock(state.getBlock()));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int hatch = state.getValue(HATCH);
        int amount = state.getValue(AMOUNT);
        return amount + hatch * 4;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        int hatch = meta / 4;
        int amount = meta & 3;
        return getDefaultState().withProperty(AMOUNT, amount).withProperty(HATCH, hatch);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AMOUNT, HATCH);
    }

    static {
        float aPix = 1F / 16;
        float threePix = aPix * 3;
        float twelvePix = threePix * 4;
        float fifteenPix = 1F - aPix;

        float length = aPix * 7;

        ONE_AABB = new AxisAlignedBB(aPix, 0, aPix, twelvePix, length, twelvePix);
        TWO_AABB = new AxisAlignedBB(aPix, 0, aPix, fifteenPix, length, fifteenPix);
    }
}
