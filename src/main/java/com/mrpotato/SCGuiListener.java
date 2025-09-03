package com.mrpotato;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SCGuiListener implements Listener {

    private final SimplifiedCommands plugin;

    public SCGuiListener(SimplifiedCommands plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plugin.getMainMenu().onInventoryClick(event);
        plugin.getGameruleMenu().handleClick(event);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameruleMenu().handleChat(player, event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
