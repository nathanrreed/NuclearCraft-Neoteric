package igentuman.nc.handler.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CommandNcPatrons {

    private CommandNcPatrons() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
//        NeoForge.EVENT_BUS.register(CommandNcPatrons.class);
        return Commands.literal("nc_patrons").executes(ctx -> execute(ctx.getSource()));
    }

    public static int execute(CommandSourceStack ctx) {
        ServerPlayer pl = ctx.getPlayer();
        pl.sendSystemMessage(Component.translatable("nc.message.patrons"));
        return 0;
    }
}