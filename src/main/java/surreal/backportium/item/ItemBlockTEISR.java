package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import surreal.backportium.client.renderer.item.ModTEISR;

public class ItemBlockTEISR extends ItemBlock {

    public ItemBlockTEISR(Block block) {
        super(block);
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            this.setTileEntityItemStackRenderer(ModTEISR.get());
        }
    }
}
