package surreal.backportium.world;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import surreal.backportium.api.extension.BiomeExtension;

// BiomeProperties color defaults to default water color
// setWaterColor method does the emulateLegacy color setActualWaterColor sets the color.
public class BiomeColorHandler {

    public static final int DEFAULT_WATER_FOG_COLOR = 329011;
    public static final int DEFAULT_WATER_COLOR = 4159204;

    private static final int DEFAULT_WATER_COLOR_112 = 16777215;
    private static final int PERCEIVED_WATER_COLOR_112 = 0x2b3bf4;

    // BiomeId, waterColor|fogColor
    private static final Int2LongMap WATER_COLOR_MAP;

    public static int getWaterColor(Biome biome, int oldColor) {
        if (oldColor == DEFAULT_WATER_COLOR_112) return DEFAULT_WATER_COLOR;
        return oldColor;
    }

    public static int getWaterFogColor(Biome biome) {
        if (biome == null) return DEFAULT_WATER_FOG_COLOR;
        int color = ((BiomeExtension) biome).getWaterFogColor();
        if (color != -1) return color;
        return DEFAULT_WATER_FOG_COLOR;
    }

    public static int emulateLegacyColor(int modColor) {
        int modR = (modColor & 0xff0000) >> 16;
        int modG = (modColor & 0x00ff00) >> 8;
        int modB = (modColor & 0x0000ff);
        int legacyR = (PERCEIVED_WATER_COLOR_112 & 0xff0000) >> 16;
        int legacyG = (PERCEIVED_WATER_COLOR_112 & 0x00ff00) >> 8;
        int legacyB = (PERCEIVED_WATER_COLOR_112 & 0x0000ff);
        int displayedR = (modR * legacyR) / 255;
        int displayedG = (modG * legacyG) / 255;
        int displayedB = (modB * legacyB) / 255;
        return (displayedR << 16) | (displayedG << 8) | displayedB;
    }

    private static void addDefault(ResourceLocation location, int waterColor, int fogColor) {
        Biome biome = ForgeRegistries.BIOMES.getValue(location);
        if (biome == null) return;
        if (waterColor == -1 && fogColor == -1) return;
        long val = (long) fogColor | ((long) waterColor << 8);
        WATER_COLOR_MAP.put(Biome.getIdForBiome(biome), val);
    }

    static {
        WATER_COLOR_MAP = new Int2LongOpenHashMap();
        WATER_COLOR_MAP.defaultReturnValue(-1L + (-1L << 8));
        addDefault(new ResourceLocation("minecraft", "mutated_swampland"), 6388580, 2302743);
        addDefault(new ResourceLocation("minecraft", "swampland"), 6388580, 2302743);
        addDefault(new ResourceLocation("minecraft", "frozen_river"), 3750089, -1);
        addDefault(new ResourceLocation("minecraft", "frozen_ocean"), 3750089, -1);
        addDefault(new ResourceLocation("minecraft", "cold_beach"), 4020182, -1);
        addDefault(new ResourceLocation("minecraft", "taiga_cold"), 4020182, -1);
        addDefault(new ResourceLocation("minecraft", "taiga_cold_hills"), 4020182, -1);
        addDefault(new ResourceLocation("minecraft", "mutated_taiga_cold"), 4020182, -1);
        addDefault(new ResourceLocation("integrateddynamics", "biome_meneglin"), -1, 5613789);
        addDefault(new ResourceLocation("biomesoplenty", "bayou"), 0x62AF84, 0x0C211C);
        addDefault(new ResourceLocation("biomesoplenty", "dead_swamp"), 0x354762, 0x040511);
        addDefault(new ResourceLocation("biomesoplenty", "mangrove"), 0x448FBD, 0x061326);
        addDefault(new ResourceLocation("biomesoplenty", "mystic_grove"), 0x9C3FE4, 0x2E0533);
        addDefault(new ResourceLocation("biomesoplenty", "ominous_woods"), 0x312346, 0x0A030C);
        addDefault(new ResourceLocation("biomesoplenty", "tropical_rainforest"), 0x1FA14A, 0x02271A);
        addDefault(new ResourceLocation("biomesoplenty", "quagmire"), 0x433721, 0x0C0C03);
        addDefault(new ResourceLocation("biomesoplenty", "wetland"), 0x272179, 0x0C031B);
//      addDefault(new ResourceLocation("biomesoplenty", "bog"), -1, -1);
//      addDefault(new ResourceLocation("biomesoplenty", "moor"), -1, -1);
        addDefault(new ResourceLocation("thebetweenlands", "swamplands"), 1589792, 1589792);
        addDefault(new ResourceLocation("thebetweenlands", "swamplands_clearing"), 1589792, 1589792);
        addDefault(new ResourceLocation("thebetweenlands", "coarse_islands"), 1784132, 1784132);
        addDefault(new ResourceLocation("thebetweenlands", "deep_waters"), 1784132, 1784132);
        addDefault(new ResourceLocation("thebetweenlands", "marsh_0"), 4742680, 4742680);
        addDefault(new ResourceLocation("thebetweenlands", "marsh_1"), 4742680, 4742680);
        addDefault(new ResourceLocation("thebetweenlands", "patchy_islands"), 1589792, 1589792);
        addDefault(new ResourceLocation("thebetweenlands", "raised_isles"), 1784132, 1784132);
        addDefault(new ResourceLocation("thebetweenlands", "sludge_plains"), 3813131, 3813131);
        addDefault(new ResourceLocation("thebetweenlands", "sludge_plains_clearing"), 3813131, 3813131);
        addDefault(new ResourceLocation("traverse", "autumnal_woods"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "woodlands"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "mini_jungle"), 0x003320, 0x052721);
        addDefault(new ResourceLocation("traverse", "meadow"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "green_swamp"), 0x617B64, 0x232317);
        addDefault(new ResourceLocation("traverse", "red_desert"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "temperate_rainforest"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "badlands"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "mountainous_desert"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "rocky_plateau"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "forested_hills"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "birch_forested_hills"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "autumnal_wooded_hills"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "cliffs"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "glacier"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "glacier_spikes"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "snowy_coniferous_forest"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "lush_hills"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "desert_shrubland"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "thicket"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "arid_highland"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("traverse", "rocky_plains"), 0x3F76E4, 0x50533);
        addDefault(new ResourceLocation("thaumcraft", "magical_forest"), 3035999, -1);
        addDefault(new ResourceLocation("thaumcraft", "eerie"), 3035999, -1);
    }
}
