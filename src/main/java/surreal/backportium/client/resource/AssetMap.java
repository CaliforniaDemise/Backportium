package surreal.backportium.client.resource;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class AssetMap extends Object2ObjectOpenHashMap<String, String> {

    @Override
    public String put(String s, String s2) {
        return super.put("assets/minecraft/" + s, "assets/minecraft/" + s2);
    }
}
