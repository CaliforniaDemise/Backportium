package surreal.backportium._internal.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Blocks {
    <T extends Block> T registerItem(T entry, String name);
    <T extends Block> T registerItem(T entry, ResourceLocation name);
    <T extends Block> T registerItem(T entry, @Nullable Function<Block, Item> itemFunction, String name);
    <T extends Block> T registerItem(T entry, @Nullable Function<Block, Item> itemFunction, ResourceLocation name);
    <V extends Block> V register(V entry, String name);
    <V extends Block> V register(V entry, ResourceLocation location);
}
