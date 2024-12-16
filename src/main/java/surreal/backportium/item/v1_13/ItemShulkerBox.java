package surreal.backportium.item.v1_13;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.client.renderer.item.ModTEISR;

public class ItemShulkerBox extends ItemBlock {
    public ItemShulkerBox(Block block) {
        super(block);
        if (FMLLaunchHandler.side() == Side.CLIENT) this.setTEISR();
    }

    @SideOnly(Side.CLIENT)
    private void setTEISR() {
        this.setTileEntityItemStackRenderer(ModTEISR.get());
    }}
