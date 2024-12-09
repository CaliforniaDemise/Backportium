package surreal.backportium.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.Tags;
import surreal.backportium.entity.v1_13.EntityPhantom;
import surreal.backportium.entity.v1_13.EntityTrident;
import surreal.backportium.util.Registrar;

import java.util.function.Function;

public class ModEntities extends Registrar<EntityEntry> {

    private int networkId = 0;

    @ObjectHolder("backportium:trident") @Nullable public static final EntityEntry TRIDENT = null;
    @ObjectHolder("backportium:phantom") @Nullable public static final EntityEntry PHANTOM = null;

    public static final ResourceLocation LOOT_PHANTOM = getLootTable("phantom");

    public ModEntities() {
        super(8);
    }

    public EntityEntry register(EntityEntryBuilder<? extends Entity> builder) {
        EntityEntry entry = builder.build();
        return this.register(entry, entry.getName());
    }

    @Override
    protected EntityEntry register(@NotNull EntityEntry entry, @NotNull ResourceLocation location) {
        return super.register(entry, location);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        this.register();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (PHANTOM != null) LootTableList.register(LOOT_PHANTOM);
    }

    private void register() {
        this.register(builder(EntityTrident.class, EntityTrident::new, "trident", 1));
        this.register(builder(EntityPhantom.class, EntityPhantom::new, "phantom", 3).egg(0x3E4B80, 0x75DB00));
    }

    private <T extends Entity> EntityEntryBuilder<T> builder(Class<T> entityClass, Function<World, T> factory, String name, int updateFrequency) {
        EntityEntryBuilder<T> builder = EntityEntryBuilder.create();
        builder.entity(entityClass);
        builder.id(new ResourceLocation(Tags.MOD_ID, name), networkId++);
        builder.name(name);
        builder.factory(factory);
        builder.tracker(80, updateFrequency, true);
        return builder;
    }

    private static ResourceLocation getLootTable(String name) {
        return new ResourceLocation(Tags.MOD_ID, "entities/" + name);
    }
}
