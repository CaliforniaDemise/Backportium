package surreal.backportium.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface StrippableLog {
    boolean onStrip(World world, EntityPlayer player, EnumHand hand, BlockPos pos, IBlockState state, EnumFacing facing, Vec3d hitVec);
}
