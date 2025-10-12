package surreal.backportium.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBubbleColumnDown extends Particle {

    private float speed;

    public ParticleBubbleColumnDown(World world, double posX, double posY, double posZ) {
        super(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        this.setParticleTextureIndex(32);
        this.particleMaxAge = (int)(Math.random() * 60.0D) + 30;
        this.canCollide = false;
        this.motionX = 0.0D;
        this.motionY = -0.05D;
        this.motionZ = 0.0D;
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.particleGravity = 0.002F;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionX += 0.6F * MathHelper.cos(this.speed);
        this.motionZ += 0.6F * MathHelper.sin(this.speed);
        this.motionX *= 0.07D;
        this.motionZ *= 0.07D;
        this.move(this.motionX, this.motionY, this.motionZ);
        if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() != Material.WATER)
            this.setExpired();
        if (this.particleAge++ >= this.particleMaxAge || this.onGround) this.setExpired();
        this.speed = this.speed + 0.08F;
    }
}
