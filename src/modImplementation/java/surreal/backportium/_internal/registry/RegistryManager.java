package surreal.backportium._internal.registry;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium._internal.block.TileEntityProvider;
import surreal.backportium._internal.client.ClientHandler;
import surreal.backportium._internal.client.renderer.model.StateMapProvider;

import java.util.LinkedList;
import java.util.List;

public class RegistryManager {

    private final String modId;
    @SideOnly(Side.CLIENT)
    private ClientHandler client;

    protected final List<TileEntityProvider> tileEntities = new LinkedList<>();
    @SideOnly(Side.CLIENT)
    protected final List<StateMapProvider> stateMaps = new LinkedList<>();

    public final RegistryBlock blocks = new RegistryBlock(this);
    public final RegistryItem items = new RegistryItem(this);
    public final RegistryPotion potions = new RegistryPotion(this);
    public final RegistryBiome biomes = new RegistryBiome(this);
    public final RegistrySound sounds = new RegistrySound(this);
    public final RegistryPotionType potionTypes = new RegistryPotionType(this);
    public final RegistryEnchantment enchantments = new RegistryEnchantment(this);
    public final RegistryProfession professions = new RegistryProfession(this);
    public final RegistryEntity entities = new RegistryEntity(this);
    public final RegistryRecipe recipes = new RegistryRecipe(this);

    public RegistryManager(String modId) {
        this.modId = modId;
    }

    public void registerTileEntities() {
        this.tileEntities.forEach(TileEntityProvider::registerTileEntity);
    }

    public void registerEntityRenders() {
        this.entities.registerRenders();
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        this.items.registerModels(event);
        this.stateMaps.forEach(StateMapProvider::registerStateMap);
    }

    protected String getModId() {
        return modId;
    }

    @SideOnly(Side.CLIENT)
    protected final ClientHandler getClient() {
        return this.client;
    }

    @SideOnly(Side.CLIENT)
    public final void setClient(ClientHandler client) {
        this.client = client;
    }
}