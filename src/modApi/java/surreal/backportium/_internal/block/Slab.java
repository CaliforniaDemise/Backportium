package surreal.backportium._internal.block;

import net.minecraft.block.BlockSlab;
import net.minecraft.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface Slab {
    boolean isDouble();
    Supplier<BlockSlab> getDoubleSlab();
    BiFunction<BlockSlab, BlockSlab, Item> getSlabItem();
}
