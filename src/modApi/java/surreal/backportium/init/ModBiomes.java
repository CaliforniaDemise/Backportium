package surreal.backportium.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import surreal.backportium.Tags;

public class ModBiomes {

    // 1.13
    public static Biome SMALL_END_ISLANDS;
    public static Biome END_MIDLANDS;
    public static Biome END_HIGHLANDS;
    public static Biome WARM_OCEAN;
    public static Biome LUKEWARM_OCEAN;
    public static Biome COLD_OCEAN;
    public static Biome FROZEN_OCEAN; // TODO ALREADY IN GAME
    public static Biome DEEP_WARM_OCEAN;
    public static Biome DEEP_LUKEWARM_OCEAN;
    public static Biome DEEP_COLD_OCEAN;
    public static Biome DEEP_FROZEN_OCEAN;

    // 1.14
    public static Biome BAMBOO_FOREST;

    // 1.16
    public static Biome BASALT_DELTA;
    public static Biome CRIMSON_FOREST;
    public static Biome SOUL_SAND_VALLEY;
    public static Biome WARPED_FOREST;

    // 1.17
    public static Biome DRIPSTONE_CAVE;
    public static Biome LUSH_CAVE;

    // 1.18
    public static Biome MEADOW;
    public static Biome GROVE;
    public static Biome SNOWY_SLOPES;
    public static Biome JAGGED_PEAKS;
    public static Biome FROZEN_PEAKS;
    public static Biome STONY_PEAKS;

    // 1.19
    public static Biome DEEP_DARK;
    public static Biome MANGROVE_SWAMP;

    // 1.20
    public static Biome CHERRY_GROOVE;

    public static final BiomeDictionary.Type FROZEN = BiomeDictionary.Type.getType("FROZEN", BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY);

    public static void init() {
        SMALL_END_ISLANDS = biome("small_end_islands");
        END_MIDLANDS = biome("end_midlands");
        END_HIGHLANDS = biome("end_highlands");
        WARM_OCEAN = biome("warm_ocean");
        LUKEWARM_OCEAN = biome("lukewarm_ocean");
        COLD_OCEAN = biome("cold_ocean");
        FROZEN_OCEAN = biome("minecraft", "frozen_ocean");
        DEEP_WARM_OCEAN = biome("deep_warm_ocean");
        DEEP_LUKEWARM_OCEAN = biome("deep_lukewarm_ocean");
        DEEP_COLD_OCEAN = biome("deep_cold_ocean");
        DEEP_FROZEN_OCEAN = biome("deep_frozen_ocean");

        BAMBOO_FOREST = biome("bamboo_forest");

        BASALT_DELTA = biome("basalt_delta");
        CRIMSON_FOREST = biome("crimson_forest");
        SOUL_SAND_VALLEY = biome("soul_sand_valley");
        WARPED_FOREST = biome("warped_forest");

        DRIPSTONE_CAVE = biome("dripstone_cave");
        LUSH_CAVE = biome("lush_cave");

        MEADOW = biome("meadow");
        GROVE = biome("grove");
        SNOWY_SLOPES = biome("snowy_slopes");
        JAGGED_PEAKS = biome("jagged_peaks");
        FROZEN_PEAKS = biome("frozen_peaks");
        STONY_PEAKS = biome("stony_peaks");

        DEEP_DARK = biome("deep_dark");
        MANGROVE_SWAMP = biome("mangrove_swamp");

        CHERRY_GROOVE = biome("cherry_groove");
    }

    private static Biome biome(String name) {
        return ForgeRegistries.BIOMES.getValue(new ResourceLocation(Tags.MOD_ID, name));
    }

    private static Biome biome(String modid, String name) {
        return ForgeRegistries.BIOMES.getValue(new ResourceLocation(modid, name));
    }
}
