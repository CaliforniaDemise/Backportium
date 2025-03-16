package surreal.backportium.util;

import net.minecraftforge.fml.common.Loader;

public class IntegrationHelper {

    public static final String FLUIDLOGGED_MODID = "fluidlogged_api";
    public static final String FORESTRY_MODID = "forestry";
    public static final String APPLIED_ENERGISTICS_MODID = "appliedenergistics2";

    public static final boolean FLUIDLOGGED = Loader.isModLoaded(FLUIDLOGGED_MODID);
    public static final boolean FORESTRY = Loader.isModLoaded(FORESTRY_MODID);
    public static final boolean APPLIED_ENERGISTICS = Loader.isModLoaded(APPLIED_ENERGISTICS_MODID);
}
