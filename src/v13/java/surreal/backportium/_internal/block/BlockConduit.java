package surreal.backportium._internal.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.Tags;
import surreal.backportium._internal.client.renderer.TextureStitcher;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium.api.block.Loggable;
import surreal.backportium.client.block.render.TileConduitRenderer;
import surreal.backportium.integration.ModList;
import surreal.backportium._internal.tile.TileConduit;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class BlockConduit extends Block implements Loggable, ITileEntityProvider, TileEntityProvider, TextureStitcher, ModelProvider {

    protected static final AxisAlignedBB CONDUIT_AABB;

    public BlockConduit() {
        super(Material.ROCK, MapColor.DIAMOND);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(3.0F).setResistance(3.0F);
        this.setLightLevel(1F);
        this.useNeighborBrightness = true;
    }

    @NotNull
    @Override
    public AxisAlignedBB getBoundingBox(@NotNull IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos) {
        return CONDUIT_AABB;
    }

    @Override
    public boolean isOpaqueCube(@NotNull IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(@NotNull IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(@NotNull IBlockState blockState, @NotNull IBlockAccess blockAccess, @NotNull BlockPos pos, @NotNull EnumFacing side) {
        return true;
    }

    @NotNull
    @Override
    public BlockFaceShape getBlockFaceShape(@NotNull IBlockAccess worldIn, @NotNull IBlockState state, @NotNull BlockPos pos, @NotNull EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @NotNull
    @Override
    public EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @NotNull
    @Override
    public Vec3d getFogColor(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Entity entity, @NotNull Vec3d originalColor, float partialTicks) {
        if (ModList.FLUIDLOGGED) return super.getFogColor(world, pos, state, entity, originalColor, partialTicks);
        BlockPos up = pos.up();
        IBlockState s = world.getBlockState(up);
        return s.getBlock().getFogColor(world, up, s, entity, originalColor, partialTicks);
    }

    @Override
    public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        return new TileConduit();
    }

    public TileConduit getTile(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return (TileConduit) te;
    }

    @Override
    public void registerTileEntity() {
        GameRegistry.registerTileEntity(TileConduit.class, new ResourceLocation(Tags.MOD_ID, "conduit"));
        if (FMLLaunchHandler.side().isClient()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileConduit.class, new TileConduitRenderer());
        }
    }

    static {
        float aPix = 1F / 16;
        float pix5 = aPix * 5;
        float pix11 = 1F - pix5;
        CONDUIT_AABB = new AxisAlignedBB(pix5, pix5, pix5, pix11, pix11, pix11);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void stitchTextures(TextureMap map) {
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/conduit/wind"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/conduit/wind_vertical"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "particle/nautilus"));
    }

    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory"));
    }
}
