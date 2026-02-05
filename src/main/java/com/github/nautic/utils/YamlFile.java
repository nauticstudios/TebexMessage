package com.github.nautic.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class YamlFile extends YamlConfiguration {

    private final JavaPlugin plugin;
    private final File file;
    private final String name;

    public YamlFile(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name.endsWith(".yml") ? name : name + ".yml";
        this.file = new File(plugin.getDataFolder(), this.name);

        saveDefault();
        reload();
    }

    public void reload() {
        try {
            load(file);

            try (InputStreamReader reader = new InputStreamReader(
                    plugin.getResource(name), StandardCharsets.UTF_8)) {

                if (reader != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
                    setDefaults(defConfig);
                }
            }

        } catch (InvalidConfigurationException e) {
            logYamlError(e);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load file: " + name);
            e.printStackTrace();
        }
    }

    public void saveFile() {
        try {
            save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save file: " + name);
            e.printStackTrace();
        }
    }

    private void saveDefault() {
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
    }

    private void logYamlError(InvalidConfigurationException e) {
        plugin.getLogger().severe(" INVALID YAML CONFIGURATION ");
        plugin.getLogger().severe(" File: " + name);
        plugin.getLogger().severe(" Error: " + e.getMessage());
    }
}