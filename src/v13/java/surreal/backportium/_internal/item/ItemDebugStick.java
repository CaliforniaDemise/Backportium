package surreal.backportium._internal.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public class ItemDebugStick extends Item {

    public ItemDebugStick() {}

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(@NotNull ItemStack stack) {
        return true;
    }

    @NotNull
    @Override
    public IRarity getForgeRarity(@NotNull ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
