package surreal.backportium._internal.client.resource;

import com.cleanroommc.assetmover.AssetMoverAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import surreal.backportium.integration.ModList;

public class FetchAssets {

    private static final String VERSION = "1.21.4";

    public static void readAssets() {
        Logger logger = LogManager.getLogger("Backportium/Assets");
        if (!ModList.ASSETMOVER) {
            logger.warn("AssetMover is not installed. You should install it, if you don't have a resource pack.");
            return;
        }
        logger.info("Fetching assets from {}", VERSION);
        Assets assets = new Assets();
        FetchAssetsV13.fetchAssets(assets);
        FetchAssetsV16.fetchAssets(assets);
        AssetMoverAPI.fromMinecraft(VERSION, assets.build());
    }
}
