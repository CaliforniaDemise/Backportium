package surreal.backportium.item;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemFoodDefault extends ItemFood {

    private final int eatingTime;

    public ItemFoodDefault(int amount, float saturation, int eatingTime, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
        this.eatingTime = eatingTime;
    }

    public ItemFoodDefault(int amount, int eatingTime, boolean isWolfFood) {
        this(eatingTime, amount, 0, isWolfFood);
    }

    public ItemFoodDefault(int amount, float saturation, boolean isWolfFood) {
        this(amount, saturation, 32, isWolfFood);
    }

    public ItemFoodDefault(int amount, boolean isWolfFood) {
        this(amount, 32, isWolfFood);
    }

    @Override
    public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
        return eatingTime;
    }
}
