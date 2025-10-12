package surreal.backportium._internal.client.renderer.model;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.util.BlockUtil;

import java.util.Map;

public class ItemBlockMeshDefinition implements ItemMeshDefinition {

    private final Int2ObjectMap<ModelResourceLocation> models = new Int2ObjectOpenHashMap<>();

    @NotNull
    @Override
    public ModelResourceLocation getModelLocation(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return ModelLoader.MODEL_MISSING;
        if (this.models.isEmpty()) {
            if (stack.getMetadata() > 15 || !(stack.getItem() instanceof ItemBlock)) return ModelLoader.MODEL_MISSING;
            Block block = BlockUtil.getBlockFromItem(stack.getItem());
            if (block == Blocks.AIR) return ModelLoader.MODEL_MISSING;
            Map<IBlockState, ModelResourceLocation> map = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getBlockStateMapper().getVariants(block);
            map.forEach((key, value) -> this.models.put(block.getMetaFromState(key), value));
        }
        return this.models.getOrDefault(stack.getMetadata(), ModelLoader.MODEL_MISSING);
    }
}
