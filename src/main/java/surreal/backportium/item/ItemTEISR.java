package surreal.backportium.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import surreal.backportium.client.renderer.item.ModTEISR;

public class ItemTEISR extends Item {

    public ItemTEISR() {
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            this.setTileEntityItemStackRenderer(ModTEISR.get());
        }
    }
}
