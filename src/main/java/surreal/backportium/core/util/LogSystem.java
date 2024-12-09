package surreal.backportium.core.util;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

/**
 * Used for registering Stripped and Bark variants of logs without a headache.
 **/
public class LogSystem {

    public static LogSystem INSTANCE = new LogSystem();

    private final Map<Block, Block> stripped; // origLog, stripped
    private final Map<Block, Block> bark; // origLog, bark
    private final Map<Block, Block> sBark; // origLog, strippedBark
    private final Map<Block, ItemBlock> items; // stripped/bark/sbark, item

    public LogSystem() {
        this.stripped = new HashMap<>();
        this.bark = new HashMap<>();
        this.sBark = new HashMap<>();
        this.items = new HashMap<>();
    }

    public void register(Block log, Block stripped, Block bark, Block strippedBark) {
        this.stripped.put(log, stripped);
        this.bark.put(log, bark);
        this.sBark.put(log, strippedBark);
    }

    public void registerItem(Block addLog, ItemBlock item) {
        this.items.put(addLog, item);
    }

    public Block getStripped(Block origLog) {
        return this.stripped.get(origLog);
    }

    public Block getBark(Block origLog) {
        return this.bark.get(origLog);
    }

    public Block getStrippedBark(Block origLog) {
        return this.sBark.get(origLog);
    }

    public ItemBlock getItem(Block addLog) {
        return this.items.get(addLog);
    }

    public void forEachBlock(Consumer<Block> consumer) {
        this.stripped.keySet().forEach(consumer);
    }

    public static void cleanup() {
        INSTANCE = null;
    }
}
