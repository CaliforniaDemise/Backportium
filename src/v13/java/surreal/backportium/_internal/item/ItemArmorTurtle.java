package surreal.backportium._internal.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.init.ModArmorMaterials;

public class ItemArmorTurtle extends ItemArmor {

    private static final String TEXTURE = "backportium:textures/models/armor/turtle_layer_1.png";

    public ItemArmorTurtle(EntityEquipmentSlot equipmentSlotIn) {
        super(ModArmorMaterials.TURTLE, 3, equipmentSlotIn);
    }

    @Nullable
    @Override
    public String getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EntityEquipmentSlot slot, @NotNull String type) {
        return TEXTURE;
    }

    @Override
    public void onArmorTick(@NotNull World world, @NotNull EntityPlayer player, @NotNull ItemStack itemStack) {
        if (!world.isRemote && !player.isInWater()) {
            player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 210));
        }
    }
}
