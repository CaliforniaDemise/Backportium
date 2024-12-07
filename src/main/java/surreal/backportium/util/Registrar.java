package surreal.backportium.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.Tags;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Registrar<T extends IForgeRegistryEntry<T>> implements Iterable<T> {

    private final List<T> list;

    public Registrar(int size) {
        this.list = new ArrayList<>(size);
    }

    protected T register(@NotNull T entry, @NotNull String path) {
        return this.register(entry, new ResourceLocation(Tags.MOD_ID, path));
    }

    protected T register(@NotNull T entry, @NotNull ResourceLocation location) {
        this.list.add(entry);
        return entry;
    }

    public void registerEntries(RegistryEvent.Register<T> event) {
        IForgeRegistry<T> registry = event.getRegistry();
        list.forEach(registry::register);
    }

    public void preInit(FMLPreInitializationEvent event) {}
    public void init(FMLInitializationEvent event) {}

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.list.iterator();
    }
}
