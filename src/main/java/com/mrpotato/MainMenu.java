package com.mrpotato;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainMenu implements Listener {

    private final SimplifiedCommands plugin;

    public MainMenu(SimplifiedCommands plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "Simplified Commands");

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Gamerule Menu");
        book.setItemMeta(meta);

        inv.setItem(4, book);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Simplified Commands")) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);

        if (event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) {
            plugin.getGameruleMenu().open((Player) event.getWhoClicked());
        }
    }
}
