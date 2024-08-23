package surreal.backportium.item.v1_13;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemArmorTurtle extends ItemArmor {

    public ItemArmorTurtle(ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, 2, equipmentSlotIn);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "minecraft:textures/models/armor/turtle_layer_1.png";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 210));
    }
}
