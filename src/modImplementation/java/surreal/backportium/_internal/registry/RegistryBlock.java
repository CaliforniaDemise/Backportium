package surreal.backportium._internal.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class RegistryBlock extends Registry<Block> implements Blocks {

    protected RegistryBlock(RegistryManager manager) {
        super(manager);
    }

    @Override
    public <T extends Block> T registerItem(T entry, String name) {
        return registerItem(entry, ItemBlock::new, name);
    }

    @Override
    public <T extends Block> T registerItem(T entry, ResourceLocation name) {
        return registerItem(entry, ItemBlock::new, name);
    }

    @Override
    public <T extends Block> T registerItem(T entry, @Nullable Function<Block, Item> itemFunction, String name) {
        return registerItem(entry, itemFunction, new ResourceLocation(manager.getModId(), name));
    }

    @Override
    public <T extends Block> T registerItem(T entry, @Nullable Function<Block, Item> itemFunction, ResourceLocation name) {
        if (itemFunction != null) {
            Item item = itemFunction.apply(entry);
            manager.items.register(item, name);
        }
        return register(entry, name);
    }

    @Override
    public <V extends Block> V register(V entry, String name) {
        return registerItem(entry, new ResourceLocation(manager.getModId(), name));
    }

    @Override
    public <V extends Block> V register(V entry, ResourceLocation location) {
        entry.setRegistryName(location).setTranslationKey(location.getNamespace() + "." + location.getPath());
        return super.register(entry);
    }
}
