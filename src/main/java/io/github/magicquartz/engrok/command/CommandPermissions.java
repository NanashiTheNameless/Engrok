package io.github.magicquartz.engrok.command;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public final class CommandPermissions {
    private static final int OP_PERMISSION_LEVEL = 2;

    private CommandPermissions() {
    }

    public static boolean canUseCommands(ServerCommandSource source) {
        MinecraftServer server = source.getServer();

        if (!server.isSingleplayer()) {
            return source.hasPermissionLevel(OP_PERMISSION_LEVEL);
        }

        GameProfile hostProfile = server.getHostProfile();
        return source.isExecutedByPlayer()
                && hostProfile != null
                && hostProfile.getName().equals(source.getName());
    }
}
