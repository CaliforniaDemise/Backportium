package surreal.backportium;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import surreal.backportium.api.enums.ModArmorMaterials;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.client.ClientHandler;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.entity.ModEntities;
import surreal.backportium.item.ModItems;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.recipe.ModRecipes;
import surreal.backportium.sound.ModSounds;

@Mod(modid = Tags.MOD_ID, name = "Backportium", version = Tags.MOD_VERSION)
@SuppressWarnings("unused")
public class Backportium {

    public static final EnumAction SPEAR = EnumHelper.addAction("SPEAR");

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientHandler.construction(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.registerTiles();
        ModArmorMaterials.register();
        ClientHandler.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModItems.registerOres();
    }

    // Registry Events
    @SubscribeEvent public void registerBlocks(RegistryEvent.Register<Block> event) { ModBlocks.registerBlocks(event); }
    @SubscribeEvent public void registerItems(RegistryEvent.Register<Item> event) { ModItems.registerItems(event); }
    @SubscribeEvent public void registerEntities(RegistryEvent.Register<EntityEntry> event) { ModEntities.registerEntities(event); }
    @SubscribeEvent public void registerEnchantments(RegistryEvent.Register<Enchantment> event) { ModEnchantments.registerEnchantments(event); }
    @SubscribeEvent public void registerPotions(RegistryEvent.Register<Potion> event) { ModPotions.registerPotions(event); }
    @SubscribeEvent public void registerPotionTypes(RegistryEvent.Register<PotionType> event) { ModPotions.registerPotionTypes(event); }
    @SubscribeEvent public void registerRecipes(RegistryEvent.Register<IRecipe> event) { ModRecipes.registerRecipes(event); }
    @SubscribeEvent public void registerSounds(RegistryEvent.Register<SoundEvent> event) { ModSounds.registerSounds(event); }

    // In-Game Events
    @SubscribeEvent public void isPotionApplicable(PotionEvent.PotionApplicableEvent event) { EventHandler.isPotionApplicable(event); }
    @SubscribeEvent public void applyBonemeal(BonemealEvent event) { EventHandler.applyBonemeal(event); }
}
