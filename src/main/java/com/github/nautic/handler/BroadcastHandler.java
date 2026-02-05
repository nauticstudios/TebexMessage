package com.github.nautic.handler;

import com.github.nautic.TebexMessage;
import com.github.nautic.manager.*;
import com.github.nautic.utils.addColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class BroadcastHandler {

    private final TebexMessage plugin;
    private final ConfigurationSection config;

    public BroadcastHandler(TebexMessage plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void send(Player executor, Map<String, String> rawArgs) {

        CustomArgManager argManager = new CustomArgManager();
        Map<String, String> args = argManager.apply(executor, rawArgs);

        for (String req : config.getStringList("customs-args")) {
            if (!args.containsKey(req)) {
                if (executor != null) {
                    executor.sendMessage("§cMissing argument: " + req);
                }
                return;
            }
        }

        MessageManager msg = new MessageManager();
        ButtonHandler btn = new ButtonHandler(config);

        Collection<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());

        for (String line : config.getStringList("broadcast.message")) {

            if (line.equalsIgnoreCase("<empty>")) {
                targets.forEach(p -> p.sendMessage(""));
                continue;
            }

            if (line.equalsIgnoreCase("%buttons%")) {
                TextComponent buttons = btn.buildButtons();
                targets.forEach(p -> p.spigot().sendMessage(buttons));
                continue;
            }

            String out = line;
            for (Map.Entry<String, String> e : args.entrySet()) {
                out = out.replace("{" + e.getKey() + "}", e.getValue());
            }

            final String finalOut = msg.parse(out);

            targets.forEach(p -> p.sendMessage(finalOut));
        }

        handleTitle(args, targets);
        handleSound(targets);
    }

    private void handleTitle(Map<String, String> args, Collection<Player> targets) {
        ConfigurationSection t = config.getConfigurationSection("broadcast.extra.events.title");
        if (t == null || !t.getBoolean("enabled")) return;

        String up = t.getString("up", "");
        String down = t.getString("down", "");

        for (Map.Entry<String, String> e : args.entrySet()) {
            up = up.replace("{" + e.getKey() + "}", e.getValue());
            down = down.replace("{" + e.getKey() + "}", e.getValue());
        }

        final String titleUp = addColor.Set(up);
        final String titleDown = addColor.Set(down);

        for (Player p : targets) {
            p.sendTitle(titleUp, titleDown, 10, 60, 10);
        }
    }

    private void handleSound(Collection<Player> targets) {
        ConfigurationSection s = config.getConfigurationSection("broadcast.extra.events.sound");
        if (s == null || !s.getBoolean("enabled")) return;

        String raw = s.getString("id");
        if (raw == null) return;

        try {
            Sound sound = Sound.valueOf(raw.toUpperCase());
            targets.forEach(p ->
                    p.playSound(p.getLocation(), sound, 1f, 1f)
            );
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Invalid sound in config.yml: " + raw);
        }
    }
}
