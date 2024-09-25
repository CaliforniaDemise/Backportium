package surreal.backportium.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import surreal.backportium.Tags;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name(Tags.MOD_ID)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-100)
public class BPPlugin implements IFMLLoadingPlugin {

    public static boolean FLUIDLOGGED;

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
        List<Object> coremodList = (List<Object>) data.get("coremodList");
        for (Object plugin : coremodList) {
            String pluginName = plugin.toString();
            if (pluginName.startsWith("Fluidlogged")) {
                FLUIDLOGGED = true;
                break;
            }
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return "surreal.backportium.core.BPTransformer";
    }
}
