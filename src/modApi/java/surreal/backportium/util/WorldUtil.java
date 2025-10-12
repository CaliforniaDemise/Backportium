package surreal.backportium.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public final class WorldUtil {

    public static <T extends Entity> List<T> getEntitiesInRadius(World world, BlockPos pos, int blockRadius, Class<T> entityClass) {
        if (blockRadius <= 0) return null;
        Chunk posChunk = world.getChunk(pos);
        int chunkX = posChunk.x;
        int chunkZ = posChunk.z;
        ImmutableList.Builder<T> list = ImmutableList.builder();
        int chunkRadius = MathHelper.ceil((float) blockRadius / 16); // I don't think MAX_ENTITY_RADIUS is needed, but we'll see
        int radiusSq = blockRadius * blockRadius;
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                Chunk chunk = world.getChunk(chunkX + x, chunkZ + z);
                int minY = MathHelper.clamp((pos.getY() - blockRadius) / 16, 0, chunk.getEntityLists().length - 1);
                int maxY = MathHelper.clamp((pos.getY() + blockRadius) / 16, blockRadius / 16, chunk.getEntityLists().length - 1);
                if (chunk.isLoaded()) {
                    for (int i = minY; i <= maxY; i++) {
                        for (T entity : chunk.getEntityLists()[i].getByClass(entityClass)) {
                            int d0 = pos.getX() - (int) entity.posX;
                            int d1 = pos.getY() - (int) entity.posY;
                            int d2 = pos.getZ() - (int) entity.posZ;
                            int sq = d0 * d0 + d1 * d1 + d2 * d2;
                            if (sq <= radiusSq) list.add(entity);
                        }
                    }
                }
            }
        }
        return list.build();
    }

    public static <T extends Entity> List<T> getEntitiesInBlock(World world, BlockPos pos, Class<T> entityClass) {
        ImmutableList.Builder<T> list = ImmutableList.builder();
        Chunk chunk = world.getChunk(pos);
        if (chunk.isLoaded()) {
            for (T entity : chunk.getEntityLists()[Math.min(pos.getY(), chunk.getEntityLists().length - 1)].getByClass(entityClass)) {
                if (entity.posX < pos.getX() + 1 && entity.posX > pos.getX() - 1 && entity.posZ < pos.getZ() + 1 && entity.posZ > pos.getZ() - 1) {
                    list.add(entity);
                }
            }
        }
        return list.build();
    }

    private WorldUtil() {}
}
