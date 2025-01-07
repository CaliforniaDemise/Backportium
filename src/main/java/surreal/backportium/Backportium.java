package surreal.backportium;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import surreal.backportium.api.enums.ModArmorMaterials;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.client.ClientHandler;
import surreal.backportium.client.renderer.tile.TESRConduit;
import surreal.backportium.command.debug.CommandGenerate;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.entity.ModEntities;
import surreal.backportium.entity.v13.EntityTrident;
import surreal.backportium.item.ModItems;
import surreal.backportium.network.NetworkHandler;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.recipe.ModRecipes;
import surreal.backportium.sound.ModSounds;
import surreal.backportium.tile.v13.TileConduit;
import surreal.backportium.world.biome.ModBiomes;

@Mod(modid = Tags.MOD_ID, name = "Backportium", version = Tags.MOD_VERSION, dependencies = "after:*")
@SuppressWarnings("unused")
public class Backportium {

    public static final EnumAction SPEAR = EnumHelper.addAction("SPEAR");

    private Registries registries = new Registries();

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        if (FMLLaunchHandler.side().isClient()) ClientHandler.construction(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.registries.preInit(event);
        ModArmorMaterials.register();
        NetworkHandler.init();
        if (FMLLaunchHandler.side().isClient()) ClientHandler.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.registries.init(event);
        new WorldGenPumpkin();
        registerDispenseBehaviours();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.cleanup();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        if (FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            // Not so good attempt for testing world generations.
            // Imagine, it's too bad that I added a check. So, it only works in the dev environment.
            event.registerServerCommand(new CommandGenerate());
        }
    }

    public static void registerDispenseBehaviours() {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.TRIDENT, ((source, stack) -> {
            World world = source.getWorld();
            IBlockState state = source.getBlockState();
            int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack);
            if (riptide != 0 || stack.getItemDamage() == stack.getMaxDamage() - 1) return stack;
            EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
            int infinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
            EntityTrident trident = new EntityTrident(world, source.getX() + facing.getXOffset(), source.getY() + facing.getYOffset(), source.getZ() + facing.getZOffset(), stack);
            trident.shoot(facing.getXOffset(), facing.getYOffset(), facing.getZOffset(), 0.9F, 0.25F);
            world.playSound(null, source.getX(), source.getY(), source.getZ(), ModSounds.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
            world.spawnEntity(trident);
            if (infinity != 0) {
                trident.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                stack.attemptDamageItem(1, world.rand, null);
            }
            else trident.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
            return ItemStack.EMPTY;
        }));
    }

    // Registry Events
    @SubscribeEvent public void registerBlocks(RegistryEvent.Register<Block> event) { this.registries.register(event); }
    @SubscribeEvent public void registerItems(RegistryEvent.Register<Item> event) { this.registries.register(event); }
    @SubscribeEvent public void registerEntities(RegistryEvent.Register<EntityEntry> event) { this.registries.register(event); }
    @SubscribeEvent public void registerEnchantments(RegistryEvent.Register<Enchantment> event) { this.registries.register(event); }
    @SubscribeEvent public void registerPotions(RegistryEvent.Register<Potion> event) { this.registries.register(event); }
    @SubscribeEvent public void registerPotionTypes(RegistryEvent.Register<PotionType> event) { this.registries.register(event); }
    @SubscribeEvent public void registerRecipes(RegistryEvent.Register<IRecipe> event) { ModRecipes.registerRecipes(event); }
    @SubscribeEvent(priority = EventPriority.LOW) public void registerRecipesLate(RegistryEvent.Register<IRecipe> event) { ModRecipes.registerLateRecipes(event); }
    @SubscribeEvent public void registerSounds(RegistryEvent.Register<SoundEvent> event) { this.registries.register(event); }
    @SubscribeEvent public void registerBiomes(RegistryEvent.Register<Biome> event) { this.registries.register(event); }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent public void registerModels(ModelRegistryEvent event) {
        this.registries.ITEMS.registerModels(event);
        ClientRegistry.bindTileEntitySpecialRenderer(TileConduit.class, new TESRConduit());
    }

    // Load Events
    @SubscribeEvent public void loadLootTables(LootTableLoadEvent event) { EventHandler.loadLootTables(event); }

    // In-Game Events
    @SubscribeEvent public void isPotionApplicable(PotionEvent.PotionApplicableEvent event) { EventHandler.isPotionApplicable(event); }
    @SubscribeEvent public void applyBonemeal(BonemealEvent event) { EventHandler.applyBonemeal(event); }
    @SubscribeEvent public void playNoteBlock(NoteBlockEvent.Play event) { EventHandler.playNoteBlock(event); }
    @SubscribeEvent public void rightClickBlock(PlayerInteractEvent.RightClickBlock event) { EventHandler.rightClickBlock(event); }
    @SubscribeEvent public void decorateBiome(DecorateBiomeEvent.Post event) { EventHandler.decorateBiome(event); }

    // Additional Events
    @SubscribeEvent
    public void replaceMissingMappings(RegistryEvent.MissingMappings<Block> event) {
        for(RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getAllMappings()) {
            if (mapping.key.getNamespace().equals("aquaacrobatics")) {
                mapping.remap(ModBlocks.BUBBLE_COLUMN);
                return;
            }
        }
    }

    private void cleanup() {
        this.registries = null;
        System.gc();
    }

    private static class Registries {
        private final ModItems ITEMS = new ModItems();
        private final ModBlocks BLOCKS = new ModBlocks(ITEMS);
        private final ModEntities ENTITIES = new ModEntities();
        private final ModEnchantments ENCHANTMENTS = new ModEnchantments();
        private final ModPotions POTIONS = new ModPotions();
        private final ModSounds SOUNDS = new ModSounds();
        private final ModBiomes BIOMES = new ModBiomes();

        public void preInit(FMLPreInitializationEvent event) {
            ITEMS.preInit(event);
            BLOCKS.preInit(event);
            ENTITIES.preInit(event);
            ENCHANTMENTS.preInit(event);
            POTIONS.preInit(event);
            SOUNDS.preInit(event);
            BIOMES.preInit(event);
        }

        public void init(FMLInitializationEvent event) {
            ITEMS.init(event);
            BLOCKS.init(event);
            ENTITIES.init(event);
            ENCHANTMENTS.init(event);
            POTIONS.init(event);
            SOUNDS.init(event);
            BIOMES.init(event);
        }

        public <T extends IForgeRegistryEntry<T>> void register(RegistryEvent.Register<T> event) {
            IForgeRegistry<T> registry = event.getRegistry();
            if (registry.getRegistrySuperType() == Item.class) { ITEMS.registerEntries((RegistryEvent.Register<Item>) event); return; }
            if (registry.getRegistrySuperType() == Block.class) { BLOCKS.registerEntries((RegistryEvent.Register<Block>) event); return; }
            if (registry.getRegistrySuperType() == EntityEntry.class) { ENTITIES.registerEntries((RegistryEvent.Register<EntityEntry>) event); return; }
            if (registry.getRegistrySuperType() == Enchantment.class) { ENCHANTMENTS.registerEntries((RegistryEvent.Register<Enchantment>) event); return; }
            if (registry.getRegistrySuperType() == Potion.class) { POTIONS.registerEntries((RegistryEvent.Register<Potion>) event); return; }
            if (registry.getRegistrySuperType() == PotionType.class) { POTIONS.registerTypeEntries((RegistryEvent.Register<PotionType>) event); return; }
            if (registry.getRegistrySuperType() == SoundEvent.class) { SOUNDS.registerEntries((RegistryEvent.Register<SoundEvent>) event); return; }
            if (registry.getRegistrySuperType() == Biome.class) { BIOMES.registerEntries((RegistryEvent.Register<Biome>) event); return; }
        }
    }
}
