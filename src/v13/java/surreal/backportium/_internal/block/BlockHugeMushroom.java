package surreal.backportium._internal.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.client.renderer.model.ModelBaker;
import surreal.backportium.block.state.HugeMushroomContainer;
import surreal.backportium.init.ModBlocks;

import java.util.Objects;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockHugeMushroom extends net.minecraft.block.BlockHugeMushroom implements ModelBaker {

    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool EAST = PropertyBool.create("east");

    public BlockHugeMushroom(Material material, MapColor color) {
        super(material, color, getSmallBlock(color));
        this.setDefaultState(this.getDefaultState().withProperty(UP, true).withProperty(DOWN, true).withProperty(NORTH, true).withProperty(SOUTH, true).withProperty(WEST, true).withProperty(EAST, true));
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    private static Block getSmallBlock(MapColor color) {
        if (color == MapColor.RED) return Blocks.RED_MUSHROOM;
        return Blocks.BROWN_MUSHROOM;
    }

    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos) {
        IBlockState fromState = worldIn.getBlockState(fromPos);
        if (fromState.getBlock() == this) {
            PropertyBool property = this.getPropertyFromFacing(this.getFaceFromPos(pos, fromPos));
            if (property == null) return;
            worldIn.setBlockState(pos, state.withProperty(property, false));
        }
    }

    protected final EnumFacing getFaceFromPos(BlockPos pos, BlockPos fromPos) {
        if (fromPos.getY() > pos.getY()) return EnumFacing.UP;
        if (fromPos.getY() < pos.getY()) return EnumFacing.DOWN;
        if (fromPos.getX() > pos.getX()) return EnumFacing.EAST;
        if (fromPos.getX() < pos.getX()) return EnumFacing.WEST;
        if (fromPos.getZ() > pos.getZ()) return EnumFacing.SOUTH;
        if (fromPos.getZ() < pos.getZ()) return EnumFacing.NORTH;
        return EnumFacing.UP;
    }

    protected final PropertyBool getPropertyFromFacing(EnumFacing facing) {
        switch (facing) {
            case UP: return UP;
            case DOWN: return DOWN;
            case NORTH: return NORTH;
            case SOUTH: return SOUTH;
            case WEST: return WEST;
            case EAST: return EAST;
        }
        return null;
    }

    @NotNull
    @Override
    public Item getItemDropped(@NotNull IBlockState state, @NotNull Random rand, int fortune) {
        if (this == Blocks.RED_MUSHROOM_BLOCK) return Item.getItemFromBlock(Blocks.RED_MUSHROOM);
        if (this == Blocks.BROWN_MUSHROOM_BLOCK) return Item.getItemFromBlock(Blocks.BROWN_MUSHROOM_BLOCK);
        return Items.AIR;
    }

    @NotNull
    @Override
    public ItemStack getPickBlock(@NotNull IBlockState state, @NotNull RayTraceResult target, @NotNull World world, @NotNull BlockPos pos, @NotNull EntityPlayer player) {
        return new ItemStack(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return Math.max(0, random.nextInt(10) - 7);
    }

    @Override
    public boolean canSilkHarvest(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer player) {
        return true;
    }

    @NotNull
    @Override
    protected ItemStack getSilkTouchDrop(@NotNull IBlockState state) {
        return new ItemStack(this);
    }

    @NotNull
    @Override
    public MapColor getMapColor(IBlockState state, @NotNull IBlockAccess worldIn, @NotNull BlockPos pos) {
        boolean up = state.getValue(UP);
        if (!up) return MapColor.SAND;
        return super.getMapColor(state, worldIn, pos);
    }

    @Override
    public int getMetaFromState(@NotNull IBlockState state) {
        int meta = 0;
        if (!state.getValue(UP)) meta |= 1;
        if (!state.getValue(DOWN)) meta |= 2;
        if (!state.getValue(NORTH)) meta |= 4;
        if (!state.getValue(SOUTH)) meta |= 8;
        if (!state.getValue(WEST)) meta |= 16;
        if (!state.getValue(EAST)) meta |= 32;
        return meta;
    }

    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean up = (meta & 1) == 0;
        boolean down = (meta & 2) == 0;
        boolean north = (meta & 4) == 0;
        boolean south = (meta & 8) == 0;
        boolean west = (meta & 16) == 0;
        boolean east = (meta & 32) == 0;
        return this.getDefaultState().withProperty(UP, up).withProperty(DOWN, down).withProperty(NORTH, north).withProperty(SOUTH, south).withProperty(WEST, west).withProperty(EAST, east);
    }

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        return new HugeMushroomContainer(this);
    }

    @Override
    public void bakeModels(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        IModel model;
        try {
            model = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        final String inside = "blocks/mushroom_block_inside";
        String skin;
        if (this == ModBlocks.BROWN_MUSHROOM_BLOCK) skin = "blocks/mushroom_block_skin_brown";
        else if (this == ModBlocks.RED_MUSHROOM_BLOCK) skin = "blocks/mushroom_block_skin_red";
        else skin = "blocks/mushroom_block_skin_stem";
        this.getBlockState().getValidStates().forEach(state -> {
            ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
            boolean[] h = new boolean[] { state.getValue(UP), state.getValue(DOWN), state.getValue(NORTH), state.getValue(SOUTH), state.getValue(WEST), state.getValue(EAST) };
//            ModelResourceLocation location = new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), String.format("up=%s,down=%s,north=%s,south=%s,west=%s,east=%s", h[0], h[1], h[2], h[3], h[4], h[5]));
            ModelResourceLocation location = new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), String.format("down=%s,east=%s,north=%s,south=%s,up=%s,west=%s", h[1], h[5], h[2], h[3], h[0], h[4]));
            textures.put("up", h[0] ? skin : inside);
            textures.put("down", h[1] ? skin : inside);
            textures.put("north", h[2] ? skin : inside);
            textures.put("south", h[3] ? skin : inside);
            textures.put("west", h[4] ? skin : inside);
            textures.put("east", h[5] ? skin : inside);
            textures.put("particle", skin);
            IModel textured = model.retexture(textures.build());
            registry.putObject(location, textured.bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()));
        });
    }
}
