package surreal.backportium._internal.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import surreal.backportium.Tags;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

    public static void init() {
        // Server -> Client

        // Client -> Server
    }
}
