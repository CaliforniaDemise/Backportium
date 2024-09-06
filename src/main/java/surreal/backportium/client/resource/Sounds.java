package surreal.backportium.client.resource;

import com.cleanroommc.assetmover.AssetMoverAPI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Sounds {

    @SideOnly(Side.CLIENT)
    public static void initSounds() {
        AssetMap map = new AssetMap();

        /* BLOCKS */
        // block.pumpkin.carve
        put(map, "block/pumpkin/carve1");
        put(map, "block/pumpkin/carve2");

        /* ITEMS */
        // item.armor.equip_turtle
        put(map, "mob/turtle/armor");

        // item.trident.hit
        put(map, "item/trident/pierce1");
        put(map, "item/trident/pierce2");
        put(map, "item/trident/pierce3");

        // item.trident.hit_ground
        put(map, "item/trident/ground_impact1");
        put(map, "item/trident/ground_impact2");
        put(map, "item/trident/ground_impact3");
        put(map, "item/trident/ground_impact4");

        // item.trident.return
        put(map, "item/trident/return1");
        put(map, "item/trident/return2");
        put(map, "item/trident/return3");

        // item.trident.riptide_1
        put(map, "item/trident/riptide1");

        // item.trident.riptide_2
        put(map, "item/trident/riptide2");

        // item.trident.riptide_3
        put(map, "item/trident/riptide3");

        // item.trident.throw
        put(map, "item/trident/throw1");
        put(map, "item/trident/throw2");

        // item.trident.thunder
        put(map, "item/trident/thunder1");
        put(map, "item/trident/thunder2");

        /* ENTITY */
        // entity.drowned.shoot
        put(map, "item/trident/throw1");
        put(map, "item/trident/throw2");

        AssetMoverAPI.fromMinecraft("1.20.1", map);
    }

    private static void put(AssetMap map, String kv) {
        kv = "sounds/" + kv + ".ogg";
        map.put(kv, kv);
    }
}
