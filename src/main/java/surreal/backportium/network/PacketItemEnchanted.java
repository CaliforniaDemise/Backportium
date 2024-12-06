package surreal.backportium.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.entity.v1_13.EntityTrident;

import java.util.UUID;

/**
 * Used to know if enchantment glint should be rendered on item entity like Trident.
 * I don't know how EntityItem itself knows about it.
 * Server -> Client
 **/
public class PacketItemEnchanted implements IMessage {

    private UUID uuid;
    private boolean enchanted;

    public PacketItemEnchanted() {}

    public PacketItemEnchanted(UUID uuid, boolean enchanted) {
        this.uuid = uuid;
        this.enchanted = enchanted;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.uuid = new UUID(buf.readLong(), buf.readLong());
        this.enchanted = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.uuid.getMostSignificantBits());
        buf.writeLong(this.uuid.getLeastSignificantBits());
        buf.writeBoolean(this.enchanted);
    }

    public static class Handler implements IMessageHandler<PacketItemEnchanted, IMessage> {
        @Override
        public IMessage onMessage(PacketItemEnchanted message, MessageContext ctx) {
            if (FMLLaunchHandler.side().isClient()) this.onMessage(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void onMessage(PacketItemEnchanted message) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                for (Entity entity : world.getLoadedEntityList()) {
                    if (entity instanceof EntityTrident && entity.getUniqueID().equals(message.uuid)) {
                        ((EntityTrident) entity).setEnchanted(message.enchanted);
                        break;
                    }
                }
            });
        }
    }
}
