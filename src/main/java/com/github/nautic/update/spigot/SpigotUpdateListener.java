package com.github.nautic.update.spigot;

import com.github.nautic.utils.addColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotUpdateListener implements Listener {

    private final JavaPlugin plugin;
    private final SpigotUpdateChecker updateChecker;
    private final int resourceId;

    public SpigotUpdateListener(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.updateChecker = new SpigotUpdateChecker(plugin, resourceId);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.isOp() && !player.hasPermission("tebexmessage.admin")) return;

        updateChecker.getVersion(latestVersion -> {
            String currentVersion = plugin.getDescription().getVersion();
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                player.sendMessage(addColor.Set("&r"));
                player.sendMessage(addColor.Set("  &#3BFF48&lTebexMessage &7» &fA new version is available!"));
                player.sendMessage(addColor.Set("  &f[Updated] Your version: &#FF6A6A" + currentVersion + " &f| Latest: &#90FF6A" + latestVersion));
                player.sendMessage(addColor.Set("  &fURL: &#FFBF6Ahttps://www.spigotmc.org/resources/"+ resourceId + "/"));
                player.sendMessage(addColor.Set("&r"));
            }
        });
    }
}