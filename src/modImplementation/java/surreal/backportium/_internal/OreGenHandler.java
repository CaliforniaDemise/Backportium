package surreal.backportium._internal;

import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OreGenHandler {

    @SubscribeEvent
    public void generateOresPost(OreGenEvent.Post event) {
        EventHandlerV13.generateOresPost(event);
    }
}