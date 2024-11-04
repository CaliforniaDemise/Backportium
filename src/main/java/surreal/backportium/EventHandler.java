package surreal.backportium;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.core.BPHooks;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.util.RandomHelper;
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

    public static void playNoteBlock(NoteBlockEvent.Play event) {
        World world = event.getWorld();
        BlockPos downPos = event.getPos().down();

        IBlockState downState = world.getBlockState(downPos);
        if (downState.getMaterial() == Material.CORAL) {
            event.setInstrument(NoteBlockEvent.Instrument.BASSDRUM);
        }
    }

    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();

        ItemStack stack = event.getItemStack();

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        EntityPlayer player = event.getEntityPlayer();

        {
            Block debarkedLog = BPHooks.DEBARKED_LOG_BLOCKS.get(block);
            if (debarkedLog != null) {
                if (stack.getItem().getHarvestLevel(stack, "axe", player, state) <= -1) return;
                if (!world.isRemote) {
                    world.setBlockState(pos, RandomHelper.copyState(state, debarkedLog));
                }
                player.swingArm(event.getHand());
                event.setUseItem(Event.Result.ALLOW);
                event.setResult(Event.Result.ALLOW);
                event.setCanceled(true);
            }
        }
    }

    // TODO Make it better... Seagrass works grows on all solid blocks which makes normal grass block implementation not work........
    public static void applyBonemeal(BonemealEvent event) {
        World worldIn = event.getWorld();

        Random rand = worldIn.rand;
        BlockPos pos = event.getPos();

        BlockPos blockpos = pos.up();

        if (WorldHelper.inWater(worldIn, blockpos) && worldIn.isSideSolid(pos, EnumFacing.UP)) {
            event.setResult(Event.Result.ALLOW);
            if (worldIn.isRemote) return;
            for (int i = 0; i < 64; ++i) {
                BlockPos blockpos1 = blockpos;
                int j = 0;

                while (true) {
                    if (j >= i / 16) {
                        IBlockState check = worldIn.getBlockState(blockpos1);
                        if (check.getBlock() == ModBlocks.SEAGRASS && worldIn.rand.nextInt(8) == 0) {
                            BlockPos upPos = blockpos1.up();
                            if (WorldHelper.inWater(worldIn, upPos)) {
                                ModBlocks.SEAGRASS_DOUBLE.place(worldIn, blockpos1, ModBlocks.SEAGRASS.getDefaultState());
                            }
                        }
                        else if (check.getBlock() != ModBlocks.SEAGRASS_DOUBLE) {
                            IBlockState state = ModBlocks.SEAGRASS.getDefaultState();
                            if (ModBlocks.SEAGRASS.canBlockStay(worldIn, blockpos1, state)) {
                                worldIn.setBlockState(blockpos1, state, 3);
                            }
                        }

                        break;
                    }

                    blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                    BlockPos downPos = blockpos1.down();
                    if (!worldIn.getBlockState(downPos).isSideSolid(worldIn, downPos, EnumFacing.UP) || worldIn.isSideSolid(blockpos1, EnumFacing.UP)) {
                        break;
                    }

                    ++j;
                }
            }
        }
    }
}
