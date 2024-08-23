package surreal.backportium.tile.v1_13;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockSeaLantern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.util.MutBlockPos;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileConduit extends TileEntity implements ITickable {

    private static final int UPDATE_TICK = 40;

    // Saved Values
    private int power = 0;

    // Unsaved Values
    private int updateTick = 0;

    private int animTick;

    public int getPower() {
        return power;
    }

    @SideOnly(Side.CLIENT)
    public int getFrame() {
        return animTick;
    }

    @Override
    public void update() {
        if (world.isRemote) {
            if (this.animTick == 0) this.animTick = this.world.rand.nextInt(10000);
            animTick++;
        }

        if (!world.isRemote && updateTick == 0) {
            handleUpdate();
            updateTick = UPDATE_TICK;
        }
        else updateTick--;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("power", this.power);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.power = compound.getInteger("power");
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new SPacketUpdateTileEntity(this.getPos(), 1, compound);
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    protected void handleUpdate() {
        if (shouldWork()) {
            int oldPower = this.power;
            this.power = this.getPowerFromBlocks();

            int radius = getRadius();
            List<EntityLivingBase> nearEntities = WorldHelper.getEntitiesInRadius(this.world, this.pos, radius, EntityLivingBase.class);
            if (nearEntities == null) return;

            for (EntityLivingBase entity : nearEntities) {
                if (shouldApplyToEntity(entity)) {
                    if (entity instanceof EntityMob) {
                        if (this.getDistanceSq(entity.posX, entity.posY, entity.posZ) <= 64) {
                            entity.attackEntityFrom(DamageSource.DROWN, 4.0F);
                        }
                    }
                    else if (entity instanceof EntityPlayer) {
                        entity.addPotionEffect(new PotionEffect(ModPotions.CONDUIT_POWER, (20 * 12) + 1, 0));
                    }
                }
            }

            if (this.power != oldPower) { // For server to clientside packets. Looks a bit dumb but seen worse.
                IBlockState state = this.getBlockType().getDefaultState();
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    public boolean isMaxPower() {
        return this.power == 5;
    }

    protected boolean shouldWork() {
        MutBlockPos mutPos = new MutBlockPos();
        for (int x = 1; x > -2; x--) {
            for (int y = 1; y > -2; y--) {
                for (int z = 1; z > -2; z--) {
                    Fluid fluid = WorldHelper.getFluid(world, mutPos.setPos(this.pos, x, y, z));
                    if (fluid != FluidRegistry.WATER) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected int countBlocks() {
        MutBlockPos mutPos = new MutBlockPos();

        int count = 0;

        for (int i = -2; i < 3; i++) {
            if (isBlock(world, mutPos.setPos(this.pos, i, 0, -2))) count++;
            if (isBlock(world, mutPos.setPos(this.pos, i, 0, 2))) count++;

            if (i != 0) {
                if (isBlock(world, mutPos.setPos(this.pos, 0, i, -2))) count++;
                if (isBlock(world, mutPos.setPos(this.pos, 0, i, 2))) count++;

                if (isBlock(world, mutPos.setPos(this.pos, -2, i, 0))) count++;
                if (isBlock(world, mutPos.setPos(this.pos, 2, i, 0))) count++;
            }

            if (Math.abs(i) != 2) {
                if (isBlock(world, mutPos.setPos(this.pos, -2, 0, i))) count++;
                if (isBlock(world, mutPos.setPos(this.pos, 2, 0, i))) count++;

                if (isBlock(world, mutPos.setPos(this.pos, 0, -2, i))) count++;
                if (isBlock(world, mutPos.setPos(this.pos, 0, 2, i))) count++;

                if (i != 0) {
                    if (isBlock(world, mutPos.setPos(this.pos, i, -2, 0))) count++;
                    if (isBlock(world, mutPos.setPos(this.pos, i, 2, 0))) count++;
                }
            }
        }

        return count;
    }

    protected boolean isBlock(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block instanceof BlockPrismarine || block instanceof BlockSeaLantern;
    }

    protected int getPowerFromBlocks() {
        int blockCount = countBlocks();
        return blockCount >= 16 ? ((blockCount - 7) / 7) : 0;
    }

    protected int getRadius() {
        return this.power > 0 ? 16 + (16 * this.power) : 0;
    }

    protected boolean shouldApplyToEntity(EntityLivingBase entity) {
        return entity.isInWater() || this.world.isRainingAt(entity.getPosition());
    }

    protected boolean shouldAttack() {
        return this.isMaxPower();
    }
}
