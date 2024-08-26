package surreal.backportium.core;

import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import surreal.backportium.Tags;
import surreal.backportium.util.IntegrationHelper;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.Name(Tags.MOD_ID)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-100)
@Optional.Interface(modid = IntegrationHelper.AQUA_ACROBATICS, iface = "zone.rong.mixinbooter.IMixinConfigHijacker")
public class BPPlugin implements IFMLLoadingPlugin {

    protected static File GAME_DIR;

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        GAME_DIR = (File) data.get("mcLocation");
    }

    @Override
    public String getAccessTransformerClass() {
        return "surreal.backportium.core.BPTransformer";
    }
}
