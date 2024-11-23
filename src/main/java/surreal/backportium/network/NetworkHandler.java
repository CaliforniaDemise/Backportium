package surreal.backportium.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import surreal.backportium.Tags;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

    public static void init() {
        // Server
        INSTANCE.registerMessage(PacketItemEnchanted.class, PacketItemEnchanted.class, 0, Side.CLIENT);
    }
}
