package com.mrpotato;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimplifiedCommands extends JavaPlugin {
    
    private MainMenu mainMenu;
    private GameruleMenu gameruleMenu;

    @Override
    public void onEnable() {
        
        GameruleCommands ruleCmd = new GameruleCommands();
        
        getCommand("size").setExecutor(new SizeCommand());
        getCommand("health").setExecutor(new HealthCommand());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("god").setExecutor(new GodCommand());
        getCommand("rule").setExecutor(ruleCmd);

        CommandAutoCompleter completer = new CommandAutoCompleter();
        getCommand("size").setTabCompleter(completer);
        getCommand("health").setTabCompleter(completer);
        getCommand("speed").setTabCompleter(completer);
        getCommand("heal").setTabCompleter(completer);
        getCommand("god").setTabCompleter(completer);
        getCommand("rule").setTabCompleter(ruleCmd);
        
        new UpdateChecker(this);
        
        
        this.mainMenu = new MainMenu(this);
        this.gameruleMenu = new GameruleMenu(this);

        getCommand("scgui").setExecutor(new SCGuiCommand(this));

        Bukkit.getPluginManager().registerEvents(new SCGuiListener(this), this);
    }
    
    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public GameruleMenu getGameruleMenu() {
        return gameruleMenu;
    }

    static class SizeCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 2 || args.length > 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /size <set|reset> <value> <player>");
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
            if (args.length < 2 || args.length > 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /health <set|reset> <value> <player>");
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

    static class SpeedCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 2 || args.length > 3 || !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /speed <walk|fly> <value> [player]");
                return true;
            }

            String type = args[0];
            float value;
            try {
                value = Float.parseFloat(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid speed value.");
                return true;
            }

            Player target = args.length == 3 ? Bukkit.getPlayer(args[2]) : (Player) sender;
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            if (type.equalsIgnoreCase("walk")) {
                target.setWalkSpeed(value);
                sender.sendMessage(ChatColor.GREEN + "Set walk speed of " + target.getName() + " to " + value);
            } else if (type.equalsIgnoreCase("fly")) {
                target.setFlySpeed(value);
                sender.sendMessage(ChatColor.GREEN + "Set fly speed of " + target.getName() + " to " + value);
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid type. Use walk or fly.");
            }
            return true;
        }
    }

    static class HealCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length > 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /heal [player]");
                return true;
            }

            Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : (sender instanceof Player ? (Player) sender : null);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            target.setFireTicks(0);
            target.setFoodLevel(20);
            sender.sendMessage(ChatColor.GREEN + "Healed " + target.getName());
            return true;
        }
    }

    static class GodCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 1 || args.length > 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /god <on|off> [player]");
                return true;
            }

            boolean enable = args[0].equalsIgnoreCase("on");
            if (!enable && !args[0].equalsIgnoreCase("off")) {
                sender.sendMessage(ChatColor.RED + "Use on or off.");
                return true;
            }

            Player target = args.length == 2 ? Bukkit.getPlayer(args[1]) : (sender instanceof Player ? (Player) sender : null);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            target.setInvulnerable(enable);
            sender.sendMessage(ChatColor.GREEN + "Set god mode " + (enable ? "on" : "off") + " for " + target.getName());
            return true;
        }
    }

    static class CommandAutoCompleter implements TabCompleter {
        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            String cmd = command.getName().toLowerCase();
            List<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

            switch (cmd) {
                case "size":
                case "health":
                    if (args.length == 1) return Arrays.asList("set", "reset");
                    if (args.length == 2 && args[0].equalsIgnoreCase("reset")) return players;
                    if (args.length == 3 && args[0].equalsIgnoreCase("set")) return players;
                    break;

                case "speed":
                    if (args.length == 1) return Arrays.asList("walk", "fly");
                    if (args.length == 3) return players;
                    break;

                case "god":
                    if (args.length == 1) return Arrays.asList("on", "off");
                    if (args.length == 2) return players;
                    break;

                case "heal":
                    if (args.length == 1) return players;
                    break;
            }

            return Collections.emptyList();
        }
    }
}
