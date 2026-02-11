package com.github.nautic.command;

import com.github.nautic.TebexMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public final class TebexCommandLoader {

    public static void register(TebexMessage plugin) {

        try {
            Field commandMapField = Bukkit.getServer()
                    .getClass()
                    .getDeclaredField("commandMap");

            commandMapField.setAccessible(true);

            CommandMap commandMap =
                    (CommandMap) commandMapField.get(Bukkit.getServer());

            TebexConsoleCommand executor = new TebexConsoleCommand(plugin);
            TebexTabCompleter tabCompleter = new TebexTabCompleter(plugin);

            registerCommand(plugin, commandMap, "tbxm", executor, tabCompleter);
            registerCommand(plugin, commandMap, "tebexmessage", executor, tabCompleter);
            registerCommand(plugin, commandMap, "tbmsg", executor, tabCompleter);
            registerCommand(plugin, commandMap, "tb", executor, tabCompleter);

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register console commands");
            e.printStackTrace();
        }
    }

    private static void registerCommand(TebexMessage plugin,
                                        CommandMap commandMap,
                                        String name,
                                        TebexConsoleCommand executor,
                                        TebexTabCompleter tabCompleter) {

        PluginCommand command = createCommand(name, plugin);

        if (command == null) return;

        command.setExecutor(executor);
        command.setTabCompleter(tabCompleter);

        commandMap.register(plugin.getName(), command);

        plugin.getLogger().info("Registered command: /" + name);
    }

    private static PluginCommand createCommand(String name, TebexMessage plugin) {
        try {
            Constructor<PluginCommand> constructor =
                    PluginCommand.class.getDeclaredConstructor(
                            String.class,
                            org.bukkit.plugin.Plugin.class
                    );
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (Exception e) {
            return null;
        }
    }
}
