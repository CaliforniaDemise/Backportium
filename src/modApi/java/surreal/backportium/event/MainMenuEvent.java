package surreal.backportium.event;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class MainMenuEvent extends Event {

    private final GuiMainMenu gui;

    protected MainMenuEvent(GuiMainMenu gui) {
        this.gui = gui;
    }

    public GuiMainMenu getGui() {
        return gui;
    }

    public static class SplashText extends MainMenuEvent {

        private final List<String> splashTexts;
        private final String splashText;
        private final Random random;
        private String newSplashText;

        public SplashText(GuiMainMenu gui, String splashText, List<String> splashTexts, Random random) {
            super(gui);
            this.splashTexts = Collections.unmodifiableList(splashTexts);
            this.splashText = splashText;
            this.newSplashText = splashText;
            this.random = random;
        }

        @Unmodifiable
        public List<String> getSplashTexts() {
            return splashTexts;
        }

        public String getSplashText() {
            return splashText;
        }

        public Random getRandom() {
            return random;
        }

        public String getNewSplashText() {
            return newSplashText;
        }

        public void setSplashText(String newSplashText) {
            this.newSplashText = newSplashText;
        }
    }
}
