package com.github.nautic.command;

import com.github.nautic.TebexMessage;
import com.github.nautic.discord.DiscordWebhook;
import com.github.nautic.handler.BroadcastHandler;
import com.github.nautic.utils.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.*;
import java.util.regex.*;

public class TebexConsoleCommand implements CommandExecutor {

    private final TebexMessage plugin;

    private static final Pattern ARG_PATTERN =
            Pattern.compile("(\\w+)\\(([^)]*)\\)");

    public TebexConsoleCommand(TebexMessage plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof ConsoleCommandSender)) {
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadAll();
            sender.sendMessage("[TebexMessage] Reload completed successfully.");
            return true;
        }

        String joined = String.join(" ", args);
        Matcher matcher = ARG_PATTERN.matcher(joined);

        Map<String, String> parsedArgs = new HashMap<>();

        while (matcher.find()) {
            parsedArgs.put(
                    matcher.group(1).toLowerCase(),
                    matcher.group(2)
            );
        }

        YamlFile config = plugin.getConfigFile();

        new BroadcastHandler(plugin, config)
                .send(null, parsedArgs);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!config.contains("discord.webhook")) return;

            var section = config.getConfigurationSection("discord.webhook");
            if (section == null || !section.getBoolean("enabled")) return;

            String url = section.getString("url");
            if (url == null || url.isEmpty()) return;

            new DiscordWebhook(url)
                    .send(section, parsedArgs);
        });

        return true;
    }
}