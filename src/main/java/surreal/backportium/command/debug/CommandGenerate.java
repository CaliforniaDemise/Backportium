package surreal.backportium.command.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A command for generating world generators
 **/
// TODO Expand it a bit more. There's no way to generate WorldGenerators with constructors that have parameters and there's no way to set position
// Should make it a subcommand, add way to define x, y, z and some hardcoding to make stuffs like Ice Spike and Fire work without needing to care about surface blocks and such.
public class CommandGenerate extends CommandBase {

    @Nonnull
    @Override
    public String getName() {
        return "generate";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "generate <WorldGenerator>";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        BlockPos pos = sender.getPosition();

        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient amount of arguments"));
        }

        String name = args[0];

        System.out.println(name);

        try {
            Class<?> cls = Class.forName(name);
            if (WorldGenerator.class.isAssignableFrom(cls)) {
                WorldGenerator generator = (WorldGenerator) cls.getConstructor().newInstance();
                generator.generate(world, world.rand, pos);
            }
            else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + name + " is not assignable to WorldGenerator"));
            }
        }
        catch (Exception e) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Could not find the class"));
        }
    }
}
