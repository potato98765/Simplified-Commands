package com.mrpotato;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GameruleMenu {

    private final SimplifiedCommands plugin;
    private final List<String> booleanRules = Arrays.asList(
            "doDaylightCycle", "doMobSpawning", "keepInventory", "doFireTick", "doWeatherCycle"
    );
    private final List<String> numericRules = Arrays.asList(
            "randomTickSpeed", "maxEntityCramming"
    );

    private final Map<UUID, String> awaitingInput = new HashMap<>();

    public GameruleMenu(SimplifiedCommands plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "Gamerule Settings");
        World world = Bukkit.getWorlds().get(0);

        for (String rule : booleanRules) {
            String value = world.getGameRuleValue(rule);
            if (value == null) continue;

            boolean enabled = Boolean.parseBoolean(value);
            Material mat = enabled ? Material.GREEN_CONCRETE : Material.RED_CONCRETE;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + rule);
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Vanilla Name: " + rule,
                    ChatColor.YELLOW + "Current Value: " + value
            ));
            item.setItemMeta(meta);

            inv.addItem(item);
        }

        for (String rule : numericRules) {
            String value = world.getGameRuleValue(rule);
            if (value == null) continue;

            ItemStack item = new ItemStack(Material.YELLOW_CONCRETE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + rule);
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Vanilla Name: " + rule,
                    ChatColor.YELLOW + "Current Value: " + value,
                    ChatColor.GOLD + "Click to enter a new number"
            ));
            item.setItemMeta(meta);

            inv.addItem(item);
        }

        player.openInventory(inv);
    }

    public void handleClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Gamerule Settings")) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);

        String rule = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        World world = Bukkit.getWorlds().get(0);
        String value = world.getGameRuleValue(rule);

        if (value == null) return;

        Player player = (Player) event.getWhoClicked();

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            boolean enabled = Boolean.parseBoolean(value);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule " + rule + " " + !enabled);
            open(player);
        } else {
            player.closeInventory();
            awaitingInput.put(player.getUniqueId(), rule);
            player.sendMessage(ChatColor.GREEN + "Enter a new value for " + rule + ":");
        }
    }

    public boolean handleChat(Player player, String message) {
        if (!awaitingInput.containsKey(player.getUniqueId())) return false;

        String rule = awaitingInput.remove(player.getUniqueId());
        try {
            int num = Integer.parseInt(message);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule " + rule + " " + num);
            player.sendMessage(ChatColor.GREEN + "Updated " + rule + " to " + num);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number. Try again.");
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> open(player), 2L);
        return true;
    }
}
