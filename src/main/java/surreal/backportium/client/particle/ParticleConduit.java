package surreal.backportium.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleConduit extends Particle {

    protected static final TextureAtlasSprite CONDUIT_PARTICLE = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:particle/nautilus");
    private final Vec3d conduitPos;

    public ParticleConduit(World worldIn, double posXIn, double posYIn, double posZIn, float size, Vec3d pos) {
        super(worldIn, posXIn + 0.5D, posYIn + 0.5D, posZIn + 0.5D);
        this.canCollide = false;
        this.conduitPos = pos;
        this.particleMaxAge = 40;
        this.setParticleTexture(CONDUIT_PARTICLE);
        this.multipleParticleScaleBy(size);
        this.handleSpeed();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        double cX = this.conduitPos.x;
        double cY = this.conduitPos.y;
        double cZ = this.conduitPos.z;
        handleExpiration(cX, cY, cZ);
        double speed = 0.005D;
        this.motionY += speed * (cY - this.posY);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    private void handleSpeed() {
        double cX = this.conduitPos.x;
        double cZ = this.conduitPos.z;
        double speed = 0.05D;
        this.motionX = speed * (cX - this.posX);
        this.motionZ = speed * (cZ - this.posZ);
    }

    private void handleExpiration(double cX, double cY, double cZ) {
        if (this.check(this.posX, cX) && this.check(this.posY, cY) && this.check(this.posZ, cZ)) {
            this.setExpired();
        }
    }

    private boolean check(double p2, double p) {
        double size = 0.1D;
        double min = p - size, max = p + size;
        return p2 > min && p2 < max;
    }
}
