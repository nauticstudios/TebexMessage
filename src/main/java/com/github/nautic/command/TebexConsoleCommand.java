package com.github.nautic.command;

import com.github.nautic.TebexMessage;
import com.github.nautic.discord.DiscordWebhook;
import com.github.nautic.handler.BroadcastHandler;
import com.github.nautic.utils.YamlFile;
import com.github.nautic.utils.addColor;
import org.bukkit.command.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
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

        YamlFile config = plugin.getConfigFile();

        if (args.length == 0) {

            sender.sendMessage("");
            sender.sendMessage(addColor.Set(
                    "     &#3BFF48&lTebexMessage &#CDCDCD| &fVersion: &#38FF35"
            ) + plugin.getDescription().getVersion());
            sender.sendMessage(addColor.Set(
                    "         &fPowered by &#3F92FFNautic Studios"
            ));

            if (sender instanceof ConsoleCommandSender) {
                String usage = buildUsage(config);

                for (String line : config.getStringList("messages.console.usage")) {
                    sender.sendMessage(addColor.Set(
                            line.replace("{command}", label)
                                    .replace("{usage}", usage)
                    ));
                }
            }

            sender.sendMessage("");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {

            if (!hasPerm(sender, "tebexmessage.help")) {
                sendMessage(sender, config.getString("messages.no-permission"));
                return true;
            }

            for (String line : config.getStringList("messages.help")) {
                sender.sendMessage(addColor.Set(
                        line.replace("{command}", label)
                ));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            if (!hasPerm(sender, "tebexmessage.reload")) {
                sendMessage(sender, config.getString("messages.no-permission"));
                return true;
            }

            plugin.reloadAll();
            sendMessage(sender, config.getString("messages.reload"));
            return true;
        }

        if (args[0].equalsIgnoreCase("args")) {

            if (!hasPerm(sender, "tebexmessage.args")) {
                sendMessage(sender, config.getString("messages.no-permission"));
                return true;
            }

            List<String> list = config.getStringList("customs-args");

            for (String line : config.getStringList("messages.args.header")) {
                sender.sendMessage(addColor.Set(line));
            }

            if (list.isEmpty()) {
                sendMessage(sender, config.getString("messages.args.empty"));
            } else {
                String format = config.getString("messages.args.format");
                for (String arg : list) {
                    sender.sendMessage(addColor.Set(
                            format.replace("{arg}", arg)
                    ));
                }
            }

            for (String line : config.getStringList("messages.args.footer")) {
                sender.sendMessage(addColor.Set(line));
            }

            return true;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            sendMessage(sender, config.getString("messages.console-only"));
            return true;
        }

        String joined = String.join(" ", args);

        if (!joined.contains("(") || !joined.contains(")")) {
            sendMessage(sender, config.getString("messages.console.missing-parenthesis"));
            sendAvailableArgs(sender, config);
            return true;
        }

        Matcher matcher = ARG_PATTERN.matcher(joined);
        Map<String, String> parsedArgs = new HashMap<>();

        while (matcher.find()) {
            parsedArgs.put(
                    matcher.group(1).toLowerCase(),
                    matcher.group(2)
            );
        }

        if (parsedArgs.isEmpty()) {
            sendMessage(sender, config.getString("messages.console.invalid-format"));
            sendAvailableArgs(sender, config);
            return true;
        }

        List<String> allowedArgs = config.getStringList("customs-args");

        for (String key : parsedArgs.keySet()) {
            if (!allowedArgs.contains(key)) {
                sendMessage(sender,
                        config.getString("messages.console.unknown-arg")
                                .replace("{arg}", key)
                );
                sendAvailableArgs(sender, config);
                return true;
            }
        }

        for (String required : allowedArgs) {
            if (!parsedArgs.containsKey(required)) {
                sendMessage(sender,
                        config.getString("messages.missing-arg")
                                .replace("{arg}", required)
                );
                sendAvailableArgs(sender, config);
                return true;
            }
        }

        new BroadcastHandler(plugin, config)
                .send(null, parsedArgs);

        CompletableFuture.runAsync(() -> {

            var section = config.getConfigurationSection("discord.webhook");
            if (section == null || !section.getBoolean("enabled")) return;

            String url = section.getString("url");
            if (url == null || url.isEmpty()) return;

            new DiscordWebhook(url)
                    .send(section, parsedArgs);

        });

        return true;
    }

    private void sendAvailableArgs(CommandSender sender, YamlFile config) {

        List<String> list = config.getStringList("customs-args");
        if (list.isEmpty()) return;

        String joined = String.join(", ", list);

        sendMessage(sender,
                config.getString("messages.console.available-args")
                        .replace("{args}", joined)
        );
    }

    private String buildUsage(YamlFile config) {

        List<String> list = config.getStringList("customs-args");
        if (list.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();

        for (String arg : list) {
            builder.append(arg).append("(<value>) ");
        }

        return builder.toString().trim();
    }

    private void sendMessage(CommandSender sender, String message) {
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(addColor.Set(message));
        }
    }

    private boolean hasPerm(CommandSender sender, String perm) {
        return sender.hasPermission("tebexmessage.admin")
                || sender.hasPermission(perm);
    }
}
