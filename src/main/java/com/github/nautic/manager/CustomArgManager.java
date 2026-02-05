package com.github.nautic.manager;

import com.github.nautic.utils.addColor;
import org.bukkit.entity.Player;

import java.util.*;

public class CustomArgManager {

    public static Map<String, String> parse(String[] args) {
        Map<String, String> map = new HashMap<>();

        for (String raw : args) {
            int open = raw.indexOf('(');
            int close = raw.lastIndexOf(')');

            if (open == -1 || close == -1) continue;

            String key = raw.substring(0, open).toLowerCase();
            String value = raw.substring(open + 1, close);

            map.put(key, value);
        }
        return map;
    }

    public Map<String, String> apply(Player player, Map<String, String> raw) {
        Map<String, String> out = new HashMap<>();

        raw.forEach((k, v) -> out.put(k, addColor.SetPlaceholders(player, v)));
        return out;
    }
}
