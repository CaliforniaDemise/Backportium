package surreal.backportium._internal.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.init.ModEnchantmentTypes;
import surreal.backportium.init.ModEnchantments;
import surreal.backportium.init.ModSounds;

import javax.annotation.Nonnull;

public class EnchantmentRiptide extends EnchantmentDefault {

    public EnchantmentRiptide() {
        super(Rarity.RARE, ModEnchantmentTypes.TRIDENT, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    protected boolean canApplyTogether(@Nonnull Enchantment ench) {
        return ench != ModEnchantments.LOYALTY && ench != ModEnchantments.CHANNELING;
    }

    @NotNull
    public static SoundEvent getRiptideSound(int level) {
        switch (level) {
            case 2: return ModSounds.ITEM_TRIDENT_RIPTIDE_2;
            case 3: return ModSounds.ITEM_TRIDENT_RIPTIDE_3;
            default: return ModSounds.ITEM_TRIDENT_RIPTIDE_1;
        }
    }

    public static boolean canRiptide(World world, EntityLivingBase entity) {
        return entity.isInWater() || world.isRainingAt(new BlockPos(entity).up());
    }
}
