package com.github.nautic.update.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SpigotUpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;
    private final boolean folia;

    public SpigotUpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.folia = isFolia();
    }

    public void getVersion(final Consumer<String> consumer) {

        CompletableFuture.runAsync(() -> {

            try (InputStream is = new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId
            ).openStream();
                 Scanner scanner = new Scanner(is)) {

                if (!scanner.hasNext()) return;

                String version = scanner.next();

                runSync(() -> consumer.accept(version));

            } catch (IOException e) {
                plugin.getLogger().warning("Could not check for updates: " + e.getMessage());
            }

        });
    }

    private void runSync(Runnable task) {

        if (!folia) {
            Bukkit.getScheduler().runTask(plugin, task);
            return;
        }

        try {
            Method getScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
            Object scheduler = getScheduler.invoke(null);

            Method runMethod = scheduler.getClass().getMethod(
                    "run",
                    JavaPlugin.class,
                    java.util.function.Consumer.class
            );

            runMethod.invoke(scheduler, plugin, (java.util.function.Consumer<Object>) t -> task.run());

        } catch (Exception e) {
            plugin.getLogger().warning("Folia scheduler failed, running normally.");
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
