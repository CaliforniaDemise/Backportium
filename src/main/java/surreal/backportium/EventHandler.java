package surreal.backportium;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.util.WorldHelper;

import java.util.Random;

public class EventHandler {

    public static void isPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();

        if (effect.getPotion() == ModPotions.SLOW_FALLING) {
            EntityLivingBase entity = event.getEntityLiving();
            if (!entity.isNonBoss() && !(entity instanceof EntityShulker)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    // TODO Make it better... Seagrass works grows on all solid blocks which makes normal grass block implementation not work........
    public static void applyBonemeal(BonemealEvent event) {
        World worldIn = event.getWorld();
        Random rand = worldIn.rand;
        BlockPos pos = event.getPos();

         BlockPos blockpos = pos.up();

         if (WorldHelper.inWater(worldIn, blockpos) && worldIn.getBlockState(blockpos).getBlock() instanceof IFluidBlock && worldIn.getBlockState(pos).isNormalCube()) {
             event.setResult(Event.Result.ALLOW);
             for (int i = 0; i < 128; ++i) {
                 BlockPos blockpos1 = blockpos;
                 int j = 0;

                 while (true) {
                     if (j >= i / 16) {
                         if (WorldHelper.inWater(worldIn, blockpos1) && worldIn.getBlockState(blockpos1).getBlock() instanceof IFluidBlock) {
                             if (rand.nextInt(8) == 0) {
                                 BlockPos upPos = blockpos1.up();
                                 if (worldIn.getBlockState(upPos).getBlock() instanceof IFluidBlock) {
                                     IBlockState state = ModBlocks.SEAGRASS_DOUBLE.getDefaultState();
                                     ModBlocks.SEAGRASS_DOUBLE.place(worldIn, blockpos1, state);
                                 }
                             }
                             else {
                                 IBlockState iblockstate1 = ModBlocks.SEAGRASS.getDefaultState();
                                 if (ModBlocks.SEAGRASS.canBlockStay(worldIn, blockpos1, iblockstate1)) {
                                     worldIn.setBlockState(blockpos1, iblockstate1, 3);
                                 }
                             }
                         }

                         break;
                     }

                     blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                     BlockPos downPos = blockpos1.down();
                     if (!worldIn.getBlockState(downPos).isSideSolid(worldIn, downPos, EnumFacing.UP) || worldIn.getBlockState(blockpos1).isNormalCube()) {
                         break;
                     }

                     ++j;
                 }
             }
         }
    }
}
