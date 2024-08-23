package surreal.backportium.block.v1_13;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import surreal.backportium.sound.ModSounds;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockPumpkin extends Block {

    public BlockPumpkin() {
        super(Material.GOURD, MapColor.ADOBE);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public boolean isToolEffective(String type, @Nonnull IBlockState state) {
        return type.equals("axe");
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.getItem() instanceof ItemShears && facing.getAxis().isHorizontal()) {
            if (worldIn.isRemote) {
                worldIn.playSound(playerIn.posX, playerIn.posY, playerIn.posZ, ModSounds.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
                return true;
            }

            worldIn.setBlockState(pos, Blocks.PUMPKIN.getDefaultState().withProperty(BlockHorizontal.FACING, facing));

            int xOff = facing.getXOffset();
            int yOff = facing.getYOffset();
            int zOff = facing.getZOffset();

            EntityItem entityItem = new EntityItem(worldIn, pos.getX() + xOff + (xOff * 0.3F), pos.getY() + yOff + (yOff * 0.3F), pos.getZ() + zOff + (zOff * 0.3F), new ItemStack(Items.PUMPKIN_SEEDS, 4));
            entityItem.setNoPickupDelay();
            worldIn.spawnEntity(entityItem);

            stack.damageItem(1, playerIn);
            return true;
        }

        return false;
    }
}
