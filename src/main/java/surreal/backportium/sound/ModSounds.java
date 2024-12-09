package surreal.backportium.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.Tags;
import surreal.backportium.util.Registrar;

public class ModSounds extends Registrar<SoundEvent> {

    @ObjectHolder("backportium:block.pumpkin.carve") public static final SoundEvent BLOCK_PUMPKIN_CARVE = null;
    @ObjectHolder("backportium:block.conduit.activate") public static final SoundEvent BLOCK_CONDUIT_ACTIVATE = null;
    @ObjectHolder("backportium:block.conduit.ambient") public static final SoundEvent BLOCK_CONDUIT_AMBIENT = null;
    @ObjectHolder("backportium:block.conduit.ambient_short") public static final SoundEvent BLOCK_CONDUIT_AMBIENT_SHORT = null;
    @ObjectHolder("backportium:block.conduit.attack_target") public static final SoundEvent BLOCK_CONDUIT_ATTACK_TARGET = null;
    @ObjectHolder("backportium:block.conduit.deactivate") public static final SoundEvent BLOCK_CONDUIT_DEACTIVATE = null;
    @ObjectHolder("backportium:block.bubble_column.bubble_pop") public static final SoundEvent BLOCK_BUBBLE_COLUMN_BUBBLE_POP = null;
    @ObjectHolder("backportium:block.bubble_column.upwards_ambient") public static final SoundEvent BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT = null;
    @ObjectHolder("backportium:block.bubble_column.upwards_inside") public static final SoundEvent BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE = null;
    @ObjectHolder("backportium:block.bubble_column.whirlpool_ambient") public static final SoundEvent BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT = null;
    @ObjectHolder("backportium:block.bubble_column.whirlpool_inside") public static final SoundEvent BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE = null;

    @ObjectHolder("backportium:item.armor.equip_turtle") public static final SoundEvent ITEM_ARMOR_EQUIP_TURTLE = null;
    @ObjectHolder("backportium:item.trident.hit") public static final SoundEvent ITEM_TRIDENT_HIT = null;
    @ObjectHolder("backportium:item.trident.hit_ground") public static final SoundEvent ITEM_TRIDENT_HIT_GROUND = null;
    @ObjectHolder("backportium:item.trident.return") public static final SoundEvent ITEM_TRIDENT_RETURN = null;
    @ObjectHolder("backportium:item.trident.riptide_1") public static final SoundEvent ITEM_TRIDENT_RIPTIDE_1 = null;
    @ObjectHolder("backportium:item.trident.riptide_2") public static final SoundEvent ITEM_TRIDENT_RIPTIDE_2 = null;
    @ObjectHolder("backportium:item.trident.riptide_3") public static final SoundEvent ITEM_TRIDENT_RIPTIDE_3 = null;
    @ObjectHolder("backportium:item.trident.throw") public static final SoundEvent ITEM_TRIDENT_THROW = null;
    @ObjectHolder("backportium:item.trident.thunder") public static final SoundEvent ITEM_TRIDENT_THUNDER = null;

    @ObjectHolder("backportium:entity.drowned.shoot") public static final SoundEvent ENTITY_DROWNED_SHOOT = null;

    public ModSounds() {
        super(16);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        this.register();
    }

    private SoundEvent register(String name) {
        ResourceLocation location = new ResourceLocation(Tags.MOD_ID, name);
        SoundEvent event = new SoundEvent(location);
        this.register(event, location);
        return event;
    }

    @Override
    protected SoundEvent register(@NotNull SoundEvent entry, @NotNull ResourceLocation location) {
        return super.register(entry, location).setRegistryName(location);
    }

    private void register() {
        this.register("block.pumpkin.carve");
        this.register("block.conduit.activate");
        this.register("block.conduit.ambient");
        this.register("block.conduit.ambient.short");
        this.register("block.conduit.attack.target");
        this.register("block.conduit.deactivate");
        this.register("block.bubble_column.bubble_pop");
        this.register("block.bubble_column.upwards_ambient");
        this.register("block.bubble_column.upwards_inside");
        this.register("block.bubble_column.whirlpool_ambient");
        this.register("block.bubble_column.whirlpool_inside");

        this.register("item.armor.equip_turtle");
        this.register("item.trident.hit");
        this.register("item.trident.hit_ground");
        this.register("item.trident.return");
        this.register("item.trident.riptide_1");
        this.register("item.trident.riptide_2");
        this.register("item.trident.riptide_3");
        this.register("item.trident.throw");
        this.register("item.trident.thunder");

        this.register("entity.drowned.shoot");
    }
}
