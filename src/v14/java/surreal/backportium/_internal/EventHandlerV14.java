package surreal.backportium._internal;

import net.minecraft.client.Minecraft;

import java.util.List;

public class EventHandlerV14 {

    protected static void getSplashTexts(List<String> list) {
        list.add(Minecraft.getMinecraft().getSession().getUsername() + " IS YOU");
        list.add("Rainbow turtle?");
        list.add("Something funny!");
        list.add("I need more context.");
        list.add("Ahhhhhh!");
        list.add("Don't worry, be happy!");
        list.add("Water bottle!");
        list.add("What's the question?");
        list.add("Plant a tree!");
        list.add("Go to the dentist!");
        list.add("What do you expect?");
        list.add("Look mum, I'm in a splash!");
        list.add("It came from space.");
    }
}
