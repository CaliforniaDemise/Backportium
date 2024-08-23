package surreal.backportium.util;

import net.minecraft.util.math.BlockPos;

public class MutBlockPos extends BlockPos.MutableBlockPos {

    public MutBlockPos() {}

    public MutBlockPos(BlockPos pos) {
        super(pos);
    }

    public MutBlockPos setPos(BlockPos pos, int x, int y, int z) {
        return (MutBlockPos) this.setPos(pos.getX() + x, pos.getY() + y,pos.getZ() + z);
    }

    public MutBlockPos setX(int x) {
        this.x = x;
        return this;
    }

    public MutBlockPos setX(BlockPos pos) {
        return this.setX(pos.getX());
    }

    public MutBlockPos setX(BlockPos pos, int x) {
        this.setX(pos.getX() + x);
        return this;
    }

    public MutBlockPos setY(BlockPos pos) {
        this.setY(pos.getY());
        return this;
    }

    public MutBlockPos setY(BlockPos pos, int y) {
        this.setY(pos.getY() + y);
        return this;
    }

    public MutBlockPos setZ(int z) {
        this.z = z;
        return this;
    }

    public MutBlockPos setZ(BlockPos pos) {
        return this.setZ(pos.getZ());
    }

    public MutBlockPos setZ(BlockPos pos, int z) {
        this.setZ(pos.getZ() + z);
        return this;
    }

    public MutBlockPos offsetX(int x) {
        this.setX(this, x);
        return this;
    }

    public MutBlockPos offsetY(int y) {
        this.setY(this, y);
        return this;
    }

    public MutBlockPos offsetZ(int z) {
        this.setZ(this, z);
        return this;
    }
}
