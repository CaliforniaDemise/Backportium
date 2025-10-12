package surreal.backportium.init;

import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import surreal.backportium.api.item.UseAction;

import java.util.HashMap;
import java.util.Map;

public class ModActions {

    private static final Map<EnumAction, UseAction> ACTIONS = new HashMap<>();

    public static final EnumAction TRIDENT = EnumHelper.addAction("trident");

    public static UseAction getUseAction(ItemStack stack) {
        return getUseAction(stack.getItemUseAction());
    }

    public static UseAction getUseAction(EnumAction action) {
        return ACTIONS.get(action);
    }

    public static EnumAction register(EnumAction action, UseAction useAction) {
        ACTIONS.put(action, useAction);
        return action;
    }
}
