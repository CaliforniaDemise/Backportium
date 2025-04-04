package surreal.backportium.client.resource;

import com.cleanroommc.assetmover.AssetMoverAPI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Textures {

    @SideOnly(Side.CLIENT)
    public static void initTextures() {
        AssetMap map = new AssetMap();

        // 1.13
        put(map, "block/blue_ice", "assets/backportium/textures/blocks/blue_ice");
        put(map, "block/dried_kelp_top", "assets/backportium/textures/blocks/dried_kelp_top");
        put(map, "block/dried_kelp_side", "assets/backportium/textures/blocks/dried_kelp_side");
        put(map, "block/dried_kelp_bottom", "assets/backportium/textures/blocks/dried_kelp_bottom");
        put(map, "block/conduit", "assets/backportium/textures/blocks/conduit");
        put(map, "block/seagrass", "assets/backportium/textures/blocks/seagrass");
        put2(map, "block/seagrass.png.mcmeta", "assets/backportium/textures/blocks/seagrass.png.mcmeta");
        put(map, "block/tall_seagrass_bottom", "assets/backportium/textures/blocks/tall_seagrass_bottom");
        put2(map, "block/tall_seagrass_bottom.png.mcmeta", "assets/backportium/textures/blocks/tall_seagrass_bottom.png.mcmeta");
        put(map, "block/tall_seagrass_top", "assets/backportium/textures/blocks/tall_seagrass_top");
        put2(map, "block/tall_seagrass_top.png.mcmeta", "assets/backportium/textures/blocks/tall_seagrass_top.png.mcmeta");
        put(map, "block/sea_pickle", "assets/backportium/textures/blocks/sea_pickle");
        put(map, "block/turtle_egg", "assets/backportium/textures/blocks/turtle_egg");
        put(map, "block/turtle_egg_slightly_cracked", "assets/backportium/textures/blocks/turtle_egg_slightly_cracked");
        put(map, "block/turtle_egg_very_cracked", "assets/backportium/textures/blocks/turtle_egg_very_cracked");
        put(map, "block/spruce_trapdoor", "assets/backportium/textures/blocks/spruce_trapdoor");
        put(map, "block/birch_trapdoor", "assets/backportium/textures/blocks/birch_trapdoor");
        put(map, "block/jungle_trapdoor", "assets/backportium/textures/blocks/jungle_trapdoor");
        put(map, "block/acacia_trapdoor", "assets/backportium/textures/blocks/acacia_trapdoor");
        put(map, "block/dark_oak_trapdoor", "assets/backportium/textures/blocks/dark_oak_trapdoor");
        put(map, "block/kelp", "assets/backportium/textures/blocks/kelp");
        put2(map, "block/kelp.png.mcmeta", "assets/backportium/textures/blocks/kelp.png.mcmeta");
        put(map, "block/kelp_plant", "assets/backportium/textures/blocks/kelp_plant");
        put2(map, "block/kelp_plant.png.mcmeta", "assets/backportium/textures/blocks/kelp_plant.png.mcmeta");
        put(map, "block/tube_coral", "assets/backportium/textures/blocks/tube_coral");
        put(map, "block/tube_coral_block", "assets/backportium/textures/blocks/tube_coral_block");
        put(map, "block/tube_coral_fan", "assets/backportium/textures/blocks/tube_coral_fan");
        put(map, "block/brain_coral", "assets/backportium/textures/blocks/brain_coral");
        put(map, "block/brain_coral_block", "assets/backportium/textures/blocks/brain_coral_block");
        put(map, "block/brain_coral_fan", "assets/backportium/textures/blocks/brain_coral_fan");
        put(map, "block/bubble_coral", "assets/backportium/textures/blocks/bubble_coral");
        put(map, "block/bubble_coral_block", "assets/backportium/textures/blocks/bubble_coral_block");
        put(map, "block/bubble_coral_fan", "assets/backportium/textures/blocks/bubble_coral_fan");
        put(map, "block/fire_coral", "assets/backportium/textures/blocks/fire_coral");
        put(map, "block/fire_coral_block", "assets/backportium/textures/blocks/fire_coral_block");
        put(map, "block/fire_coral_fan", "assets/backportium/textures/blocks/fire_coral_fan");
        put(map, "block/horn_coral", "assets/backportium/textures/blocks/horn_coral");
        put(map, "block/horn_coral_block", "assets/backportium/textures/blocks/horn_coral_block");
        put(map, "block/horn_coral_fan", "assets/backportium/textures/blocks/horn_coral_fan");
        put(map, "block/dead_tube_coral", "assets/backportium/textures/blocks/dead_tube_coral");
        put(map, "block/dead_tube_coral_block", "assets/backportium/textures/blocks/dead_tube_coral_block");
        put(map, "block/dead_tube_coral_fan", "assets/backportium/textures/blocks/dead_tube_coral_fan");
        put(map, "block/dead_brain_coral", "assets/backportium/textures/blocks/dead_brain_coral");
        put(map, "block/dead_brain_coral_block", "assets/backportium/textures/blocks/dead_brain_coral_block");
        put(map, "block/dead_brain_coral_fan", "assets/backportium/textures/blocks/dead_brain_coral_fan");
        put(map, "block/dead_bubble_coral", "assets/backportium/textures/blocks/dead_bubble_coral");
        put(map, "block/dead_bubble_coral_block", "assets/backportium/textures/blocks/dead_bubble_coral_block");
        put(map, "block/dead_bubble_coral_fan", "assets/backportium/textures/blocks/dead_bubble_coral_fan");
        put(map, "block/dead_fire_coral", "assets/backportium/textures/blocks/dead_fire_coral");
        put(map, "block/dead_fire_coral_block", "assets/backportium/textures/blocks/dead_fire_coral_block");
        put(map, "block/dead_fire_coral_fan", "assets/backportium/textures/blocks/dead_fire_coral_fan");
        put(map, "block/dead_horn_coral", "assets/backportium/textures/blocks/dead_horn_coral");
        put(map, "block/dead_horn_coral_block", "assets/backportium/textures/blocks/dead_horn_coral_block");
        put(map, "block/dead_horn_coral_fan", "assets/backportium/textures/blocks/dead_horn_coral_fan");

        put(map, "item/sea_pickle", "items/sea_pickle");
        put(map, "item/turtle_egg", "items/turtle_egg");
        put(map, "item/turtle_helmet", "items/turtle_helmet");
        put(map, "item/scute", "items/scute");
        put(map, "item/nautilus_shell", "items/nautilus_shell");
        put(map, "item/heart_of_the_sea", "items/heart_of_the_sea");
        put(map, "item/dried_kelp", "items/dried_kelp");
        put(map, "item/phantom_membrane", "items/phantom_membrane");
        put(map, "item/kelp", "items/kelp");
        put(map, "item/trident", "items/trident");

        put(map, "entity/trident", "entity/trident");
        put(map, "entity/trident_riptide");
        put(map, "entity/conduit/base", "entity/conduit/base");
        put(map, "entity/conduit/cage", "entity/conduit/cage");
        put(map, "entity/conduit/closed_eye", "entity/conduit/closed_eye");
        put(map, "entity/conduit/open_eye", "entity/conduit/open_eye");
        put(map, "entity/conduit/wind", "entity/conduit/wind");
//        put2(map, "entity/conduit/wind.png.mcmeta", "entity/conduit/wind.png.mcmeta");
        put(map, "entity/conduit/wind_vertical", "entity/conduit/wind_vertical");
//        put2(map, "entity/conduit/wind_vertical.png.mcmeta", "entity/conduit/wind_vertical.png.mcmeta");
        put(map, "entity/phantom", "entity/phantom");
        put(map, "entity/phantom_eyes", "entity/phantom_eyes");
        map.put("textures/entity/shulker/shulker.png", "minecraft:textures/entity/shulker/shulker.png");
        map.put("textures/block/shulker_box.png", "minecraft:textures/blocks/shulker_top.png");

        put(map, "mob_effect/conduit_power");
        put(map, "mob_effect/dolphins_grace");
        put(map, "mob_effect/slow_falling");

        put(map, "models/armor/turtle_layer_1");

        put(map, "particle/nautilus");

        put(map, "misc/enchanted_glint_entity", "misc/enchanted_entity_glint");

        AssetMoverAPI.fromMinecraft("1.20.1", map);
    }

    private static void put(AssetMap map, String kv) {
        put(map, kv, kv);
    }

    private static void put(AssetMap map, String key, String value) {
        put2(map, key + ".png", value + ".png");
    }

    private static void put2(AssetMap map, String key, String value) {
        map.put("textures/" + key, "textures/" + value);
    }
}
