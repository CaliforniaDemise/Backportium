package surreal.backportium._internal;

import surreal.backportium._internal.registry.*;

public class Register {

    public static void registerAll(RegistryManager manager) {
        registerEntityMoves();
        registerActions();
        registerSoundTypes();
        registerArmorMaterials();
        registerBlocks(manager.blocks);
        registerItems(manager.items);
        registerPotions(manager.potions);
        registerPotionTypes(manager.potionTypes);
        registerBiomes(manager.biomes);
        registerSounds(manager.sounds);
        registerEnchantments(manager.enchantments);
        registerProfessions(manager.professions);
        registerEntities(manager.entities);
        postRegister();
    }

    public static void registerRecipes(Recipes registry) {
        RegisterV13.registerRecipes(registry);
    }

    private static void registerEntityMoves() {
        RegisterV13.registerEntityStates();
    }

    private static void registerActions() {
        RegisterV13.registerActions();
    }

    private static void registerSoundTypes() {
        RegisterV13.registerSoundTypes();
    }

    private static void registerArmorMaterials() {
        RegisterV13.registerArmorMaterials();
    }

    private static void registerBlocks(Blocks registry) {
        RegisterV13.registerBlocks(registry);
    }

    private static void registerItems(Items registry) {
        RegisterV13.registerItems(registry);
    }

    private static void registerPotions(Potions registry) {
        RegisterV13.registerPotions(registry);
    }

    private static void registerPotionTypes(PotionTypes registry) {
        RegisterV13.registerPotionTypes(registry);
    }

    private static void registerBiomes(Biomes registry) {
        RegisterV13.registerBiomes(registry);
    }

    private static void registerSounds(Sounds registry) {
        RegisterV13.registerSounds(registry);
    }

    private static void registerEnchantments(Enchantments registry) {
        RegisterV13.registerEnchantments(registry);
    }

    private static void registerProfessions(Professions registry) {
    }

    private static void registerEntities(Entities registry) {
        RegisterV13.registerEntities(registry);
    }

    private static void postRegister() {
        RegisterV13.postRegister();
    }
}
