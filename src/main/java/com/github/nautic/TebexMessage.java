package com.github.nautic;

import com.github.nautic.command.TebexCommandLoader;
import com.github.nautic.utils.YamlFile;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class TebexMessage extends JavaPlugin {

    private YamlFile configFile;

    @Override
    public void onEnable() {
        loadAll();
        TebexCommandLoader.register(this);

        int pluginId = 29336;
        new Metrics(this, pluginId);

        getLogger().info("[TebexMessage] Plugin enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("[TebexMessage] Plugin disabled successfully.");
    }

    public void loadAll() {
        saveDefaultConfig();
        reloadConfig();
        this.configFile = new YamlFile(this, "config.yml");
    }

    public void reloadAll() {
        loadAll();
    }

    public YamlFile getConfigFile() {
        return configFile;
    }
}