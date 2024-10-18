package org.economy.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandMapFetcher {

    private static CommandMap COMMAND_MAP;
    private static Field KNOWN_COMMANDS;

    static {
        try {
            Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);

            COMMAND_MAP = (CommandMap) commandMap.get(Bukkit.getServer());

            KNOWN_COMMANDS = COMMAND_MAP.getClass().getDeclaredField("knownCommands");
            KNOWN_COMMANDS.setAccessible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getRegisteredCommands(Player player) {
        try {
            Map<String, Command> knownCommands = (Map<String, Command>) KNOWN_COMMANDS.get(COMMAND_MAP);
            List<String> commands = new ArrayList<>();

            for (String command : knownCommands.keySet()) {
                Command cmd = knownCommands.get(command);
                if (cmd != null && player.hasPermission(cmd.getPermission())) {
                    commands.add(command);
                }
            }

            return commands;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static void registerCommand(String name, org.bukkit.command.Command command) {
        COMMAND_MAP.register(name, command);
    }

    public static void registerCommands(Command... cmds) {
        for (Command cmd : cmds) {
            COMMAND_MAP.register(cmd.getName(), cmd);
        }
    }

}
