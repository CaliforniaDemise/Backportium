package surreal.backportium.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class EntityItemThrowable extends AbstractEntityArrow {

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityItemThrowable.class, DataSerializers.ITEM_STACK);

    public EntityItemThrowable(World worldIn) {
        super(worldIn);
    }

    public EntityItemThrowable(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityItemThrowable(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ITEM, ItemStack.EMPTY);
    }

    @NotNull
    @Override
    protected ItemStack getArrowStack() {
        return this.getItem();
    }

    public ItemStack getItem() {
        return this.dataManager.get(ITEM);
    }

    public void setItem(@NotNull ItemStack stack) {
        this.dataManager.set(ITEM, stack);
        this.dataManager.setDirty(ITEM);
    }

    @Override
    public void onUpdate() {
        if (this.getItem().isEmpty()) this.setDead();
        super.onUpdate();
    }

    @Override
    public void writeEntityToNBT(@NotNull NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("Item", this.getItem().writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readEntityFromNBT(@NotNull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setItem(new ItemStack(compound.getCompoundTag("Item")));
    }
}
