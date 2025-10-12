package surreal.backportium.block.properties.wall;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import surreal.backportium._internal.client.renderer.model.ModelBlockDefinitionProvider;
import surreal.backportium.api.annotations.Extension;
import vazkii.quark.base.block.BlockQuarkWall;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Objects;

import static surreal.backportium.block.properties.wall.NewWallProperties.*;

@SuppressWarnings("unused")
@Extension({BlockWall.class, BlockQuarkWall.class})
public interface NewWall extends ModelBlockDefinitionProvider {

    static boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing p_176253_3_) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, p_176253_3_);
        boolean flag = blockfaceshape == BlockFaceShape.MIDDLE_POLE_THIN || blockfaceshape == BlockFaceShape.MIDDLE_POLE_THICK || blockfaceshape == BlockFaceShape.MIDDLE_POLE && block instanceof BlockFenceGate;
        boolean isExcepBlockForAttachWithPiston = block instanceof BlockShulkerBox || block instanceof BlockLeaves || block instanceof BlockTrapDoor || block == Blocks.BEACON || block == Blocks.CAULDRON || block == Blocks.GLASS || block == Blocks.GLOWSTONE || block == Blocks.ICE || block == Blocks.SEA_LANTERN || block == Blocks.STAINED_GLASS;
        isExcepBlockForAttachWithPiston |= block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD;
        isExcepBlockForAttachWithPiston |= block == Blocks.BARRIER || block == Blocks.MELON_BLOCK || block == Blocks.PUMPKIN || block == Blocks.LIT_PUMPKIN;
        return !isExcepBlockForAttachWithPiston && blockfaceshape == BlockFaceShape.SOLID || flag;
    }

    default IBlockState getActualState_NewWall(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean up = false;
        EnumMap<EnumFacing, Connection> connections = new EnumMap<>(EnumFacing.class);
        int a = 0;
        int upA = 0;
        BlockPos mainPos = pos;
        for (int y = 0; y < 2; y++) {
            mainPos = mainPos.offset(EnumFacing.UP, y);
            if (worldIn.isAirBlock(mainPos)) break;
            for (int i = 0; i < 2; i++) {
                EnumFacing facing = EnumFacing.byHorizontalIndex(i);
                BlockPos facePos = mainPos.offset(facing);
                IBlockState faceState = worldIn.getBlockState(facePos);
                if (faceState.getBlock().canBeConnectedTo(worldIn, facePos, facing.getOpposite()) || canConnectTo(worldIn, facePos, facing)) {
                    if (y == 0) {
                        connections.put(facing, Connection.LOW);
                        a ^= 1 << i;
                        a |= 4;
                    }
                    else if (connections.get(facing) == Connection.LOW) {
                        connections.put(facing, Connection.TALL);
                        upA ^= 1 << i;
                        upA |= 4;
                    }
                }
                facePos = mainPos.offset(facing.getOpposite());
                faceState = worldIn.getBlockState(facePos);
                if (faceState.getBlock().canBeConnectedTo(worldIn, facePos, facing) || canConnectTo(worldIn, facePos, facing)) {
                    if (y == 0) {
                        connections.put(facing.getOpposite(), Connection.LOW);
                        a ^= 1 << i;
                        a |= 4;
                    }
                    else if (connections.get(facing.getOpposite()) == Connection.LOW) {
                        connections.put(facing.getOpposite(), Connection.TALL);
                        upA ^= 1 << i;
                        upA |= 4;
                    }
                }
            }
        }
        BlockPos upPos = pos.up();
        if (!worldIn.isAirBlock(upPos)) {
            IBlockState s = worldIn.getBlockState(upPos);
            BlockFaceShape shape = s.getBlockFaceShape(worldIn, upPos, EnumFacing.DOWN);
            if (shape == BlockFaceShape.SOLID) {
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    Connection connection = connections.get(facing);
                    if (connection == Connection.LOW) connections.put(facing, Connection.TALL);
                }
            }
            else if (s.getBlock() instanceof NewWall) {
                s = s.getActualState(worldIn, upPos);
                up = s.getValue(BlockWall.UP);
            }
            else if (!canConnectTo(worldIn, upPos, EnumFacing.NORTH)) {
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    Connection connection = connections.get(facing);
                    if (connection == Connection.TALL) connections.put(facing, Connection.LOW);
                }
                if (shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_SMALL || shape == BlockFaceShape.CENTER_BIG) {
                    up = true;
                }
            }
        }
        if (!up) {
            up = a == 0 || (a & 1) == 1 || (a & 2) == 2;
            if (!up && !worldIn.isAirBlock(pos.up())) {
                IBlockState s = worldIn.getBlockState(pos.up());
                BlockFaceShape shape = s.getBlockFaceShape(worldIn, pos.up(), EnumFacing.DOWN);
                up = shape != BlockFaceShape.SOLID && (upA == 0 || (upA & 1) == 1 || (upA & 2) == 2);
            }
        }
        IBlockState actualState = state.withProperty(BlockWall.UP, up);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            actualState = actualState.withProperty(Objects.requireNonNull(getConnection(facing)), connections.getOrDefault(facing, Connection.NONE));
        }
        return actualState;
    }

    @SideOnly(Side.CLIENT)
    @Override
    default ModelBlockDefinition getModelDefinition(IResourceManager manager, BlockModelShapes shapes, Reader reader, ResourceLocation location, Gson gson) {
        File file = new File(".cache/blockstates", location.getNamespace() + "/block/" + location.getPath() + ".json");
        if (file.exists()) {
            try {
                return ModelBlockDefinition.parseFromReader(new StringReader(new String(Files.readAllBytes(file.toPath()))), location);
            } catch (IOException e) {
                System.err.println("Could not read model json from " + file.getPath());
                e.printStackTrace(System.err);
            }
        }
        Block block = (Block) this;
        JsonObject object = gson.fromJson(reader, JsonObject.class);
        JsonObject o;
        if (object.has("forge_marker")) {
            o = parseForgeBlockState(object);
        }
        else {
            JsonArray multipart = object.getAsJsonArray("multipart");
            String textureName = null;
            try {
                for (JsonElement e : multipart) {
                    JsonObject o2 = e.getAsJsonObject();
                    JsonObject apply = o2.getAsJsonObject("apply");
                    if (apply.has("model")) {
                        String model = apply.get("model").getAsString();
                        ResourceLocation loc = new ResourceLocation(model);
                        loc = new ResourceLocation(loc.getNamespace(), "models/block/" + loc.getPath() + ".json");
                        IResource res = manager.getResource(loc);
                        InputStream is = res.getInputStream();
                        byte[] bytes = IOUtils.toByteArray(is);
                        is.close();
                        JsonObject modelJson = gson.fromJson(new String(bytes), JsonObject.class);
                        textureName = modelJson.getAsJsonObject("textures").get("wall").getAsString();
                        break;
                    }
                }
            }
            catch (IOException e) {
                System.err.println("Oh nooooo");
                e.printStackTrace(System.err);
                return ModelBlockDefinition.parseFromReader(reader, location);
            }
            o = new JsonObject();
            o.addProperty("forge_marker", 1);
            {
                JsonObject defaults = new JsonObject();
                JsonObject textures = new JsonObject();
                textures.addProperty("wall", textureName);
                defaults.add("textures", textures);
                o.add("defaults", defaults);
            }
            JsonObject variants = new JsonObject();
            BlockStateMapper mapper = shapes.getBlockStateMapper();
            ModelResourceLocation l = mapper.getVariants(block).get(block.getDefaultState());
            String[] a;
            {
                String[] split = l.getVariant().split("[=,]");
                a = new String[split.length / 2];
                for (int i = 0; i < split.length / 2; i++) {
                    a[i] = split[i * 2];
                }
            }
            for (String s : a) {
                if (!s.equals("up") && !s.equals("north") && !s.equals("south") && !s.equals("west") && !s.equals("east")) {
                    IProperty<?> property = block.getBlockState().getProperty(s);
                    if (property != null) {
                        JsonObject variant = new JsonObject();
                        property.getAllowedValues().forEach(v -> {
                            variant.add(v instanceof IStringSerializable ? ((IStringSerializable) v).getName() : v.toString(), new JsonObject());
                        });
                        variants.add(property.getName(), variant);
                    }
                    else System.err.println("Property " + s + " is null!");
                }
            }
            String tallModel = "backportium:template_wall_side_tall";
            if (location.getNamespace().equals("betternether")) tallModel = "wall_side";
            {
                JsonObject up = new JsonObject();
                JsonObject upTrue = new JsonObject();
                upTrue.addProperty("submodel", "wall_post");
                up.add("true", upTrue);
                up.add("false", new JsonObject());
                variants.add("up", up);
            }
            {
                JsonObject north = new JsonObject();
                north.add("none", new JsonObject());
                JsonObject low = new JsonObject();
                low.addProperty("submodel", "wall_side");
                low.addProperty("uvlock", true);
                north.add("low", low);
                JsonObject tall = new JsonObject();
                tall.addProperty("submodel", tallModel);
                tall.addProperty("uvlock", true);
                north.add("tall", tall);
                variants.add("north", north);
            }
            {
                JsonObject east = new JsonObject();
                east.add("none", new JsonObject());
                JsonObject low = new JsonObject();
                low.addProperty("submodel", "wall_side");
                low.addProperty("uvlock", true);
                low.addProperty("y", 90);
                east.add("low", low);
                JsonObject tall = new JsonObject();
                tall.addProperty("submodel", tallModel);
                tall.addProperty("uvlock", true);
                tall.addProperty("y", 90);
                east.add("tall", tall);
                variants.add("east", east);
            }
            {
                JsonObject south = new JsonObject();
                south.add("none", new JsonObject());
                JsonObject low = new JsonObject();
                low.addProperty("submodel", "wall_side");
                low.addProperty("uvlock", true);
                low.addProperty("y", 180);
                south.add("low", low);
                JsonObject tall = new JsonObject();
                tall.addProperty("submodel", tallModel);
                tall.addProperty("uvlock", true);
                tall.addProperty("y", 180);
                south.add("tall", tall);
                variants.add("south", south);
            }
            {
                JsonObject west = new JsonObject();
                west.add("none", new JsonObject());
                JsonObject low = new JsonObject();
                low.addProperty("submodel", "wall_side");
                low.addProperty("uvlock", true);
                low.addProperty("y", 270);
                west.add("low", low);
                JsonObject tall = new JsonObject();
                tall.addProperty("submodel", tallModel);
                tall.addProperty("uvlock", true);
                tall.addProperty("y", 270);
                west.add("tall", tall);
                variants.add("west", west);
            }
            o.add("variants", variants);
        }
        String str = gson.toJson(o);
        try {
            file.getParentFile().mkdirs();
            Files.write(file.toPath(), Collections.singletonList(str));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ModelBlockDefinition.parseFromReader(new StringReader(str), location);
    }

    @SideOnly(Side.CLIENT)
    static JsonObject parseForgeBlockState(JsonObject model) {
        JsonObject variants = model.getAsJsonObject("variants");
        String[] aaaa = new String[] { "north", "east", "south", "west" };
        int i = 0;
        for (String variant : aaaa) {
            JsonObject face = variants.getAsJsonObject(variant);
            JsonElement trueFace = face.get("true");
            JsonElement falseFace = face.get("false");
            face.add("low", trueFace);
            face.add("none", falseFace);
            face.remove("true");
            face.remove("false");
            JsonObject tall = new JsonObject();
            tall.addProperty("submodel", "backportium:template_wall_side_tall");
            tall.addProperty("y", i * 90);
            tall.addProperty("uvlock", true);
            face.add("tall", tall);
            i++;
        }
        return model;
    }

    static PropertyEnum<Connection> getConnection(EnumFacing facing) {
        switch (facing) {
            case NORTH: return NORTH_NEW;
            case SOUTH: return SOUTH_NEW;
            case WEST: return WEST_NEW;
            case EAST: return EAST_NEW;
        }
        return null;
    }
}
