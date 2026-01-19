package surreal.backportium._internal.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import surreal.backportium.init.ModEnchantmentTypes;
import surreal.backportium.init.ModEnchantments;
import surreal.backportium.init.ModSounds;

import javax.annotation.Nonnull;

public class EnchantmentChanneling extends EnchantmentDefault {

    public EnchantmentChanneling() {
        super(Rarity.RARE, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    }

    @Override
    protected boolean canApplyTogether(@Nonnull Enchantment ench) {
        return ench != ModEnchantments.RIPTIDE;
    }

    public static void handle(Entity entity, int level) {
        World world = entity.world;
        world.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.ITEM_TRIDENT_THUNDER, SoundCategory.NEUTRAL, 5.0F, 1.0F);
        world.addWeatherEffect(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false));
    }

    public static boolean canApplyTo(Entity entity) {
        return !entity.world.isRemote && entity.world.isThundering() && !entity.isInWater();
    }
}
