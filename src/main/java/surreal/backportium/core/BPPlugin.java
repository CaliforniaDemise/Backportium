package surreal.backportium.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.libraries.LibraryManager;
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

    public static boolean DEBARK, DEBARKED_LOGS, FUTUREMC;

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
        searchForModsDir(GAME_DIR);
        List<Object> coremodList = (List<Object>) data.get("coremodList");
        for (Object plugin : coremodList) {
            String pluginName = plugin.toString();
            if (pluginName.startsWith("Fluidlogged")) FLUIDLOGGED = true;
            else if (pluginName.equals("FutureMC")) FUTUREMC = true;
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return "surreal.backportium.core.BPTransformer";
    }

    public static void searchForModsDir(File gameDir) {
        List<File> mods = LibraryManager.gatherLegacyCanidates(gameDir);
        for (File file : mods) {
            if (file.getName().startsWith("Debark-")) DEBARK = true; // Asies Debark
            else if (file.getName().startsWith("Debarked Logs-")) DEBARKED_LOGS = true; // BeetoGuys Debarked Logs
        }
    }
}
