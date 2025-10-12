package surreal.backportium._internal.registry;

import net.minecraft.block.BlockSlab;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.registries.IForgeRegistryEntry;
import surreal.backportium._internal.block.Slab;
import surreal.backportium._internal.block.TileEntityProvider;
import surreal.backportium._internal.client.ClientHandler;
import surreal.backportium._internal.client.renderer.TextureStitcher;
import surreal.backportium._internal.client.renderer.model.ModelBaker;
import surreal.backportium._internal.client.renderer.model.StateMapProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class Registry<T extends IForgeRegistryEntry<T>> {

    protected final RegistryManager manager;
    protected final List<T> list = new LinkedList<>();

    protected Registry(RegistryManager manager) {
        this.manager = manager;
    }

    public <V extends T> V register(V entry) {
        if (entry instanceof TileEntityProvider) {
            manager.tileEntities.add((TileEntityProvider) entry);
        }
        if (entry instanceof Slab) {
            Slab slab = (Slab) entry;
            if (!slab.isDouble()) {
                BlockSlab doubleSlab = slab.getDoubleSlab().get();
                Item slabItem = slab.getSlabItem().apply(((BlockSlab) slab), doubleSlab);
                this.manager.blocks.register(doubleSlab, new ResourceLocation(Objects.requireNonNull(entry.getRegistryName()).getNamespace(), entry.getRegistryName().getPath() + "_double"));
                this.manager.items.register(slabItem, entry.getRegistryName());
            }
        }
        if (FMLLaunchHandler.side().isClient()) {
            ClientHandler handler = manager.getClient();
            if (entry instanceof TextureStitcher) {
                handler.addTextureStitcher((TextureStitcher) entry);
            }
            if (entry instanceof ModelBaker) {
                handler.addModelBaker((ModelBaker) entry);
            }
            if (entry instanceof StateMapProvider) {
                manager.stateMaps.add((StateMapProvider) entry);
            }
        }
        list.add(entry);
        return entry;
    }

    public void registerAll(RegistryEvent.Register<T> event) {
        list.forEach(event.getRegistry()::register);
    }
}
