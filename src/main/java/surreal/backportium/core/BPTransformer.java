package surreal.backportium.core;

import net.minecraft.launchwrapper.IClassTransformer;
import surreal.backportium.core.transformers.*;
import surreal.backportium.core.v13.ClassTransformer13;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@SuppressWarnings("unused")
public class BPTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (transformedName.startsWith("surreal.backportium")) return basicClass;
        if (transformedName.startsWith("it.unimi") || transformedName.startsWith("com.google") || transformedName.startsWith("com.ibm") || transformedName.startsWith("com.paulscode")) return basicClass;
        basicClass = ClassTransformer13.transformClass(transformedName, basicClass);
        switch (transformedName) {
            // Trident
            case "net.minecraft.client.model.ModelBiped": return TridentTransformer.transformModelBiped(PlayerMoveTransformer.transformModelBiped(basicClass));
            case "net.minecraft.entity.EntityLivingBase": return PlayerMoveTransformer.transformEntityLivingBase(TridentTransformer.transformEntityLivingBase(basicClass));
            case "net.minecraft.entity.player.EntityPlayer": return PlayerMoveTransformer.transformEntityPlayer(TridentTransformer.transformEntityPlayer(basicClass));
            case "net.minecraft.client.renderer.entity.RenderLivingBase": return TridentTransformer.transformRenderLivingBase(basicClass);
            case "net.minecraft.client.renderer.entity.RenderPlayer": return PlayerMoveTransformer.transformRenderPlayer(TridentTransformer.transformRenderLivingBase(basicClass));
            case "net.minecraft.client.entity.EntityPlayerSP": return BubbleColumnTransformer.transformEntityPlayerSP(TridentTransformer.transformEntityPlayerSP(basicClass));

            // Pumpkin
            case "net.minecraft.block.BlockPane":
            case "net.minecraft.block.BlockWall": return PumpkinTransformer.transformBlockFenceLike(basicClass);
            case "net.minecraft.block.BlockStem": return PumpkinTransformer.transformBlockStem(basicClass);
            case "net.minecraft.stats.StatList": return PumpkinTransformer.transformStatList(basicClass);
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return PumpkinTransformer.transformWorldGenPumpkin(basicClass);

            // Debarking
            case "net.minecraft.client.renderer.block.statemap.BlockStateMapper": return LogTransformer.transformBlockStateMapper(basicClass);
            case "net.minecraftforge.fml.common.eventhandler.ASMEventHandler": return LogTransformer.transformASMEventHandler(basicClass);
            case "net.minecraftforge.registries.IForgeRegistryEntry$Impl": return LogTransformer.transformForgeRegistryEntry$Impl(basicClass);
            case "net.minecraft.block.Block": return OurpleShulkerTransformer.transformBlock(LogTransformer.transformBlock(basicClass));
            case "net.minecraft.item.Item": return OurpleShulkerTransformer.transformItem(LogTransformer.transformItem(basicClass));
            case "net.minecraft.item.ItemBlock": return LogTransformer.transformItemBlock(basicClass);

            // For The Game Players
            case "net.minecraft.block.BlockBed": return IntentionalTransformerDesign.transformBlockBed(basicClass);

            // Biomes

            // Item Entity Buoyancy
            case "net.minecraft.entity.item.EntityItem": return BuoyancyTransformer.transformEntityItem(basicClass);

            // Bubble Column
            case "net.minecraft.block.BlockSoulSand": return BubbleColumnTransformer.transformSoulSand(basicClass);
            case "net.minecraft.block.BlockMagma": return BubbleColumnTransformer.transformBlockMagma(basicClass);
            case "net.minecraft.entity.projectile.EntityThrowable": return BubbleColumnTransformer.transformEntityThrowable(basicClass);
            case "net.minecraft.entity.item.EntityBoat": return BubbleColumnTransformer.transformEntityBoat(basicClass);
            case "net.minecraft.client.renderer.entity.RenderBoat": return BubbleColumnTransformer.transformRenderBoat(basicClass);

            // Purple Shulker Box
            case "net.minecraft.block.BlockShulkerBox": return OurpleShulkerTransformer.transformBlockShulkerBox(basicClass);
            case "net.minecraft.entity.monster.EntityShulker": return OurpleShulkerTransformer.transformEntityShulker(basicClass);
            case "net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer": return OurpleShulkerTransformer.transformTESRShulker(basicClass);
            case "net.minecraft.client.renderer.entity.RenderShulker": return OurpleShulkerTransformer.transformRenderShulker(basicClass);
            case "net.minecraft.client.renderer.entity.RenderShulker$HeadLayer": return OurpleShulkerTransformer.transformRenderShulker$HeadLayer(basicClass);
            case "net.minecraft.init.Blocks": return OurpleShulkerTransformer.transformBlocks(basicClass);
            case "net.minecraft.client.renderer.BlockModelShapes": return OurpleShulkerTransformer.transformBlockModelShapes(basicClass);
            case "net.minecraft.client.renderer.RenderItem": return OurpleShulkerTransformer.transformRenderItem(basicClass);
            case "net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer": return OurpleShulkerTransformer.transformTileEntityItemStackRenderer(basicClass);

            // Iron Chest
            case "cpw.mods.ironchest.common.blocks.shulker.BlockIronShulkerBox": return OurpleShulkerTransformer.transformBlockIronShulkerBox(basicClass);
            case "cpw.mods.ironchest.common.items.shulker.ItemIronShulkerBox": return OurpleShulkerTransformer.transformItemIronShulkerBox(basicClass);
            case "cpw.mods.ironchest.common.core.IronChestBlocks": return OurpleShulkerTransformer.transformIronChestBlocks(basicClass);
            case "cpw.mods.ironchest.common.core.IronChestBlocks$Registration": return OurpleShulkerTransformer.transformIronChestBlocks$Registration(basicClass);
//            case "cpw.mods.ironchest.common.items.shulker.ItemShulkerBoxChanger": return OurpleShulkerTransformer.transformItemShulkerBoxChanger(basicClass);
            case "cpw.mods.ironchest.client.renderer.shulker.TileEntityIronShulkerBoxRenderer": return OurpleShulkerTransformer.transformTileEntityIronShulkerBoxRenderer(basicClass);

            // Random Fixes
            case "net.minecraft.block.BlockGrass":
            case "net.minecraft.block.BlockMycelium": return FixTransformer.transformBlockGrass(basicClass);
            case "net.minecraft.network.NetHandlerPlayServer": return FixTransformer.transformNetHandlerPlayServer(basicClass);

            // Resource Management
        }
        // To Fix: Some AoA and DivineRPG logs are not BlockLogs
        if (!transformedName.startsWith("net.minecraftforge") && !transformedName.endsWith("$Debarked")) {
            boolean bewitchmentCheck = transformedName.equals("com.bewitchment.common.block.util.ModBlockPillar"); // Some mods like Bewitchment likes to create logs without extending BlockLog
            boolean techrebornCheck = transformedName.equals("techreborn.blocks.BlockRubberLog");
            boolean thaumcraftCheck = transformedName.equals("thaumcraft.common.blocks.world.plants.BlockLogsTC");
            String[] toCheck = new String[] { "net/minecraft/block/BlockLog", "com/progwml6/natura/common/block/BlockEnumLog" };
            if (bewitchmentCheck || techrebornCheck || thaumcraftCheck || LogTransformer.checkLogs(basicClass, transformedName, toCheck)) return LogTransformer.transformBlockLogEx(basicClass);
        }
        return basicClass;
    }
}
