package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.item.ItemBlockSpecial;

public class ItemBlockKelp extends ItemBlockSpecial {

    protected static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = BlockDoublePlant.HALF;

    public ItemBlockKelp(Block block) {
        super(block);
    }

    @NotNull
    @Override
    public EnumActionResult onItemUse(@NotNull EntityPlayer player, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == this.getBlock()) {
            BlockDoublePlant.EnumBlockHalf half = state.getValue(HALF);
            if (half == BlockDoublePlant.EnumBlockHalf.UPPER) {
                facing = EnumFacing.UP;
            } else {
                BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(pos);
                while (true) {
                    IBlockState upState = worldIn.getBlockState(mutPos.move(EnumFacing.UP));
                    if (upState.getBlock() == this.getBlock() && upState.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
                        pos = mutPos.toImmutable();
                        facing = EnumFacing.UP;
                        break;
                    }
                }
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
