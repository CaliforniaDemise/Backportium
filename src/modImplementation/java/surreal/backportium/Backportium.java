package surreal.backportium;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium._internal.EventHandler;
import surreal.backportium._internal.OreGenHandler;
import surreal.backportium._internal.Register;
import surreal.backportium._internal.TerrainGenEvents;
import surreal.backportium._internal.bytecode.traverse.ClassTraverser;
import surreal.backportium._internal.client.ClientHandler;
import surreal.backportium._internal.registry.RegistryManager;
import surreal.backportium.event.MainMenuEvent;
import surreal.backportium.init.*;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.MOD_VERSION)
public class Backportium {

    @SideOnly(Side.CLIENT)
    private ClientHandler client;
    private RegistryManager manager = new RegistryManager(Tags.MOD_ID);

    public Backportium() {
        if (FMLLaunchHandler.side().isClient()) {
            this.client = new ClientHandler();
            this.manager.setClient(this.client);
        }
    }

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.ORE_GEN_BUS.register(new OreGenHandler());
        if (FMLLaunchHandler.side().isClient()) {
            this.client.construction(event);
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Register.registerAll(this.manager);
        if (FMLLaunchHandler.side().isClient()) {
            this.manager.registerEntityRenders();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.manager.registerTileEntities();
        TerrainGenEvents.register();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.manager = null;
        ClassTraverser.clear();
    }

    // Events
    @SubscribeEvent public void isPotionApplicable(PotionEvent.PotionApplicableEvent event) { EventHandler.isPotionApplicable(event); }
    @SubscribeEvent public void playNoteBlock(NoteBlockEvent.Play event) { EventHandler.playNoteBlock(event); }
    @SubscribeEvent public void applyBonemeal(BonemealEvent event) { EventHandler.applyBonemeal(event); }
    @SubscribeEvent public void decorateBiomePre(DecorateBiomeEvent.Pre event) { EventHandler.decorateBiomePre(event); }
    @SubscribeEvent public void decorateBiomePost(DecorateBiomeEvent.Post event) { EventHandler.decorateBiomePost(event); }

    @SideOnly(Side.CLIENT) @SubscribeEvent public void getSplashTexts(MainMenuEvent.SplashText event) { EventHandler.getSplashTexts(event); }
    @SideOnly(Side.CLIENT) @SubscribeEvent public void renderSpecificHand(RenderSpecificHandEvent event) { this.client.renderSpecificHand(event); }

    // Registration events
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        this.manager.blocks.registerAll(event);
        ModBlocks.init();
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        this.manager.items.registerAll(event);
        ModItems.init();
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        this.manager.potions.registerAll(event);
        ModPotions.init();
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        this.manager.biomes.registerAll(event);
        ModBiomes.init();
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        this.manager.sounds.registerAll(event);
        ModSounds.init();
    }

    @SubscribeEvent
    public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        this.manager.potionTypes.registerAll(event);
        ModPotionTypes.init();
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        this.manager.enchantments.registerAll(event);
        ModEnchantments.init();
    }

    @SubscribeEvent
    public void registerProfessions(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event) {
        this.manager.professions.registerAll(event);
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        this.manager.entities.registerAll(event);
        ModEntities.init();
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        this.manager.recipes.registerAll(event);
        Register.registerRecipes(this.manager.recipes);
    }

    @SideOnly(Side.CLIENT) @SubscribeEvent public void registerModels(ModelRegistryEvent event) { this.manager.registerModels(event); }
    @SideOnly(Side.CLIENT) @SubscribeEvent public void stitchTextures(TextureStitchEvent.Pre event) { this.client.stitchTextures(event); }
    @SideOnly(Side.CLIENT) @SubscribeEvent public void bakeModels(ModelBakeEvent event) { this.client.bakeModels(event); }
}
