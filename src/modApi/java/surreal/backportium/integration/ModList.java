package surreal.backportium.integration;

import net.minecraftforge.fml.common.Loader;

public class ModList {

    public static final String ASSETMOVER_MODID = "assetmover";
    public static final String FLUIDLOGGED_MODID = "fluidlogged_api";
    public static final String AE2_MODID = "appliedenergistics2";

    public static final boolean ASSETMOVER = Loader.isModLoaded(ASSETMOVER_MODID);
    public static final boolean FLUIDLOGGED = Loader.isModLoaded(FLUIDLOGGED_MODID);
    public static final boolean AE2 = Loader.isModLoaded(AE2_MODID);
}
