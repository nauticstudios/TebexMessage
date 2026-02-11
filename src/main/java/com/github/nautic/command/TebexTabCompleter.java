package com.github.nautic.command;

import com.github.nautic.TebexMessage;
import com.github.nautic.utils.YamlFile;
import org.bukkit.command.*;

import java.util.*;

public class TebexTabCompleter implements TabCompleter {

    private final TebexMessage plugin;

    public TebexTabCompleter(TebexMessage plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,
                                      Command command,
                                      String alias,
                                      String[] args) {

        YamlFile config = plugin.getConfigFile();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {

            if (hasPerm(sender, "tebexmessage.help"))
                completions.add("help");

            if (hasPerm(sender, "tebexmessage.reload"))
                completions.add("reload");

            if (hasPerm(sender, "tebexmessage.args"))
                completions.add("args");

            if (sender instanceof ConsoleCommandSender) {
                for (String arg : config.getStringList("customs-args")) {
                    completions.add(arg + "(");
                }
            }

            return filter(completions, args[0]);
        }

        if (sender instanceof ConsoleCommandSender) {

            List<String> available = config.getStringList("customs-args");

            String current = args[args.length - 1];

            for (String arg : available) {
                completions.add(arg + "(");
            }

            return filter(completions, current);
        }

        return Collections.emptyList();
    }

    private boolean hasPerm(CommandSender sender, String perm) {
        return sender.hasPermission("tebexmessage.admin")
                || sender.hasPermission(perm);
    }

    private List<String> filter(List<String> list, String current) {

        List<String> filtered = new ArrayList<>();

        for (String s : list) {
            if (s.toLowerCase().startsWith(current.toLowerCase())) {
                filtered.add(s);
            }
        }

        return filtered;
    }
}
