package surreal.backportium.item.v1_13;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import surreal.backportium.item.ItemBlockSub;

import java.util.Objects;

public class ItemBlockCoralFan extends ItemBlockSub {

    public ItemBlockCoralFan(Block block) {
        super(block);
    }

    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "alive=true,inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 9, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "alive=false,inventory"));
    }
}
