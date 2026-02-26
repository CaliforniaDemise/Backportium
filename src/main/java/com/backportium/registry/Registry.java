package com.backportium.registry;

import com.backportium.registry.client.StateMapper;
import com.backportium.registry.client.TextureStitcher;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class Registry<E extends IForgeRegistryEntry<E>> implements Iterable<E> {

    private final List<E> entries = new ArrayList<>();
    protected final RegistryManager manager;

    public Registry(RegistryManager manager) {
        this.manager = manager;
    }

    public E register(E entry) {
        final boolean client = FMLLaunchHandler.side().isClient();
        if (client) {
            if (entry instanceof TextureStitcher) manager.stitchers.add((TextureStitcher) entry);
            if (entry instanceof StateMapper) manager.stateMappers.add((StateMapper) entry);
        }
        entries.add(entry);
        return entry;
    }

    public void registerAll(RegistryEvent.Register<E> event) {
        IForgeRegistry<E> registry = event.getRegistry();
        entries.forEach(registry::register);
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return entries.iterator();
    }
}
