package surreal.backportium.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import surreal.backportium.Tags;
import surreal.backportium.entity.v1_13.EntityPhantom;
import surreal.backportium.entity.v1_13.EntityTrident;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModEntities {

    private static final List<EntityEntry> ENTITIES = new ArrayList<>();
    private static int networkId = 0;

    public static final EntityEntry TRIDENT = register(builder(EntityTrident.class, EntityTrident::new, "trident", 1));
    public static final EntityEntry PHANTOM = register(builder(EntityPhantom.class, EntityPhantom::new, "phantom", 3).egg(0x3E4B80, 0x75DB00));

    public static final ResourceLocation LOOT_PHANTOM = registerLootTable("phantom");

    public static <T extends Entity> EntityEntry register(EntityEntryBuilder<T> builder) {
        EntityEntry entry = builder.build();
        ENTITIES.add(entry);
        return entry;
    }

    private static <T extends Entity> EntityEntryBuilder<T> builder(Class<T> entityClass, Function<World, T> factory, String name, int updateFrequency) {
        EntityEntryBuilder<T> builder = EntityEntryBuilder.create();
        builder.entity(entityClass);
        builder.id(new ResourceLocation(Tags.MOD_ID, name), networkId++);
        builder.name(name);
        builder.factory(factory);
        builder.tracker(80, updateFrequency, true);
        return builder;
    }

    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();
        ENTITIES.forEach(registry::register);
    }

    private static ResourceLocation registerLootTable(String name) {
        return LootTableList.register(new ResourceLocation(Tags.MOD_ID, "entities/" + name));
    }
}
