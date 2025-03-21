package surreal.backportium.tile.v13;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockSeaLantern;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.client.particle.ParticleConduit;
import surreal.backportium.core.BPPlugin;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.sound.ModSounds;
import surreal.backportium.util.MutBlockPos;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TileConduit extends TileEntity implements ITickable {

    private static final int UPDATE_TICK = 40;

    // Saved Values
    private int power = 0;

    // Unsaved Values
    private EntityLivingBase toAttack = null;
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
            this.handleClientUpdate();
        }

        if (!world.isRemote && updateTick == 0) {
            handleUpdate();
            updateTick = UPDATE_TICK;
        } else updateTick--;
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
        int oldPower = this.power;
        boolean shouldUpdate = false;

        if (this.shouldWork()) {
            this.power = this.getPowerFromBlocks();
            if (this.power != 0) {
                this.world.playSound(null, this.pos, ModSounds.BLOCK_CONDUIT_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                this.world.playSound(null, this.pos, ModSounds.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (this.toAttack != null && (this.toAttack.isDead || !this.shouldApplyToEntity(this.toAttack) || this.getDistanceSq(this.toAttack.posX, this.toAttack.posY, this.toAttack.posZ) > 64)) this.toAttack = null;

                int radius = getRadius();
                List<EntityLivingBase> nearEntities = WorldHelper.getEntitiesInRadius(this.world, this.pos, radius, EntityLivingBase.class);
                if (nearEntities == null) return;

                for (EntityLivingBase entity : nearEntities) {
                    if (shouldApplyToEntity(entity)) {
                        if (entity instanceof EntityPlayer) {
                            entity.addPotionEffect(new PotionEffect(ModPotions.CONDUIT_POWER, 13 * 20, 0, true, false));
                        }
                        else if (this.toAttack == null && this.shouldAttack() && entity instanceof EntityMob) {
                            if (this.getDistanceSq(entity.posX, entity.posY, entity.posZ) <= 64) {
                                this.toAttack = entity;
                            }
                        }
                    }
                }

                if (this.toAttack != null && this.shouldAttack()) {
                    this.world.playSound(null, this.pos, ModSounds.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.toAttack.attackEntityFrom(DamageSource.DROWN, 4.0F);
                    if (this.toAttack.isDead) this.toAttack = null;
                }
            }
        }
        else this.power = 0;

        if (this.power != oldPower) {
            shouldUpdate = true;
            if (this.power == 0) {
                this.world.playSound(null, this.pos, ModSounds.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            else if (oldPower == 0) {
                this.world.playSound(null, this.pos, ModSounds.BLOCK_CONDUIT_ACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

        if (shouldUpdate) {
            IBlockState state = this.getBlockType().getDefaultState();
            world.notifyBlockUpdate(pos, state, state, 3);
            this.markDirty();
        }
    }

    protected void handleClientUpdate() {
        if (this.power > 0) {
            if (this.toAttack != null && (this.toAttack.isDead || !this.shouldApplyToEntity(this.toAttack) || this.getDistanceSq(this.toAttack.posX, this.toAttack.posY, this.toAttack.posZ) > 64)) this.toAttack = null;
            if (this.toAttack == null && this.shouldAttack()) {
                int radius = getRadius();
                List<EntityMob> nearEntities = WorldHelper.getEntitiesInRadius(this.world, this.pos, radius, EntityMob.class);
                if (nearEntities == null) return;
                for (EntityMob entity : nearEntities) {
                    if (shouldApplyToEntity(entity) && this.getDistanceSq(entity.posX, entity.posY, entity.posZ) <= 64) {
                        this.toAttack = entity;
                        break;
                    }
                }
            }

            this.spawnParticles();
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
                    if (!BPPlugin.FLUIDLOGGED && x == 0 && y == 0 && z == 0) continue;
                    Fluid fluid = WorldHelper.getFluid(world, mutPos.setPos(this.pos, x, y, z));
                    if (fluid != FluidRegistry.WATER) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Kind of like countBlocks but for spawning particles
    protected void spawnParticles() {
        Random random = this.world.rand;

        // Particles from entity that gets attacked
        if (this.toAttack != null) {
            Vec3d entityPos = new Vec3d(this.toAttack.posX, this.toAttack.posY + 0.25D, this.toAttack.posZ);
            int loop = 32;
            double aRad = 2.0D * Math.PI / loop;
            double length = 1.0D;

            double y = entityPos.y + 1.75D;

            for (int i = 0; i < loop; i++) {
                double radian = aRad * i;
                double xPos = entityPos.x + length * Math.cos(radian);
                double zPos = entityPos.z + length * Math.sin(radian);
                spawnParticleFromEntity(random, xPos, y, zPos, entityPos);
            }
        }

        // Particles from Prismarines
        MutBlockPos mutPos = new MutBlockPos();
        Vec3d conduitPos = new Vec3d(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D);

        for (int i = -2; i < 3; i++) {
            if (this.isBlock(this.world, mutPos.setPos(this.pos, i, 0, -2)))
                this.spawnParticleFromBlock(random, mutPos, conduitPos);

            if (this.isBlock(this.world, mutPos.setPos(this.pos, i, 0, 2)))
                this.spawnParticleFromBlock(random, mutPos, conduitPos);

            if (i != 0) {
                if (this.isBlock(this.world, mutPos.setPos(this.pos, 0, i, -2)))
                    this.spawnParticleFromBlock(random, mutPos, conduitPos);

                if (this.isBlock(this.world, mutPos.setPos(this.pos, 0, i, 2)))
                    this.spawnParticleFromBlock(random, mutPos, conduitPos);


                if (this.isBlock(this.world, mutPos.setPos(this.pos, -2, i, 0)))
                    this.spawnParticleFromBlock(random, mutPos, conduitPos);

                if (this.isBlock(this.world, mutPos.setPos(this.pos, 2, i, 0)))
                    this.spawnParticleFromBlock(random, mutPos, conduitPos);

            }

            if (Math.abs(i) != 2) {
                if (this.isBlock(this.world, mutPos.setPos(this.pos, -2, 0, i)))
                    this.spawnParticleFromBlock(random, mutPos, conduitPos);

                if (this.isBlock(this.world, mutPos.setPos(this.pos, 2, 0, i)))
                    this.spawnParticleFromBlock(random, mutPos, conduitPos);


                if (this.isBlock(this.world, mutPos.setPos(this.pos, 0, -2, i)))
                    this.spawnParticleFromBlock(random, mutPos, conduitPos);

                if (this.isBlock(this.world, mutPos.setPos(this.pos, 0, 2, i)))
                    this.spawnParticleFromBlock(random, mutPos, conduitPos);

                if (i != 0) {
                    if (this.isBlock(this.world, mutPos.setPos(this.pos, i, -2, 0)))
                        this.spawnParticleFromBlock(random, mutPos, conduitPos);

                    if (this.isBlock(this.world, mutPos.setPos(this.pos, i, 2, 0)))
                        this.spawnParticleFromBlock(random, mutPos, conduitPos);
                }
            }
        }
    }

    private void spawnParticleFromBlock(Random random, BlockPos pos, Vec3d conduitPos) {
        if (random.nextInt(32) != 0) return;
        for (int i = 0; i < random.nextInt(3); i++) {
            double x = pos.getX() + (random.nextDouble() - 0.5D);
            double y = pos.getY();
            double z = pos.getZ() + (random.nextDouble() - 0.5D);
            float size = world.rand.nextFloat() * 0.9F;
            ParticleConduit conduit = new ParticleConduit(this.world, x, y, z, size, conduitPos);
            Minecraft.getMinecraft().effectRenderer.addEffect(conduit);
        }
    }

    private void spawnParticleFromEntity(Random random, double posX, double posY, double posZ, Vec3d conduitPos) {
        if (random.nextInt(32) != 0) return;
        for (int i = 0; i < random.nextInt(3); i++) {
            double x = posX + (random.nextDouble() - 0.5D);
            double y = posY;
            double z = posZ + (random.nextDouble() - 0.5D);
            float size = world.rand.nextFloat() * 0.9F;
            ParticleConduit conduit = new ParticleConduit(this.world, x, y, z, size, conduitPos);
            Minecraft.getMinecraft().effectRenderer.addEffect(conduit);
        }
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

    public static boolean shouldApplyToEntity(EntityLivingBase entity) {
        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        return (entity.isInWater() && entity.world.getBlockState(pos).getMaterial() == Material.WATER) || entity.world.isRainingAt(pos);
    }

    protected boolean shouldAttack() {
        return this.isMaxPower();
    }
}
