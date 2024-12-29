package surreal.backportium.client.resource;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class AssetMap extends Object2ObjectOpenHashMap<String, String> {

    @Override
    public String put(String s, String s2) {
        if (s2.indexOf(':') != -1) s2 = "assets/" + s2.replace(':', '/');
        else s2 = "assets/backportium/" + s2;
        return super.put("assets/minecraft/" + s, s2);
    }
}
