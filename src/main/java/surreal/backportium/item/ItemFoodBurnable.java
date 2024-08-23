package surreal.backportium.item;

public class ItemFoodBurnable extends ItemFoodDefault {

    private final int burnTime;

    public ItemFoodBurnable(int burnTime, int amount, float saturation, int eatingTime, boolean isWolfFood) {
        super(amount, saturation, eatingTime, isWolfFood);
        this.burnTime = burnTime;
    }

    public ItemFoodBurnable(int burnTime, int amount, int eatingTime, boolean isWolfFood) {
        this(burnTime, amount, 0, eatingTime, isWolfFood);
    }

    public ItemFoodBurnable(int burnTime, int amount, float saturation, boolean isWolfFood) {
        this(burnTime, amount, saturation, 32, isWolfFood);
    }

    public ItemFoodBurnable(int burnTime, int amount, boolean isWolfFood) {
        this(burnTime, amount, 0, 32, isWolfFood);
    }
}
