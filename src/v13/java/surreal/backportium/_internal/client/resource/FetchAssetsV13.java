package surreal.backportium._internal.client.resource;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FetchAssetsV13 {

    @SideOnly(Side.CLIENT)
    public static void fetchAssets(Assets assets) {
        /* BLOCKS */
        // Trapdoors
        assets.textures("block/spruce_trapdoor", "block/birch_trapdoor", "block/jungle_trapdoor", "block/acacia_trapdoor", "block/dark_oak_trapdoor");
        // Blue Ice
        assets.textures("block/blue_ice");
        // Bubble Column
        assets.assets(new String[] {
                "particle/bubble",
                "particle/bubble_pop_0", "particle/bubble_pop_1", "particle/bubble_pop_2", "particle/bubble_pop_3", "particle/bubble_pop_4"
            },
            null,
            new String[] {
                "block/bubble_column/bubble1", "block/bubble_column/bubble2", "block/bubble_column/bubble3",
                "block/bubble_column/upwards_ambient1", "block/bubble_column/upwards_ambient2", "block/bubble_column/upwards_ambient3", "block/bubble_column/upwards_ambient4", "block/bubble_column/upwards_ambient5", "block/bubble_column/upwards_inside",
                "block/bubble_column/whirlpool_ambient1", "block/bubble_column/whirlpool_ambient2", "block/bubble_column/whirlpool_ambient3", "block/bubble_column/whirlpool_ambient4", "block/bubble_column/whirlpool_ambient5", "block/bubble_column/whirlpool_inside"
            });
        // Pumpkin
        assets.sounds("block/pumpkin/carve1", "block/pumpkin/carve2");
        // Conduit
        assets.assets(
            new String[] {
                "block/conduit",
                "entity/conduit/base", "entity/conduit/cage", "entity/conduit/closed_eye", "entity/conduit/open_eye", "entity/conduit/wind", "entity/conduit/wind.png.mcmeta", "entity/conduit/wind_vertical", "entity/conduit/wind_vertical.png.mcmeta",
                "particle/nautilus"
            },
            null,
            new String[] {
                "block/conduit/activate",
                "block/conduit/ambient",
                "block/conduit/short1", "block/conduit/short2", "block/conduit/short3", "block/conduit/short4", "block/conduit/short5", "block/conduit/short6", "block/conduit/short7", "block/conduit/short8", "block/conduit/short9",
                "block/conduit/attack1", "block/conduit/attack2", "block/conduit/attack3",
                "block/conduit/deactivate"
            });
        // Corals
        assets.assets(
            new String[] {
                "block/tube_coral", "block/tube_coral_block", "block/tube_coral_fan", "block/dead_tube_coral", "block/dead_tube_coral_block", "block/dead_tube_coral_fan",
                "block/brain_coral", "block/brain_coral_block", "block/brain_coral_fan", "block/dead_brain_coral", "block/dead_brain_coral_block", "block/dead_brain_coral_fan",
                "block/bubble_coral", "block/bubble_coral_block", "block/bubble_coral_fan", "block/dead_bubble_coral", "block/dead_bubble_coral_block", "block/dead_bubble_coral_fan",
                "block/fire_coral", "block/fire_coral_block", "block/fire_coral_fan", "block/dead_fire_coral", "block/dead_fire_coral_block", "block/dead_fire_coral_fan",
                "block/horn_coral", "block/horn_coral_block", "block/horn_coral_fan", "block/dead_horn_coral", "block/dead_horn_coral_block", "block/dead_horn_coral_fan",
            },
            new String[] {
                "block/coral_fan", "block/coral_wall_fan"
            },
            new String[] {
                "dig/coral1", "dig/coral2", "dig/coral3", "dig/coral4",
                "step/coral1", "step/coral2", "step/coral3", "step/coral4", "step/coral5", "step/coral6"
            }
        );
        // Dried Kelp Block
        assets.textures("block/dried_kelp_top", "block/dried_kelp_side", "block/dried_kelp_bottom");
        // Wet Grass - Seagrass, Tall Seagrass, Kelp etc.
        assets.sounds("dig/wet_grass1", "dig/wet_grass2", "dig/wet_grass3", "dig/wet_grass4", "step/wet_grass1", "step/wet_grass2", "step/wet_grass3", "step/wet_grass4", "step/wet_grass5", "step/wet_grass6");
        // Kelp
        assets.textures("block/kelp", "block/kelp.png.mcmeta", "block/kelp_plant", "block/kelp_plant.png.mcmeta", "item/kelp");
        // Seagrass
        assets.assets(
            new String[] {
                "block/seagrass", "block/seagrass.png.mcmeta",
                "item/seagrass"
            },
            new String[] {
                "block/template_seagrass"
            },
            null
        );
        // Tall Seagrass
        assets.textures("block/tall_seagrass_bottom", "block/tall_seagrass_bottom.png.mcmeta", "block/tall_seagrass_top", "block/tall_seagrass_top.png.mcmeta");
        // Sea Pickle
        assets.assets(
            new String[] {
                "block/sea_pickle",
                "item/sea_pickle"
            },
            null,
            null
        );
        // Turtle Egg
        assets.assets(
            new String[] {
                "block/turtle_egg", "block/turtle_egg_slightly_cracked", "block/turtle_egg_very_cracked",
                "item/turtle_egg"
            },
            new String[] {
                "block/template_turtle_egg", "block/template_two_turtle_eggs", "block/template_three_turtle_eggs"
            },
            new String[] {
                "mob/turtle/egg/egg_break1", "mob/turtle/egg/egg_break2",
                "mob/turtle/egg/egg_crack1", "mob/turtle/egg/egg_crack2", "mob/turtle/egg/egg_crack3", "mob/turtle/egg/egg_crack4", "mob/turtle/egg/egg_crack5",
                "mob/turtle/egg/drop_egg1", "mob/turtle/egg/drop_egg2"
            }
        );
        /* ITEMS */
        // Dried Kelp
        assets.textures("item/dried_kelp");
        // Mob Bucket TODO Fetch them in mobs' assets method instead.
        assets.assets(
            new String[] {
                "item/cod_bucket", "item/salmon_bucket", "item/tropical_fish_bucket", "item/pufferfish_bucket"
            },
            null,
            new String[] {
                "item/bucket/empty_fish1", "item/bucket/empty_fish2", "item/bucket/empty_fish3"
            }
        );
        // Heart of the Sea
        assets.textures("item/heart_of_the_sea");
        // Nautilus Shell
        assets.textures("item/nautilus_shell");
        // Phantom Membrane
        assets.textures("item/phantom_membrane");
        // Turtle Scute
        assets.textures("item/turtle_scute");
        // Trident
        assets.assets(
            new String[] {
                "entity/trident",
                "entity/trident_riptide",
                "item/trident"
            },
            null,
            new String[] {
                "item/trident/throw1", "item/trident/throw2",
                "item/trident/pierce1", "item/trident/pierce2", "item/trident/pierce3",
                "item/trident/ground_impact1", "item/trident/ground_impact2", "item/trident/ground_impact3", "item/trident/ground_impact4",
                "item/trident/return1", "item/trident/return2", "item/trident/return3",
                "item/trident/riptide1", "item/trident/riptide2", "item/trident/riptide3",
                "item/trident/thunder1", "item/trident/thunder2",
            }
        );
        assets.assets(
            new String[] {
                "entity/equipment/humanoid/turtle_scute",
                "item/turtle_helmet"
            },
            null,
            new String[] {
                "mob/turtle/armor"
            }
        );
        // Axe Strip
        assets.sounds("item/axe/strip1", "item/axe/strip2", "item/axe/strip3", "item/axe/strip4");
        /* POTIONS */
        // Conduit Power
        assets.textures("mob_effect/conduit_power");
        // Dolphins Grace
        assets.textures("mob_effect/dolphins_grace");
        // Slow Falling
        assets.textures("mob_effect/slow_falling");
        /* ENTITIES */
        // Fish - General
        assets.sounds(
            "entity/fish/hurt1", "entity/fish/hurt2", "entity/fish/hurt3", "entity/fish/hurt4",
            "entity/fish/flop1", "entity/fish/flop2", "entity/fish/flop3", "entity/fish/flop4",
            "entity/fish/swim5", "entity/fish/swim6", "entity/fish/swim7"
        );
        // Dolphin
        assets.assets(
            null,
            null,
            new String[] {
                "mob/dolphin/blowhole1", "mob/dolphin/blowhole2",
                "mob/dolphin/idle1", "mob/dolphin/idle2", "mob/dolphin/idle3", "mob/dolphin/idle4", "mob/dolphin/idle5", "mob/dolphin/idle6",
                "mob/dolphin/idle_water1", "mob/dolphin/idle_water2", "mob/dolphin/idle_water3", "mob/dolphin/idle_water4", "mob/dolphin/idle_water5", "mob/dolphin/idle_water6", "mob/dolphin/idle_water7", "mob/dolphin/idle_water8", "mob/dolphin/idle_water9", "mob/dolphin/idle_water10",
                "mob/dolphin/attack1", "mob/dolphin/attack2", "mob/dolphin/attack3",
                "mob/dolphin/death1", "mob/dolphin/death2",
                "mob/dolphin/eat1", "mob/dolphin/eat2", "mob/dolphin/eat3",
                "mob/dolphin/hurt1", "mob/dolphin/hurt2", "mob/dolphin/hurt3",
                "mob/dolphin/jump1", "mob/dolphin/jump2", "mob/dolphin/jump3",
                "mob/dolphin/play1", "mob/dolphin/play2",
                "mob/dolphin/splash1", "mob/dolphin/splash2", "mob/dolphin/splash3",
                "mob/dolphin/swim1", "mob/dolphin/swim2", "mob/dolphin/swim3", "mob/dolphin/swim4"
            }
        );
        // Drowned
        assets.assets(
            null,
            null,
            new String[] {
                "mob/drowned/idle1", "mob/drowned/idle2", "mob/drowned/idle3", "mob/drowned/idle4", "mob/drowned/idle5",
                "mob/drowned/water/idle1", "mob/drowned/water/idle2", "mob/drowned/water/idle3", "mob/drowned/water/idle4",
                "mob/drowned/hurt1", "mob/drowned/hurt2", "mob/drowned/hurt3",
                "mob/drowned/death1", "mob/drowned/death2",
                "mob/drowned/water/death1", "mob/drowned/water/death2",
                "mob/drowned/water/hurt1", "mob/drowned/water/hurt2", "mob/drowned/water/hurt3",
                "mob/drowned/step1", "mob/drowned/step2", "mob/drowned/step3", "mob/drowned/step4", "mob/drowned/step5",
            }
        );
        // Phantom
        assets.assets(
            null,
            null,
            new String[] {
                "mob/phantom/idle1", "mob/phantom/idle2", "mob/phantom/idle3", "mob/phantom/idle4", "mob/phantom/idle5",
                "mob/phantom/bite1", "mob/phantom/bite2",
                "mob/phantom/death1", "mob/phantom/death2", "mob/phantom/death3",
                "mob/phantom/flap1", "mob/phantom/flap2", "mob/phantom/flap3", "mob/phantom/flap4", "mob/phantom/flap5", "mob/phantom/flap6",
                "mob/phantom/hurt1", "mob/phantom/hurt2", "mob/phantom/hurt3",
                "mob/phantom/swoop1", "mob/phantom/swoop2", "mob/phantom/swoop3", "mob/phantom/swoop4"
            }
        );
        // Pufferfish
        assets.assets(
            null,
            null,
            new String[] {
                "entity/pufferfish/blow_out1", "entity/pufferfish/blow_out2",
                "entity/pufferfish/blow_up1", "entity/pufferfish/blow_up2",
                "entity/pufferfish/death1", "entity/pufferfish/death2",
                "entity/pufferfish/flop1", "entity/pufferfish/flop2", "entity/pufferfish/flop3", "entity/pufferfish/flop4",
                "entity/pufferfish/hurt1", "entity/pufferfish/hurt2",
                "entity/pufferfish/sting1", "entity/pufferfish/sting2"
            }
        );
        // Turtle
        assets.assets(
            null,
            null,
            new String[] {
                "mob/turtle/idle1", "mob/turtle/idle2", "mob/turtle/idle3",
                "mob/turtle/death1", "mob/turtle/death2", "mob/turtle/death3",
                "mob/turtle/baby/death1", "mob/turtle/baby/death2",
                "mob/turtle/baby/egg_hatched1", "mob/turtle/baby/egg_hatched2", "mob/turtle/baby/egg_hatched3",
                "mob/turtle/hurt1", "mob/turtle/hurt2", "mob/turtle/hurt3", "mob/turtle/hurt4", "mob/turtle/hurt5",
                "mob/turtle/baby/hurt1", "mob/turtle/baby/hurt2",
                "mob/turtle/walk1", "mob/turtle/walk2", "mob/turtle/walk3", "mob/turtle/walk4", "mob/turtle/walk5",
                "mob/turtle/baby/shamble1", "mob/turtle/baby/shamble2", "mob/turtle/baby/shamble3", "mob/turtle/baby/shamble4",
                "mob/turtle/swim/swim1", "mob/turtle/swim/swim2", "mob/turtle/swim/swim3", "mob/turtle/swim/swim4", "mob/turtle/swim/swim5",
            }
        );
        // Squid
        assets.sounds("entity/squid/squirt1", "entity/squid/squirt2", "entity/squid/squirt3");
        // Husk
        assets.sounds("mob/husk/convert1", "mob/husk/convert2");
        /* OTHER TEXTURES */
        // Entity Item Enchantment Glint
        assets.textures("misc/enchanted_glint_entity", "misc/enchanted_glint_entity.png.mcmeta");
        /* OTHER SOUNDS */
        // Underwater Ambient
        assets.sounds(
            "ambient/underwater/enter1", "ambient/underwater/enter2", "ambient/underwater/enter3",
            "ambient/underwater/exit1", "ambient/underwater/exit2", "ambient/underwater/exit3",
            "ambient/underwater/underwater_ambience",
            "ambient/underwater/additions/bubbles1", "ambient/underwater/additions/bubbles2", "ambient/underwater/additions/bubbles3", "ambient/underwater/additions/bubbles4", "ambient/underwater/additions/bubbles5", "ambient/underwater/additions/bubbles6",
            "ambient/underwater/additions/water1", "ambient/underwater/additions/water2",
            "ambient/underwater/additions/animal1", "ambient/underwater/additions/animal2",
            "ambient/underwater/additions/bass_whale1", "ambient/underwater/additions/bass_whale2",
            "ambient/underwater/additions/crackles1", "ambient/underwater/additions/crackles2",
            "ambient/underwater/additions/driplets1", "ambient/underwater/additions/driplets2",
            "ambient/underwater/additions/earth_crack",
            "ambient/underwater/additions/dark1", "ambient/underwater/additions/dark2", "ambient/underwater/additions/dark3", "ambient/underwater/additions/dark4"
        );
        // Swim
        assets.sounds("liquid/swim1", "liquid/swim2", "liquid/swim3", "liquid/swim4", "liquid/swim5");
    }
}
