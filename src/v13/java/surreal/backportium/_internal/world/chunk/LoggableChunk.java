package surreal.backportium._internal.world.chunk;

import net.minecraft.world.chunk.Chunk;

public interface LoggableChunk {

    default LoggingMap getLoggingMap() { return null; }

    static LoggableChunk cast(Chunk chunk) { return (LoggableChunk) chunk; }
}
