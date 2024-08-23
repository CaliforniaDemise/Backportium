package surreal.backportium.item;

import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import surreal.backportium.api.item.OredictProvider;

public class ItemOreDict extends Item implements OredictProvider {

    private final String oreEntry;

    public ItemOreDict(String oreEntry) {
        this.oreEntry = oreEntry;
    }

    @Override
    public void registerOreEntries() {
        OreDictionary.registerOre(oreEntry, this);
    }
}
