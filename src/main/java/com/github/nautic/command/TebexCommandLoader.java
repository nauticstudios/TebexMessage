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

            PluginCommand tbxm = createCommand("tbxm", plugin);
            if (tbxm != null) {
                tbxm.setExecutor(new TebexConsoleCommand(plugin));
                commandMap.register(plugin.getName(), tbxm);
                plugin.getLogger().info("Registered console command: /tbxm");
            }

            PluginCommand tebexmessage = createCommand("tebexmessage", plugin);
            if (tebexmessage != null) {
                tebexmessage.setExecutor(new TebexConsoleCommand(plugin));
                commandMap.register(plugin.getName(), tebexmessage);
                plugin.getLogger().info("Registered console command: /tebexmessage");
            }

            PluginCommand tbmsg = createCommand("tbmsg", plugin);
            if (tbmsg != null) {
                tbmsg.setExecutor(new TebexConsoleCommand(plugin));
                commandMap.register(plugin.getName(), tbmsg);
                plugin.getLogger().info("Registered console command: /tbmsg");
            }

            PluginCommand tb = createCommand("tb", plugin);
            if (tb != null) {
                tb.setExecutor(new TebexConsoleCommand(plugin));
                commandMap.register(plugin.getName(), tb);
                plugin.getLogger().info("Registered console command: /tb");
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register console commands");
            e.printStackTrace();
        }
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