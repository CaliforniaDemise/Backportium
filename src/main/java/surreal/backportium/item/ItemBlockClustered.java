package surreal.backportium.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static surreal.backportium.block.BlockClustered.AMOUNT;

public class ItemBlockClustered extends ItemBlockSpecial {

    public ItemBlockClustered(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos oldPos = pos;
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() != this.getBlock()) {
            pos = pos.offset(facing);
            state = worldIn.getBlockState(pos);
        }

        if (state.getBlock() == this.getBlock() && state.getValue(AMOUNT) < 3) {
            worldIn.setBlockState(pos, state.withProperty(AMOUNT, state.getValue(AMOUNT) + 1), 3);

            IBlockState iblockstate1 = worldIn.getBlockState(pos);
            ItemStack itemstack = player.getHeldItem(hand);

            SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
            worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, itemstack);
            }

            state.getBlock().onBlockPlacedBy(worldIn, pos, state, player, itemstack);

            itemstack.shrink(1);

            return EnumActionResult.SUCCESS;
        }
        else return super.onItemUse(player, worldIn, oldPos, hand, facing, hitX, hitY, hitZ);
    }
}
