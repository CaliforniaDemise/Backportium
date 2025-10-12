package surreal.backportium._internal.client.renderer.model;

import com.google.gson.Gson;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.Reader;

public interface ModelBlockDefinitionProvider {

    @SideOnly(Side.CLIENT)
    default ModelBlockDefinition getModelDefinition(IResourceManager manager, BlockModelShapes shapes, Reader reader, ResourceLocation location, Gson gson) {
        return ModelBlockDefinition.parseFromReader(reader, location);
    }
}
