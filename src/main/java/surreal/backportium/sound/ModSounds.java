package surreal.backportium.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import surreal.backportium.Tags;

import java.util.ArrayList;
import java.util.List;

public class ModSounds {

    private static final List<SoundEvent> SOUNDS = new ArrayList<>();

    public static final SoundEvent BLOCK_PUMPKIN_CARVE = register("block.pumpkin.carve");

    public static final SoundEvent ITEM_ARMOR_EQUIP_TURTLE = register("item.armor.equip_turtle");
    public static final SoundEvent ITEM_TRIDENT_HIT = register("item.trident.hit");
    public static final SoundEvent ITEM_TRIDENT_HIT_GROUND = register("item.trident.hit_ground");
    public static final SoundEvent ITEM_TRIDENT_RETURN = register("item.trident.return");
    public static final SoundEvent ITEM_TRIDENT_RIPTIDE_1 = register("item.trident.riptide_1");
    public static final SoundEvent ITEM_TRIDENT_RIPTIDE_2 = register("item.trident.riptide_2");
    public static final SoundEvent ITEM_TRIDENT_RIPTIDE_3 = register("item.trident.riptide_3");
    public static final SoundEvent ITEM_TRIDENT_THROW = register("item.trident.throw");
    public static final SoundEvent ITEM_TRIDENT_THUNDER = register("item.trident.thunder");

    public static final SoundEvent ENTITY_DROWNED_SHOOT = register("entity.drowned.shoot");

    public static SoundEvent register(String name) {
        ResourceLocation location = new ResourceLocation(Tags.MOD_ID, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(location);
        SOUNDS.add(event);
        return event;
    }

    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        SOUNDS.forEach(registry::register);
    }
}
