package surreal.backportium.api.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import surreal.backportium.sound.ModSounds;

import java.lang.reflect.Method;

public class RiptideHelper {

    private static final Method HANDLE_RIPTIDE, IS_IN_RIPTIDE, GET_RIPTIDE_TICK;

    public static SoundEvent getSound(int level) {
        switch (level) {
            default:
            case 1:
                return ModSounds.ITEM_TRIDENT_RIPTIDE_1;
            case 2:
                return ModSounds.ITEM_TRIDENT_RIPTIDE_2;
            case 3:
                return ModSounds.ITEM_TRIDENT_RIPTIDE_3;
        }
    }

    public static void handleRiptide(EntityLivingBase entity, ItemStack stack) {
        try {
            HANDLE_RIPTIDE.invoke(entity, stack);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isInRiptide(EntityLivingBase entity) {
        try {
            return (boolean) IS_IN_RIPTIDE.invoke(entity);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int getRiptideTickLeft(EntityLivingBase entity) {
        try {
            return (int) GET_RIPTIDE_TICK.invoke(entity);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            HANDLE_RIPTIDE = EntityLivingBase.class.getMethod("handleRiptide", ItemStack.class);
            IS_IN_RIPTIDE = EntityLivingBase.class.getMethod("isInRiptide");
            GET_RIPTIDE_TICK = EntityLivingBase.class.getMethod("getRiptideTickLeft");
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
