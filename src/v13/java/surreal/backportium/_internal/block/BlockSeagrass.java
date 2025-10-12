package surreal.backportium._internal.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium.block.BlockPlantWater;

import java.util.Objects;

public class BlockSeagrass extends BlockPlantWater implements ModelProvider {

    public BlockSeagrass(Block doublePlant) {
        super(Material.PLANTS, MapColor.WATER, doublePlant);
    }

    @Override
    public void registerModels() {
        Item item = Item.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory"));
    }
}
