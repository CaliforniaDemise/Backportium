package surreal.backportium.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleBubbleColumnUp extends Particle {

    public ParticleBubbleColumnUp(World world, double x, double y, double z, double speedX, double speedY, double speedZ) {
        super(world, x, y, z, speedX, speedY, speedZ);
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(32);
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.motionX = speedX * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
        this.motionY = speedY * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
        this.motionZ = speedZ * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
        this.particleMaxAge = (int)(40.0D / (Math.random() * 0.8D + 0.2D));
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY += 0.005D;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.85D;
        this.motionY *= 0.85D;
        this.motionZ *= 0.85D;
        if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() != Material.WATER) {
            this.setExpired();
        }

        if (this.particleMaxAge-- <= 0) {
            this.setExpired();
        }

    }
}