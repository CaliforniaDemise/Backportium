package surreal.backportium._internal.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium._internal.client.renderer.model.ItemBlockMeshDefinition;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium.util.BlockUtil;

import java.util.Objects;

public class RegistryItem extends Registry<Item> implements Items {

    protected RegistryItem(RegistryManager manager) {
        super(manager);
    }

    @Override
    public <V extends Item> V register(V entry, String name) {
        return register(entry, new ResourceLocation(manager.getModId(), name));
    }

    @Override
    public <V extends Item> V register(V entry, ResourceLocation location) {
        entry.setRegistryName(location).setTranslationKey(location.getNamespace() + "." + location.getPath());
        return super.register(entry);
    }

    @SideOnly(Side.CLIENT)
    protected void registerModels(ModelRegistryEvent event) {
        this.list.forEach(item -> {
            if (item instanceof ModelProvider) {
                ((ModelProvider) item).registerModels();
            }
            else {
                Block block = BlockUtil.getBlockFromItem(item);
                if (block != Blocks.AIR) {
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
        });
    }
}
