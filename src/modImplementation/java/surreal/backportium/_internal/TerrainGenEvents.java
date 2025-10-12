package surreal.backportium._internal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TerrainGenEvents {

    public static void register() {
        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGenEvents());
    }

    @SubscribeEvent
    public void initGenLayers(WorldTypeEvent.InitBiomeGens event) {
        EventHandlerV13.initGenLayers(event);
    }
}
