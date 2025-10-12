package surreal.backportium._internal.world.chunk;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LoggingMap {

    private final Short2ObjectMap<IBlockState> map = new Short2ObjectOpenHashMap<>();

    public LoggingMap() {}

    public IBlockState getLoggedState(int x, int y, int z) {
        short key = key(x, y, z);
        return map.getOrDefault(key, Objects.requireNonNull(Blocks.AIR).getDefaultState());
    }

    public void setLoggedState(int x, int y, int z, @Nullable IBlockState state) {
        short key = key(x, y, z);
        if (state == null || state.getBlock() == Blocks.AIR) {
            map.remove(key);
        }
        else {
            map.put(key, state);
        }
    }

    @SuppressWarnings("unused")
    public void writeToNBT(@NotNull NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        map.short2ObjectEntrySet().forEach(entry -> {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagCompound blockState = new NBTTagCompound();
            NBTUtil.writeBlockState(blockState, entry.getValue());
            compound.setShort("Key", entry.getShortKey());
            compound.setTag("BlockState", blockState);
            list.appendTag(compound);
        });
        tag.setTag("LoggedData", list);
    }

    @SuppressWarnings("unused")
    public void readFromNBT(@NotNull NBTTagCompound tag) {
        NBTTagList list = tag.getTagList("LoggedData", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            short key = compound.getShort("Key");
            IBlockState state = NBTUtil.readBlockState(compound.getCompoundTag("BlockState"));
            map.put(key, state);
        }
    }

    @SuppressWarnings("unused")
    public void readFromPacket(PacketBuffer buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            short key = buf.readShort();
            IBlockState state = Block.getStateById(buf.readInt());
            map.put(key, state);
        }
    }

    @SuppressWarnings("unused")
    public void writeToPacket(PacketBuffer buf) {
        buf.writeInt(map.size());
        map.short2ObjectEntrySet().forEach(entry -> {
            buf.writeShort(entry.getShortKey());
            buf.writeInt(Block.getStateId(entry.getValue()));
        });
    }

    private short key(int x, int y, int z) {
        x &= 15;
        y &= 255;
        z &= 15;
        return (short) (x << 12 | z << 8 | y);
    }
}
