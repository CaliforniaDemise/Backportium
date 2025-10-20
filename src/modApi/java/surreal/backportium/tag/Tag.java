package surreal.backportium.tag;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class Tag<T> {

    protected final Map<String, Collection<T>> map = new Object2ObjectOpenHashMap<>();
    protected final Hash.Strategy<T> strategy;

    public Tag(@Nullable Hash.Strategy<T> strategy) {
        this.strategy = strategy;
    }

    public boolean contains(String name, T object) {
        Collection<T> collection = this.map.get(name);
        if (collection == null) return false;
        return collection.contains(object);
    }

    public void add(String name, T object) {
        Collection<T> collection = this.map.get(name);
        if (collection == null) {
            collection = this.strategy != null ? new ObjectOpenCustomHashSet<>(this.strategy) : new ObjectOpenHashSet<>();
            map.put(name, collection);
        }
        collection.add(object);
    }

    public boolean removeAll(String name) {
        return this.map.remove(name) != null;
    }

    public boolean remove(String name, T object) {
        final Collection<T> collection = this.map.get(name);
        if (collection == null) return false;
        return collection.remove(object);
    }
}
