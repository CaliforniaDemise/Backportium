package surreal.backportium.block.properties.button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import surreal.backportium._internal.client.renderer.model.ModelBlockDefinitionProvider;
import surreal.backportium.api.annotations.Extension;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;

import static surreal.backportium.block.properties.button.NewButtonProperties.*;

@SuppressWarnings("unused")
@Extension(BlockButton.class)
public interface NewButton extends ModelBlockDefinitionProvider {

    static AxisAlignedBB getBoundingBox_NewButton(AxisAlignedBB aabb, IBlockState state) {
        Face face = state.getValue(FACE);
        if (face != Face.WALL && state.getValue(FACING).getAxis() == EnumFacing.Axis.X) {
            boolean powered = state.getValue(POWERED);
            if (face == Face.CEILING) return powered ? AABB_DOWN_AXIS_X_ON : AABB_DOWN_AXIS_X_OFF;
            if (face == Face.FLOOR) return powered ? AABB_UP_AXIS_X_ON : AABB_UP_AXIS_X_OFF;
        }
        return aabb;
    }

    static IBlockState getStateForPlacement_NewButton(@Nullable IBlockState state, Block block, EnumFacing facing, EntityLivingBase placer) {
        if (state == null) state = block.getDefaultState();
        EnumFacing entityFacing = placer.getHorizontalFacing();
        Face face;
        {
            if (facing == EnumFacing.DOWN) face = Face.CEILING;
            else if (facing == EnumFacing.UP) face = Face.FLOOR;
            else face = Face.WALL;
        }
        return state.withProperty(FACE, face).withProperty(FACING, entityFacing.getOpposite());
    }

    default int getMetaFromState_NewButton(IBlockState state) {
        int powered = state.getValue(POWERED) ? 1 : 0;
        Face face = state.getValue(FACE);
        EnumFacing facing = state.getValue(FACING);
        return ((facing.getHorizontalIndex() << 3) & 0xFF) | ((face.ordinal() << 1) & 0xFF) | powered;
    }

    default IBlockState getStateFromMeta_NewButton(int metadata) {
        boolean powered = (metadata & 1) == 1;
        EnumFacing facing = EnumFacing.byHorizontalIndex((metadata >> 1) & 3);
        Face face = Face.byIndex((metadata >> 3) & 3);
        return ((Block) this).getDefaultState().withProperty(POWERED, powered).withProperty(FACE, face).withProperty(FACING, facing);
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
            o = parseForgeBlockState(object, block, shapes.getBlockStateMapper());
        }
        else {
            String textureName;
            try {
                JsonObject variants = object.getAsJsonObject("variants");
                JsonObject modelDefinition = variants.entrySet().iterator().next().getValue().getAsJsonObject();
                String model = modelDefinition.get("model").getAsString();
                ResourceLocation loc = new ResourceLocation(model);
                loc = new ResourceLocation(loc.getNamespace(), "models/block/" + loc.getPath() + ".json");
                IResource res = manager.getResource(loc);
                InputStream is = res.getInputStream();
                byte[] bytes = IOUtils.toByteArray(is);
                is.close();
                JsonObject modelJson = gson.fromJson(new String(bytes), JsonObject.class);
                textureName = modelJson.getAsJsonObject("textures").get("texture").getAsString();
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
                textures.addProperty("texture", textureName);
                defaults.add("textures", textures);
                defaults.addProperty("model", "button");
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
                if (!s.equals("facing") && !s.equals("powered") && !s.equals("face")) {
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
            {
                JsonObject c_n_p = new JsonObject();
                JsonObject c_s_p = new JsonObject();
                JsonObject c_w_p = new JsonObject();
                JsonObject c_e_p = new JsonObject();
                JsonObject w_n_p = new JsonObject();
                JsonObject w_s_p = new JsonObject();
                JsonObject w_w_p = new JsonObject();
                JsonObject w_e_p = new JsonObject();
                JsonObject f_n_p = new JsonObject();
                JsonObject f_s_p = new JsonObject();
                JsonObject f_w_p = new JsonObject();
                JsonObject f_e_p = new JsonObject();
                JsonObject c_n = new JsonObject();
                JsonObject c_s = new JsonObject();
                JsonObject c_w = new JsonObject();
                JsonObject c_e = new JsonObject();
                JsonObject w_n = new JsonObject();
                JsonObject w_s = new JsonObject();
                JsonObject w_w = new JsonObject();
                JsonObject w_e = new JsonObject();
                JsonArray f_n = new JsonArray();
                JsonObject f_s = new JsonObject();
                JsonObject f_w = new JsonObject();
                JsonObject f_e = new JsonObject();
                c_n_p.addProperty("model", "button_pressed");
                c_n_p.addProperty("x", 180);
                c_n_p.addProperty("y", 180);
                c_s_p.addProperty("model", "button_pressed");
                c_s_p.addProperty("x", 180);
                c_w_p.addProperty("model", "button_pressed");
                c_w_p.addProperty("x", 180);
                c_w_p.addProperty("y", 90);
                c_e_p.addProperty("model", "button_pressed");
                c_e_p.addProperty("x", 180);
                c_e_p.addProperty("y", 270);
                w_n_p.addProperty("model", "button_pressed");
                w_n_p.addProperty("x", 90);
                w_n_p.addProperty("uvlock", true);
                w_s_p.addProperty("model", "button_pressed");
                w_s_p.addProperty("x", 90);
                w_s_p.addProperty("y", 180);
                w_s_p.addProperty("uvlock", true);
                w_w_p.addProperty("model", "button_pressed");
                w_w_p.addProperty("x", 90);
                w_w_p.addProperty("y", 270);
                w_w_p.addProperty("uvlock", true);
                w_e_p.addProperty("model", "button_pressed");
                w_e_p.addProperty("x", 90);
                w_e_p.addProperty("y", 90);
                w_e_p.addProperty("uvlock", true);
                f_n_p.addProperty("model", "button_pressed");
                f_s_p.addProperty("model", "button_pressed");
                f_s_p.addProperty("y", 180);
                f_w_p.addProperty("model", "button_pressed");
                f_w_p.addProperty("y", 270);
                f_e_p.addProperty("model", "button_pressed");
                f_e_p.addProperty("y", 90);
                c_n.addProperty("x", 180);
                c_n.addProperty("y", 180);
                c_s.addProperty("x", 180);
                c_w.addProperty("x", 180);
                c_w.addProperty("y", 90);
                c_e.addProperty("x", 180);
                c_e.addProperty("y", 270);
                w_n.addProperty("x", 90);
                w_n.addProperty("uvlock", true);
                w_s.addProperty("x", 90);
                w_s.addProperty("y", 180);
                w_s.addProperty("uvlock", true);
                w_w.addProperty("x", 90);
                w_w.addProperty("y", 270);
                w_w.addProperty("uvlock", true);
                w_e.addProperty("x", 90);
                w_e.addProperty("y", 90);
                w_e.addProperty("uvlock", true);
                f_n.add(new JsonObject());
                f_s.addProperty("y", 180);
                f_w.addProperty("y", 270);
                f_e.addProperty("y", 90);
                variants.add("face=ceiling,facing=north,powered=true", c_n_p);
                variants.add("face=ceiling,facing=south,powered=true", c_s_p);
                variants.add("face=ceiling,facing=west,powered=true", c_w_p);
                variants.add("face=ceiling,facing=east,powered=true", c_e_p);
                variants.add("face=wall,facing=north,powered=true", w_n_p);
                variants.add("face=wall,facing=south,powered=true", w_s_p);
                variants.add("face=wall,facing=west,powered=true", w_w_p);
                variants.add("face=wall,facing=east,powered=true", w_e_p);
                variants.add("face=floor,facing=north,powered=true", f_n_p);
                variants.add("face=floor,facing=south,powered=true", f_s_p);
                variants.add("face=floor,facing=west,powered=true", f_w_p);
                variants.add("face=floor,facing=east,powered=true", f_e_p);
                variants.add("face=ceiling,facing=north,powered=false", c_n);
                variants.add("face=ceiling,facing=south,powered=false", c_s);
                variants.add("face=ceiling,facing=west,powered=false", c_w);
                variants.add("face=ceiling,facing=east,powered=false", c_e);
                variants.add("face=wall,facing=north,powered=false", w_n);
                variants.add("face=wall,facing=south,powered=false", w_s);
                variants.add("face=wall,facing=west,powered=false", w_w);
                variants.add("face=wall,facing=east,powered=false", w_e);
                variants.add("face=floor,facing=north,powered=false", f_n);
                variants.add("face=floor,facing=south,powered=false", f_s);
                variants.add("face=floor,facing=west,powered=false", f_w);
                variants.add("face=floor,facing=east,powered=false", f_e);
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
    static JsonObject parseForgeBlockState(JsonObject model, Block block, BlockStateMapper mapper) {
        JsonObject variantsNew = new JsonObject();
        { // TODO This is a bit dumb, parse them get blockstates from them to handle it properly.
            JsonObject variants = model.getAsJsonObject("variants");
            model.remove("variants");
            variants.entrySet().forEach(e -> {
                if (!e.getKey().contains("=")) variantsNew.add(e.getKey(), e.getValue());
            });
        }
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
            if (!s.equals("facing") && !s.equals("powered") && !s.equals("face")) {
                IProperty<?> property = block.getBlockState().getProperty(s);
                if (property != null) {
                    JsonObject variant = new JsonObject();
                    property.getAllowedValues().forEach(v -> {
                        variant.add(v instanceof IStringSerializable ? ((IStringSerializable) v).getName() : v.toString(), new JsonObject());
                    });
                    variantsNew.add(property.getName(), variant);
                }
                else System.err.println("Property " + s + " is null!");
            }
        }
        {
            JsonObject c_n_p = new JsonObject();
            JsonObject c_s_p = new JsonObject();
            JsonObject c_w_p = new JsonObject();
            JsonObject c_e_p = new JsonObject();
            JsonObject w_n_p = new JsonObject();
            JsonObject w_s_p = new JsonObject();
            JsonObject w_w_p = new JsonObject();
            JsonObject w_e_p = new JsonObject();
            JsonObject f_n_p = new JsonObject();
            JsonObject f_s_p = new JsonObject();
            JsonObject f_w_p = new JsonObject();
            JsonObject f_e_p = new JsonObject();
            JsonObject c_n = new JsonObject();
            JsonObject c_s = new JsonObject();
            JsonObject c_w = new JsonObject();
            JsonObject c_e = new JsonObject();
            JsonObject w_n = new JsonObject();
            JsonObject w_s = new JsonObject();
            JsonObject w_w = new JsonObject();
            JsonObject w_e = new JsonObject();
            JsonObject f_n = new JsonObject();
            JsonObject f_s = new JsonObject();
            JsonObject f_w = new JsonObject();
            JsonObject f_e = new JsonObject();
            c_n_p.addProperty("x", 180);
            c_n_p.addProperty("model", "button_pressed");
            c_n_p.addProperty("y", 180);
            c_s_p.addProperty("model", "button_pressed");
            c_s_p.addProperty("x", 180);
            c_w_p.addProperty("model", "button_pressed");
            c_w_p.addProperty("x", 180);
            c_w_p.addProperty("y", 90);
            c_e_p.addProperty("model", "button_pressed");
            c_e_p.addProperty("x", 180);
            c_e_p.addProperty("y", 270);
            w_n_p.addProperty("model", "button_pressed");
            w_n_p.addProperty("x", 90);
            w_n_p.addProperty("uvlock", true);
            w_s_p.addProperty("model", "button_pressed");
            w_s_p.addProperty("x", 90);
            w_s_p.addProperty("y", 180);
            w_s_p.addProperty("uvlock", true);
            w_w_p.addProperty("model", "button_pressed");
            w_w_p.addProperty("x", 90);
            w_w_p.addProperty("y", 270);
            w_w_p.addProperty("uvlock", true);
            w_e_p.addProperty("model", "button_pressed");
            w_e_p.addProperty("x", 90);
            w_e_p.addProperty("y", 90);
            w_e_p.addProperty("uvlock", true);
            f_n_p.addProperty("model", "button_pressed");
            f_s_p.addProperty("model", "button_pressed");
            f_s_p.addProperty("y", 180);
            f_w_p.addProperty("model", "button_pressed");
            f_w_p.addProperty("y", 270);
            f_e_p.addProperty("model", "button_pressed");
            f_e_p.addProperty("y", 90);
            c_n.addProperty("model", "button");
            c_n.addProperty("x", 180);
            c_n.addProperty("y", 180);
            c_s.addProperty("model", "button");
            c_s.addProperty("x", 180);
            c_w.addProperty("model", "button");
            c_w.addProperty("x", 180);
            c_w.addProperty("y", 90);
            c_e.addProperty("model", "button");
            c_e.addProperty("x", 180);
            c_e.addProperty("y", 270);
            w_n.addProperty("model", "button");
            w_n.addProperty("x", 90);
            w_n.addProperty("uvlock", true);
            w_s.addProperty("model", "button");
            w_s.addProperty("x", 90);
            w_s.addProperty("y", 180);
            w_s.addProperty("uvlock", true);
            w_w.addProperty("model", "button");
            w_w.addProperty("x", 90);
            w_w.addProperty("y", 270);
            w_w.addProperty("uvlock", true);
            w_e.addProperty("model", "button");
            w_e.addProperty("x", 90);
            w_e.addProperty("y", 90);
            w_e.addProperty("uvlock", true);
            f_n.addProperty("model", "button");
            f_s.addProperty("model", "button");
            f_s.addProperty("y", 180);
            f_w.addProperty("model", "button");
            f_w.addProperty("y", 270);
            f_e.addProperty("model", "button");
            f_e.addProperty("y", 90);
            variantsNew.add("face=ceiling,facing=north,powered=true", c_n_p);
            variantsNew.add("face=ceiling,facing=south,powered=true", c_s_p);
            variantsNew.add("face=ceiling,facing=west,powered=true", c_w_p);
            variantsNew.add("face=ceiling,facing=east,powered=true", c_e_p);
            variantsNew.add("face=wall,facing=north,powered=true", w_n_p);
            variantsNew.add("face=wall,facing=south,powered=true", w_s_p);
            variantsNew.add("face=wall,facing=west,powered=true", w_w_p);
            variantsNew.add("face=wall,facing=east,powered=true", w_e_p);
            variantsNew.add("face=floor,facing=north,powered=true", f_n_p);
            variantsNew.add("face=floor,facing=south,powered=true", f_s_p);
            variantsNew.add("face=floor,facing=west,powered=true", f_w_p);
            variantsNew.add("face=floor,facing=east,powered=true", f_e_p);
            variantsNew.add("face=ceiling,facing=north,powered=false", c_n);
            variantsNew.add("face=ceiling,facing=south,powered=false", c_s);
            variantsNew.add("face=ceiling,facing=west,powered=false", c_w);
            variantsNew.add("face=ceiling,facing=east,powered=false", c_e);
            variantsNew.add("face=wall,facing=north,powered=false", w_n);
            variantsNew.add("face=wall,facing=south,powered=false", w_s);
            variantsNew.add("face=wall,facing=west,powered=false", w_w);
            variantsNew.add("face=wall,facing=east,powered=false", w_e);
            variantsNew.add("face=floor,facing=north,powered=false", f_n);
            variantsNew.add("face=floor,facing=south,powered=false", f_s);
            variantsNew.add("face=floor,facing=west,powered=false", f_w);
            variantsNew.add("face=floor,facing=east,powered=false", f_e);
        }
        model.add("variants", variantsNew);
        return model;
    }
}