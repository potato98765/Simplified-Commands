package com.mrpotato;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker implements Listener {

    private final JavaPlugin plugin;
    private String latestVersion = null;

    private final String modrinthProjectId = "7N0FDy0S";

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        fetchLatestVersion();
    }

    private void fetchLatestVersion() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.modrinth.com/v2/project/" + modrinthProjectId + "/version");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) json.append(line);
                reader.close();

                JsonArray versions = JsonParser.parseString(json.toString()).getAsJsonArray();

                String serverMC = Bukkit.getBukkitVersion().split("-")[0];

                for (JsonElement element : versions) {
                    JsonObject ver = element.getAsJsonObject();
                    JsonArray gameVersions = ver.getAsJsonArray("game_versions");
                    for (JsonElement gameVerElem : gameVersions) {
                        if (gameVerElem.getAsString().equals(serverMC)) {
                            latestVersion = ver.get("version_number").getAsString();
                            return;
                        }
                    }
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("simplifiedcommands.admin") && !player.isOp()) return;
        if (latestVersion == null) return;

        String currentVersion = plugin.getDescription().getVersion();
        
        //if (currentVersion.toLowerCase().contains("dev") || currentVersion.toLowerCase().contains("pre")) return;
        
        if (!currentVersion.equals(latestVersion)) {
            player.sendMessage("§e[SimplifiedCommands] A new version is available for your server version: §b" + latestVersion +
                    "§e! You are running §b" + currentVersion + "§e.");
            player.sendMessage("§eDownload it here: §bhttps://modrinth.com/plugin/simplifiedcommands");
        }
    }
}
