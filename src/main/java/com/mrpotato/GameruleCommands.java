package com.mrpotato;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.GameRule;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class GameruleCommands implements CommandExecutor, TabCompleter {

    private static final Map<String, GameRule<?>> RULE_MAP = new HashMap<>();
    static {
        RULE_MAP.put("announce", GameRule.ANNOUNCE_ADVANCEMENTS);
        RULE_MAP.put("blockdecay", GameRule.BLOCK_EXPLOSION_DROP_DECAY);
        RULE_MAP.put("cmdoutput", GameRule.COMMAND_BLOCK_OUTPUT);
        RULE_MAP.put("cmdmodlimit", GameRule.COMMAND_MODIFICATION_BLOCK_LIMIT);
        RULE_MAP.put("disableelytracheck", GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK);
        RULE_MAP.put("disableraids", GameRule.DISABLE_RAIDS);
        RULE_MAP.put("daylightcycle", GameRule.DO_DAYLIGHT_CYCLE);
        RULE_MAP.put("entitydrops", GameRule.DO_ENTITY_DROPS);
        RULE_MAP.put("firetick", GameRule.DO_FIRE_TICK);
        RULE_MAP.put("insomnia", GameRule.DO_INSOMNIA);
        RULE_MAP.put("immediaterespawn", GameRule.DO_IMMEDIATE_RESPAWN);
        RULE_MAP.put("limitedcrafting", GameRule.DO_LIMITED_CRAFTING);
        RULE_MAP.put("mobloot", GameRule.DO_MOB_LOOT);
        RULE_MAP.put("mobspawning", GameRule.DO_MOB_SPAWNING);
        RULE_MAP.put("patrolspawning", GameRule.DO_PATROL_SPAWNING);
        RULE_MAP.put("tiledrops", GameRule.DO_TILE_DROPS);
        RULE_MAP.put("traderspawning", GameRule.DO_TRADER_SPAWNING);
        RULE_MAP.put("vinesspread", GameRule.DO_VINES_SPREAD);
        RULE_MAP.put("weathercycle", GameRule.DO_WEATHER_CYCLE);
        RULE_MAP.put("wardenspawning", GameRule.DO_WARDEN_SPAWNING);
        RULE_MAP.put("drowningdamage", GameRule.DROWNING_DAMAGE);
        RULE_MAP.put("pearlsvanish", GameRule.ENDER_PEARLS_VANISH_ON_DEATH);
        RULE_MAP.put("falldamage", GameRule.FALL_DAMAGE);
        RULE_MAP.put("firedamage", GameRule.FIRE_DAMAGE);
        RULE_MAP.put("forgiveplayers", GameRule.FORGIVE_DEAD_PLAYERS);
        RULE_MAP.put("freezedamage", GameRule.FREEZE_DAMAGE);
        RULE_MAP.put("keepinventory", GameRule.KEEP_INVENTORY);
        RULE_MAP.put("lavaconversion", GameRule.LAVA_SOURCE_CONVERSION);
        RULE_MAP.put("logadmin", GameRule.LOG_ADMIN_COMMANDS);
        RULE_MAP.put("maxcmdchain", GameRule.MAX_COMMAND_CHAIN_LENGTH);
        RULE_MAP.put("entitycramming", GameRule.MAX_ENTITY_CRAMMING);
        RULE_MAP.put("mobgriefing", GameRule.MOB_GRIEFING);
        RULE_MAP.put("naturalregen", GameRule.NATURAL_REGENERATION);
        RULE_MAP.put("sleepperecent", GameRule.PLAYERS_SLEEPING_PERCENTAGE);
        RULE_MAP.put("tickspeed", GameRule.RANDOM_TICK_SPEED);
        RULE_MAP.put("reduceddebug", GameRule.REDUCED_DEBUG_INFO);
        RULE_MAP.put("cmdfeedback", GameRule.SEND_COMMAND_FEEDBACK);
        RULE_MAP.put("deathmessages", GameRule.SHOW_DEATH_MESSAGES);
        RULE_MAP.put("spectatorchunks", GameRule.SPECTATORS_GENERATE_CHUNKS);
        RULE_MAP.put("spawnradius", GameRule.SPAWN_RADIUS);
        RULE_MAP.put("universalanger", GameRule.UNIVERSAL_ANGER);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /" + label + " <rule> <value> [world]");
            sender.sendMessage("§eDeprecated command planned removal in version 2.1");
            return true;
        }

        String inputRule = args[0];
        String pluginRule = inputRule.toLowerCase();
        GameRule<?> rule = RULE_MAP.get(pluginRule);

        if (rule == null) {
            sender.sendMessage("§cUnknown rule: " + inputRule);
            sender.sendMessage("§eDeprecated command planned removal in version 2.1");
            return true;
        }

        World targetWorld;
        if (args.length >= 3) {
            targetWorld = Bukkit.getWorld(args[2]);
            if (targetWorld == null) {
                sender.sendMessage("§cWorld not found: " + args[2]);
                sender.sendMessage("§eDeprecated command planned removal in version 2.1");
                return true;
            }
        } else {
            if (sender instanceof Player) {
                targetWorld = ((Player) sender).getWorld();
            } else {
                sender.sendMessage("§cConsole must specify a world.");
                sender.sendMessage("§eDeprecated command planned removal in version 2.1");
                return true;
            }
        }

        if (rule.getType().equals(Integer.class)) {
            try {
                int intVal = Integer.parseInt(args[1]);
                targetWorld.setGameRule((GameRule<Integer>) rule, intVal);
                sender.sendMessage("§aSet " + inputRule + " to " + intVal + " in world " + targetWorld.getName());
            } catch (NumberFormatException e) {
                sender.sendMessage("§cValue must be an integer for " + inputRule);
            }
            return true;
        }

        if (rule.getType().equals(Boolean.class)) {
            String value = args[1].toLowerCase();
            if (!value.equals("true") && !value.equals("false")) {
                sender.sendMessage("§cValue must be true or false for " + inputRule);
                sender.sendMessage("§eDeprecated command planned removal in version 2.1");
                return true;
            }
            boolean boolVal = Boolean.parseBoolean(value);
            targetWorld.setGameRule((GameRule<Boolean>) rule, boolVal);
            sender.sendMessage("§aSet " + inputRule + " to " + boolVal + " in world " + targetWorld.getName());
            sender.sendMessage("§eDeprecated command planned removal in version 2.1");
            return true;
        }

        sender.sendMessage("§cUnsupported rule type for: " + inputRule);
        sender.sendMessage("§eDeprecated command planned removal in version 2.1");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return partialMatch(args[0], RULE_MAP.keySet());
        }

        if (args.length == 2) {
            String pluginRule = args[0].toLowerCase();
            GameRule<?> rule = RULE_MAP.get(pluginRule);

            if (rule == null) return Collections.emptyList();

            if (rule.getType().equals(Boolean.class)) {
                return partialMatch(args[1], Arrays.asList("true", "false"));
            }

            if (rule.getType().equals(Integer.class)) {
                List<String> intSuggestions = new ArrayList<>();
                for (int i = 0; i <= 100; i += (i < 10 ? 1 : i < 50 ? 5 : 10)) {
                    intSuggestions.add(String.valueOf(i));
                }
                return partialMatch(args[1], intSuggestions);
            }
        }

        if (args.length == 3) {
            List<String> worldNames = new ArrayList<>();
            for (World w : Bukkit.getWorlds()) {
                worldNames.add(w.getName());
            }
            return partialMatch(args[2], worldNames);
        }

        return Collections.emptyList();
    }

    private List<String> partialMatch(String arg, Collection<String> possibilities) {
        List<String> result = new ArrayList<>();
        for (String s : possibilities) {
            if (s.toLowerCase().startsWith(arg.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }
}
