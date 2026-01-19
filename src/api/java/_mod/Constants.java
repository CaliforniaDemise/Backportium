package _mod;

public class Constants {

    private static final String MOD_PKG = "surreal/backportium/";
    private static final String INTERNAL_PKG = MOD_PKG + "_internal/";
    private static final String VISITORS_PKG = INTERNAL_PKG + "core/visitor/";
    private static final String API_PKG = MOD_PKG + "api/";

    /* CORE */
    public static final String V_ACTION_ANIMATION = VISITORS_PKG + "ActionAnimation";
    public static final String V_BETTER_SHOULDER_ENTITIES = VISITORS_PKG + "BetterShoulderEntities";
    public static final String V_MODEL_OVERRIDE = VISITORS_PKG + "ModelOverride";
    public static final String V_MORE_BLOCK_STATES = VISITORS_PKG + "MoreBlockStates";
    public static final String V_CUSTOM_SPLASH_TEXTS = VISITORS_PKG + "CustomSplashTexts";
    public static final String V_TAGS = VISITORS_PKG + "Tags";

    /* 1.13 */
    public static final String V_AIR_BAR_DEPLETION = VISITORS_PKG + "AirBarDepletion";
    public static final String V_FIX_BANNER_SOUND = VISITORS_PKG + "FixBannerSound";
    public static final String V_BED_EXPLOSION_DEATH_MESSAGE = VISITORS_PKG + "BedExplosionDeathMessage";
    public static final String V_BLOCK_HUGE_MUSHROOM_ALIAS = VISITORS_PKG + "BlockHugeMushroomAlias";
    public static final String V_INTERPOLATED_CAMERA_MOVEMENT = VISITORS_PKG + "InterpolatedCameraMovement";
    public static final String V_CONDUIT_POWER_IMPLEMENTATION = VISITORS_PKG + "ConduitPowerImplementation";
    public static final String V_SLOW_FALLING_IMPLEMENTATION = VISITORS_PKG + "SlowFallingImplementation";
    public static final String V_UNCARVED_PUMPKIN = VISITORS_PKG + "UncarvedPumpkin";

    // MoreBiomeOverride
    public static final String V_MORE_BIOME_OVERRIDE = VISITORS_PKG + "MoreBiomeOverride";
    public static final String A_OVERRIDABLE_BIOME = API_PKG + "world/biome/Overridable";

    // EntityStates
    public static final String V_ENTITY_STATES = VISITORS_PKG + "EntityStates";
    public static final String A_ENTITY_WITH_STATE = API_PKG + "entity/EntityWithState";
    public static final String A_SET_SIZE = INTERNAL_PKG + "entity/SetSize";

    // BiomeNameTranslation
    public static final String V_BIOME_NAME_TRANSLATION = VISITORS_PKG + "BiomeNameTranslation";
    public static final String A_TRANSLATABLE = API_PKG + "world/biome/Translatable";

    // BubbleColumns
    public static final String V_BUBBLE_COLUMNS = VISITORS_PKG + "BubbleColumns";
    public static final String A_BUBBLE_COLUMN_INTERACTABLE = API_PKG + "entity/BubbleColumnInteractable";
    public static final String A_ROCKABLE_BOAT = INTERNAL_PKG + "entity/RockableBoat";

    // ItemEntityBuoyancy
    public static final String V_ITEM_ENTITY_BUOYANCY = VISITORS_PKG + "ItemEntityBuoyancy";
    public static final String C_MOD_LIST = MOD_PKG + "integration/ModList";

    // NewButtonStates
    public static final String V_NEW_BUTTON_STATES = VISITORS_PKG + "NewButtonStates";
    public static final String C_NEW_BUTTON = MOD_PKG + "block/properties/button/NewButton";

    // BetterWaterColor
    public static final String V_BETTER_WATER_COLOR = VISITORS_PKG + "BetterWaterColor";
    public static final String A_BETTER_WATER_COLOR = API_PKG + "world/biome/BetterWaterColor";

    // TridentImplementation
    public static final String V_TRIDENT_IMPLEMENTATION = VISITORS_PKG + "TridentImplementation";
    public static final String A_RIPTIDE_ENTITY = API_PKG + "entity/RiptideEntity";

    // WaterLogging
    public static final String V_WATER_LOGGING = VISITORS_PKG + "WaterLogging";
    public static final String A_LOGGED_ACCESS = INTERNAL_PKG + "world/LoggedAccess";
    public static final String A_LOGGABLE_CHUNK = INTERNAL_PKG + "world/chunk/LoggableChunk";
    public static final String C_LOGGING_MAP = INTERNAL_PKG + "world/chunk/LoggingMap";

    // SwimmingState
    public static final String V_SWIMMING_STATE = VISITORS_PKG + "SwimmingState";
    public static final String A_SWIMMING_ENTITY = API_PKG + "entity/SwimmingEntity";
    public static final String A_MODEL_BIPED_SWIMMING = INTERNAL_PKG + "client/model/ModelBipedSwimming";
    public static final String A_CLIENT_PLAYER_SWIMMING = INTERNAL_PKG + "client/entity/player/ClientPlayerSwimming";

    /* 1.16 */
    public static final String V_UNDERWATER_GRASS_TO_DIRT = VISITORS_PKG + "UnderwaterGrassToDirt";

    // NewWallStates
    public static final String V_NEW_WALL_STATES = VISITORS_PKG + "NewWallStates";
    public static final String A_NEW_WALL = MOD_PKG + "block/properties/wall/NewWall";
}