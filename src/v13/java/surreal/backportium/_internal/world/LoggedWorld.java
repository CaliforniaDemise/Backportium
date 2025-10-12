package surreal.backportium._internal.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.api.block.Loggable;

public class LoggedWorld implements IBlockAccess {

    private final IBlockAccess parent;

    public LoggedWorld(IBlockAccess parent) {
        this.parent = parent;
    }

    @Override
    public @Nullable TileEntity getTileEntity(@NotNull BlockPos pos) {
        return this.parent.getTileEntity(pos);
    }

    @Override
    public int getCombinedLight(@NotNull BlockPos pos, int lightValue) {
        return this.parent.getCombinedLight(pos, lightValue);
    }

    @NotNull
    @Override
    public IBlockState getBlockState(@NotNull BlockPos pos) {
        IBlockState state = this.parent.getBlockState(pos);
        if (state.getBlock() instanceof Loggable) {
            return Loggable.cast(state.getBlock()).getLoggedState(this.parent, pos, state);
        }
        return state;
    }

    @Override
    public boolean isAirBlock(@NotNull BlockPos pos) {
        return this.parent.isAirBlock(pos);
    }

    @NotNull
    @Override
    public Biome getBiome(@NotNull BlockPos pos) {
        return this.parent.getBiome(pos);
    }

    @Override
    public int getStrongPower(@NotNull BlockPos pos, @NotNull EnumFacing direction) {
        return this.parent.getStrongPower(pos, direction);
    }

    @NotNull
    @Override
    public WorldType getWorldType() {
        return this.parent.getWorldType();
    }

    @Override
    public boolean isSideSolid(@NotNull BlockPos pos, @NotNull EnumFacing side, boolean _default) {
        return this.parent.isSideSolid(pos, side, _default);
    }
}
