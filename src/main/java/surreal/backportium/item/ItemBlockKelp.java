package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import surreal.backportium.util.MutBlockPos;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.block.BlockDoublePlant.HALF;

public class ItemBlockKelp extends ItemBlockSpecial {

    public ItemBlockKelp(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);

        if (state.getBlock() == this.getBlock()) {
            BlockDoublePlant.EnumBlockHalf half = state.getValue(HALF);

            if (half == BlockDoublePlant.EnumBlockHalf.UPPER) {
                facing = EnumFacing.UP;
            }
            else {
                MutBlockPos mutPos = new MutBlockPos(pos);
                while (true) {
                    IBlockState upState = worldIn.getBlockState(mutPos.move(EnumFacing.UP));
                    if (upState.getBlock() == this.getBlock() && upState.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
                        pos = mutPos;
                        facing = EnumFacing.UP;
                        break;
                    }
                }
            }
        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
