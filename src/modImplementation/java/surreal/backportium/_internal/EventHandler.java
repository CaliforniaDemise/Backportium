package surreal.backportium._internal;

import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import surreal.backportium.event.MainMenuEvent;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {

    public static void getSplashTexts(MainMenuEvent.SplashText event) {
        List<String> texts = new ArrayList<>(event.getSplashTexts());
        EventHandlerV13.getSplashTexts(texts);
        event.setSplashText(texts.get(event.getRandom().nextInt(texts.size())));
    }

    public static void isPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        EventHandlerV13.isPotionApplicable(event);
    }

    public static void playNoteBlock(NoteBlockEvent.Play event) {
        EventHandlerV13.playNoteBlock(event);
    }

    public static void applyBonemeal(BonemealEvent event) {
        EventHandlerV13.applyBonemeal(event);
    }

    public static void decorateBiomePost(DecorateBiomeEvent.Post event) {
        EventHandlerV13.decorateBiomePost(event);
    }
}
