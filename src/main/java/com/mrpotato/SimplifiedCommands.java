package com.mrpotato;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SimplifiedCommands extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("size").setExecutor(new SizeCommand());
        getCommand("health").setExecutor(new HealthCommand());
    }

    static class SizeCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /size <set|reset> <value?> <player?>");
                return true;
            }

            String sub = args[0];
            String valueOrTarget = args[1];
            String target = args.length > 2 ? args[2] : sender.getName();

            if (sub.equalsIgnoreCase("set")) {
                try {
                    float scale = Float.parseFloat(valueOrTarget);
                    if (scale < 0.0f || scale > 120.0f) {
                        sender.sendMessage(ChatColor.RED + "Scale must be between 0 and 120");
                        return true;
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "attribute " + target + " minecraft:generic.scale base set " + scale);

                    sender.sendMessage(ChatColor.GREEN + "Set size of " + target + " to " + scale);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number: " + valueOrTarget);
                }
            } else if (sub.equalsIgnoreCase("reset")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "attribute " + valueOrTarget + " minecraft:generic.scale base set 1");

                sender.sendMessage(ChatColor.GREEN + "Reset size of " + valueOrTarget + " to default");
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + sub);
            }
            return true;
        }
    }

    static class HealthCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /health <set|reset> <value?> <player?>");
                return true;
            }

            String sub = args[0];
            String valueOrTarget = args[1];
            String target = args.length > 2 ? args[2] : sender.getName();

            if (sub.equalsIgnoreCase("set")) {
                try {
                    float amount = Float.parseFloat(valueOrTarget);
                    if (amount <= 0 || amount > 1024) {
                        sender.sendMessage(ChatColor.RED + "Health must be between 1 and 1024.");
                        return true;
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "attribute " + target + " minecraft:generic.max_health base set " + amount);

                    sender.sendMessage(ChatColor.GREEN + "Set health of " + target + " to " + amount);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number: " + valueOrTarget);
                }
            } else if (sub.equalsIgnoreCase("reset")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "attribute " + valueOrTarget + " minecraft:generic.max_health base set 20");

                sender.sendMessage(ChatColor.GREEN + "Reset health of " + valueOrTarget + " to default");
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + sub);
            }
            return true;
        }
    }
}
