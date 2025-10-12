package surreal.backportium._internal.core;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import surreal.backportium._internal.bytecode.traverse.ClassBytes;
import surreal.backportium._internal.bytecode.traverse.ClassTraverser;
import surreal.backportium._internal.core.visitor.*;

import java.util.function.Function;

import static surreal.backportium._internal.bytecode.asm.Transformer.*;

public final class TransformerV13 {

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getVisitor(String name, String transformedName, byte[] bytes) {
        Function<ClassVisitor, ClassVisitor> function = null;
        function = mix(function, visitBubbleColumn(name, transformedName, bytes));
        function = mix(function, visitAirBar(name, transformedName, bytes));
        function = mix(function, visitItemBuoyancy(name, transformedName, bytes));
        function = mix(function, visitHugeMushroom(name, transformedName, bytes));
        function = mix(function, visitNewPotions(name, transformedName, bytes));
        function = mix(function, visitNewButton(name, transformedName, bytes));
        function = mix(function, visitLogging(name, transformedName, bytes));
        function = mix(function, visitBiomeName(name, transformedName, bytes));
        function = mix(function, visitWaterColor(name, transformedName, bytes));
        function = mix(function, visitTrident(name, transformedName, bytes));
        function = mix(function, visitSwimming(name, transformedName, bytes));
        function = mix(function, visitCamera(name, transformedName, bytes));
        function = mix(function, visitUncarvedPumpkin(name, transformedName, bytes));

        // Fixes
        switch (transformedName) {
            // Banner: Fix missing breaking sound
            case "net.minecraft.item.ItemBanner": function = mix(function, ItemBannerVisitor::new); break;
        }
        return function;
    }

    // Bubble Column: Implement
    private static Function<ClassVisitor, ClassVisitor> visitBubbleColumn(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.block.BlockSoulSand": return cv -> new BubbleColumnVisitor.BlockColumnPlacerVisitor(cv, true);
            case "net.minecraft.block.BlockMagma": return cv -> new BubbleColumnVisitor.BlockColumnPlacerVisitor(cv, false);
            case "net.minecraft.entity.Entity": return BubbleColumnVisitor.EntityVisitor::new;
            case "net.minecraft.client.entity.EntityPlayerSP": return BubbleColumnVisitor.EntityPlayerSPVisitor::new;
            case "net.minecraft.entity.projectile.EntityThrowable": return BubbleColumnVisitor.EntityThrowableVisitor::new;
            case "net.minecraft.entity.item.EntityBoat": return BubbleColumnVisitor.EntityBoatVisitor::new;
            case "net.minecraft.client.renderer.entity.RenderBoat": return BubbleColumnVisitor.RenderBoatVisitor::new;
        }
        return null;
    }

    private static Function<ClassVisitor, ClassVisitor> visitAirBar(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.entity.EntityLivingBase": return AirBarVisitor.EntityLivingBaseVisitor::new;
            case "net.minecraftforge.client.GuiIngameForge": return AirBarVisitor.GuiIngameForgeVisitor::new;
        }
        return null;
    }

    private static Function<ClassVisitor, ClassVisitor> visitItemBuoyancy(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.entity.item.EntityItem")) return BuoyancyVisitor.EntityItemVisitor::new;
        return null;
    }

    // Huge Mushroom: Change drops and pick block to 1.13 ones.
    private static Function<ClassVisitor, ClassVisitor> visitHugeMushroom(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.block.BlockHugeMushroom")) return BlockHugeMushroomVisitor::new;
        return null;
    }

    // 1.13 Potions: Implement
    private static Function<ClassVisitor, ClassVisitor> visitNewPotions(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.entity.EntityLivingBase": return PotionVisitor.EntityLivingBaseVisitor::new;
            case "net.minecraft.entity.player.EntityPlayer": return PotionVisitor.EntityPlayerVisitor::new;
            case "net.minecraft.client.renderer.EntityRenderer": return PotionVisitor.EntityRendererVisitor::new;
            case "net.minecraft.block.Block": return PotionVisitor.BlockVisitor::new;
        }
        return null;
    }

    // Logging: Implement (water)logging
    private static Function<ClassVisitor, ClassVisitor> visitLogging(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.world.chunk.Chunk": return LoggingVisitor.ChunkVisitor::new;
            case "net.minecraft.world.chunk.storage.AnvilChunkLoader": return LoggingVisitor.AnvilChunkLoaderVisitor::new;
            case "net.minecraft.network.play.server.SPacketChunkData": return LoggingVisitor.SPacketChunkDataVisitor::new;
            case "net.minecraft.client.network.NetHandlerPlayClient": return LoggingVisitor.NetHandlerPlayClientVisitor::new;
            case "net.minecraft.world.IBlockAccess": return LoggingVisitor.IBlockAccessVisitor::new;
            case "net.minecraft.world.World": return LoggingVisitor.WorldVisitor::new;
            case "net.minecraft.world.ChunkCache": return LoggingVisitor.ChunkCacheVisitor::new;
            case "net.minecraft.client.renderer.chunk.RenderChunk": return LoggingVisitor.RenderChunkVisitor::new;
            case "net.minecraft.client.renderer.BlockFluidRenderer": return LoggingVisitor.BlockFluidRendererVisitor::new;
            case "net.minecraft.block.BlockLiquid": return LoggingVisitor.BlockLiquidVisitor::new;
            case "net.minecraft.block.BlockDynamicLiquid": return LoggingVisitor.BlockDynamicLiquidVisitor::new;
            case "net.minecraft.block.BlockStaticLiquid": return LoggingVisitor.BlockStaticLiquidVisitor::new;
            case "net.minecraft.world.WorldServer": return LoggingVisitor.WorldServerVisitor::new;
            case "net.minecraft.network.play.server.SPacketBlockChange": return LoggingVisitor.SPacketBlockChangeVisitor::new;
            case "net.minecraft.world.WorldEntitySpawner": return LoggingVisitor.WorldEntitySpawnerVisitor::new;
            case "net.minecraft.entity.Entity": return LoggingVisitor.EntityVisitor::new;
            case "net.minecraft.client.renderer.ActiveRenderInfo": return LoggingVisitor.ActiveRenderInfoVisitor::new;
            case "net.minecraft.client.renderer.EntityRenderer": return LoggingVisitor.EntityRendererVisitor::new;
            case "net.minecraftforge.common.ForgeHooks": return LoggingVisitor.ForgeHooks::new;
        }
        return null;
    }

    // Button: Change buttons to work like 1.13
    private static Function<ClassVisitor, ClassVisitor> visitNewButton(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.block.Block")) return NewButtonVisitor.BlockVisitor::new;
        if (transformedName.equals("net.minecraft.block.BlockButton")) return NewButtonVisitor.BlockButtonVisitor::new;
        else {
            int[] constantTable = ClassBytes.getConstantJumpTable(bytes);
            if (ClassTraverser.get().isSuper(bytes, constantTable, "net/minecraft/block/BlockButton")) return NewButtonVisitor.BlockButtonChildVisitor::new;
        }
        return null;
    }

    // Biome Name: Make biome names translatable
    private static Function<ClassVisitor, ClassVisitor> visitBiomeName(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.world.biome.Biome": return BiomeNameVisitor.BiomeVisitor::new;
            case "net.minecraftforge.registries.GameData": return BiomeNameVisitor.GameDataVisitor::new;
            case "net.minecraft.client.gui.GuiOverlayDebug": return BiomeNameVisitor.GuiOverlayDebug::new;
            case "org.cyclops.cyclopscore.config.configurable.ConfigurableBiome": return BiomeNameVisitor.ConfigurableBiomeVisitor::new;
        }
        return null;
    }

    // Water Color: Make biome water colors more modifiable
    private static Function<ClassVisitor, ClassVisitor> visitWaterColor(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.world.biome.Biome": return WaterColorVisitor.BiomeVisitor::new;
            case "net.minecraft.world.biome.BiomeColorHelper$3": return WaterColorVisitor.BiomeColorHelper$WaterColorVisitor::new;
            case "net.minecraft.client.renderer.BlockFluidRenderer": return WaterColorVisitor.BlockFluidRendererVisitor::new;
            case "net.minecraft.client.renderer.ItemRenderer": return WaterColorVisitor.ItemRendererVisitor::new;
            case "net.minecraft.client.renderer.EntityRenderer": return WaterColorVisitor.EntityRendererVisitor::new;
            case "net.minecraft.block.BlockLiquid": return WaterColorVisitor.BlockLiquidVisitor::new;
            case "net.minecraftforge.registries.GameData": return WaterColorVisitor.GameDataVisitor::new;
        }
        return null;
    }

    // Trident: Implement
    private static Function<ClassVisitor, ClassVisitor> visitTrident(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.entity.EntityLivingBase": return TridentVisitor.EntityLivingBaseVisitor::new;
            case "net.minecraft.client.renderer.entity.RenderLivingBase": return TridentVisitor.RenderLivingBaseVisitor::new;
            case "net.minecraft.client.renderer.entity.RenderPlayer": return TridentVisitor.RenderPlayerVisitor::new;
        }
        return null;
    }

    // Swimming: Implement
    private static Function<ClassVisitor, ClassVisitor> visitSwimming(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.entity.EntityLivingBase": return SwimmingVisitor.EntityLivingBaseVisitor::new;
            case "net.minecraft.entity.player.EntityPlayer": return SwimmingVisitor.EntityPlayerVisitor::new;
            case "net.minecraft.client.model.ModelBiped": return SwimmingVisitor.ModelBipedVisitor::new;
            case "net.minecraft.client.renderer.entity.RenderPlayer": return SwimmingVisitor.RenderPlayerVisitor::new;
            case "net.minecraft.client.renderer.EntityRenderer": return SwimmingVisitor.EntityRendererVisitor::new;
        }
        return null;
    }

    // Camera: Interpolate movement
    private static Function<ClassVisitor, ClassVisitor> visitCamera(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer")) {
            return CameraVisitor.EntityRendererVisitor::new;
        }
        return null;
    }

    // Uncarved Pumpkin: Replace vanilla Pumpkin with Uncarved Pumpkin
    private static Function<ClassVisitor, ClassVisitor> visitUncarvedPumpkin(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.block.BlockFence": return cv -> new UncarvedPumpkinVisitor.BlockFenceLikeVisitor(cv, "func_194142_e");
            case "net.minecraft.block.BlockWall": return cv -> new UncarvedPumpkinVisitor.BlockFenceLikeVisitor(cv, "func_194143_e");
            case "net.minecraft.block.BlockPane": return cv -> new UncarvedPumpkinVisitor.BlockFenceLikeVisitor(cv, "func_193394_e");
            case "net.minecraft.block.BlockStem": return UncarvedPumpkinVisitor.BlockStemVisitor::new;
            case "net.minecraft.stats.StatList": return UncarvedPumpkinVisitor.StatListVisitor::new;
            case "net.minecraft.world.gen.feature.WorldGenPumpkin": return UncarvedPumpkinVisitor.WorldGenPumpkinVisitor::new;
        }
        return null;
    }

    private TransformerV13() {}
}
