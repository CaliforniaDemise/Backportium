package surreal.backportium._internal.client.resource;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.Tags;

import java.util.Map;

public class Assets {

    private final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

    public void textures(String... textures) { assets(textures, null, null); }
    public void models(String... models) { assets(null, models, null); }
    public void sounds(String... sounds) { assets(null, null, sounds); }

    public void assets(String @Nullable [] textures, String @Nullable [] models, String @Nullable [] sounds) {
        final String mcAssets = "assets/minecraft/";
        final String bpAssets = "assets/" + Tags.MOD_ID + "/";
        if (textures != null) {
            for (String texture : textures) {
                if (!texture.contains(".")) texture = texture + ".png";
                String value = texture.replace("item/", "items/").replace("block/", "blocks/");
                String key = mcAssets + "textures/" + texture;
                value = bpAssets + "textures/" + value;
                this.builder.put(key, value);
            }
        }
        if (models != null) {
            for (String model : models) {
                model = "models/" + model + ".json";
                this.builder.put(mcAssets + model, bpAssets + model);
            }
        }
        if (sounds != null) {
            for (String sound : sounds) {
                sound = "sounds/" + sound + ".ogg";
                this.builder.put(mcAssets + sound, bpAssets + sound);
            }
        }
    }

    public Map<String, String> build() {
        return this.builder.build();
    }
}
