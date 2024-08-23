package surreal.backportium.client.resource;

import com.cleanroommc.assetmover.AssetMoverAPI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Models {

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        AssetMap map = new AssetMap();

        // 1.13
        put(map, "block/template_seagrass");
        put(map, "block/sea_pickle");
        put(map, "block/two_sea_pickles");
        put(map, "block/three_sea_pickles");
        put(map, "block/four_sea_pickles");
        put(map, "block/dead_sea_pickle");
        put(map, "block/two_dead_sea_pickles");
        put(map, "block/three_dead_sea_pickles");
        put(map, "block/four_dead_sea_pickles");
        put(map, "block/template_turtle_egg");
        put(map, "block/template_two_turtle_eggs");
        put(map, "block/template_three_turtle_eggs");
        put(map, "block/template_four_turtle_eggs");
        put(map, "block/template_orientable_trapdoor_open", "block/trapdoor_open");
        put(map, "block/coral_fan", "block/coral_fan");
        put(map, "block/coral_wall_fan", "block/coral_wall_fan");

        AssetMoverAPI.fromMinecraft("1.20.1", map);
    }

    private static void put(AssetMap map, String key, String value) {
        map.put("models/" + key + ".json", "models/" + value + ".json");
    }

    private static void put(AssetMap map, String key) {
        String kv = "models/" + key + ".json";
        map.put(kv, kv);
    }
}
