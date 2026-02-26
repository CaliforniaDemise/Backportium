package com.backportium.registry;

import com.backportium.client.Client;
import com.backportium.registry.client.ItemBlockMeshDefinition;
import com.backportium.registry.client.ModelProvider;
import com.backportium.registry.client.StateMapper;
import com.backportium.registry.client.TextureStitcher;
import com.backportium.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class RegistryManager {

    private final BlockRegistry blocks = new BlockRegistry(this);
    private final ItemRegistry items = new ItemRegistry(this);
    private final PotionRegistry potions = new PotionRegistry(this);
    private final PotionTypeRegistry potionTypes = new PotionTypeRegistry(this);

    private static final boolean CLIENT = FMLLaunchHandler.side().isClient();

    @SideOnly(Side.CLIENT)
    final List<TextureStitcher> stitchers = new ArrayList<>();
    @SideOnly(Side.CLIENT)
    final List<StateMapper> stateMappers = new ArrayList<>();

    @SideOnly(Side.CLIENT)
    public Client createClient() {
        return new Client(stitchers.toArray(new TextureStitcher[0]));
    }

    public void registerBlocks(RegistryEvent.Register<Block> event) {
        com.backportium.Registry.registerBlocks(blocks);
        blocks.registerAll(event);
    }

    public void registerItems(RegistryEvent.Register<Item> event) {
        com.backportium.Registry.registerItems(items);
        items.registerAll(event);
    }

    public void registerPotions(RegistryEvent.Register<Potion> event) {
        com.backportium.Registry.registerPotions(potions);
        potions.registerAll(event);
    }

    public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        com.backportium.Registry.registerPotionTypes(potionTypes);
        potionTypes.registerAll(event);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        for (Item item : items) {
            if (item instanceof ModelProvider) {
                ((ModelProvider) item).registerModels();
            }
            else {
                Block block = ItemUtil.getBlockFromItem(item);
                if (block != net.minecraft.init.Blocks.AIR) {
                    if (block instanceof ModelProvider) {
                        ((ModelProvider) block).registerModels();
                    }
                    else {
                        ModelLoader.setCustomMeshDefinition(item, new ItemBlockMeshDefinition());
                    }
                }
                else {
                    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
                }
            }
        }
        for (StateMapper mapper : stateMappers) {
            mapper.registerStateMapper();
        }
    }

    private static final class ItemRegistry extends Registry<Item> implements Items {

        public ItemRegistry(RegistryManager manager) {
            super(manager);
        }

        @Override
        public Item register(Item entry) {
            return super.register(entry);
        }

        @Override
        public void register(ResourceLocation name, Item item) {
            item.setRegistryName(name).setTranslationKey(name.getNamespace() + "_" + name.getPath());
            register(item);
        }
    }

    private static final class BlockRegistry extends Registry<Block> implements Blocks {

        public BlockRegistry(RegistryManager manager) {
            super(manager);
        }

        @Override
        public void registerItem(ResourceLocation name, Block block, @Nullable Function<Block, Item> itemFunction) {
            block.setRegistryName(name).setTranslationKey(name.getNamespace() + "_" + name.getPath());
            if (itemFunction != null) manager.items.register(name, itemFunction.apply(block));
            register(block);
        }
    }

    private static final class PotionRegistry extends Registry<Potion> implements Potions {

        public PotionRegistry(RegistryManager manager) {
            super(manager);
        }

        @Override
        public void register(Potion potion, ResourceLocation name) {
            potion.setRegistryName(name).setPotionName(name.getNamespace() + "_" + name.getPath());
            register(potion);
        }
    }

    private static final class PotionTypeRegistry extends Registry<PotionType> implements PotionTypes {

        public PotionTypeRegistry(RegistryManager manager) {
            super(manager);
        }

        @Override
        public void register(ResourceLocation name, Consumer<PotionTypeBuilder> consumer) {
            PotionTypeBuilder builder = new PotionTypeBuilder(name);
            consumer.accept(builder);
            register(builder.build());
        }
    }
}
