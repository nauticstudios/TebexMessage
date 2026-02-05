package com.github.nautic.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class addColor {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder()
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    public static String Set(String message) {
        if (message == null || message.isEmpty()) return message;

        return TranslateColor(
                TranslateHex(
                        SafeMiniMessage(message)
                )
        );
    }

    public static String SetPlaceholders(Player player, String message) {
        if (player != null && message != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return Set(message);
    }

    public static String TranslateColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String TranslateHex(String message) {
        final char colorChar = ChatColor.COLOR_CHAR;
        final Matcher matcher = HEX_PATTERN.matcher(message);
        final StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            final String hex = matcher.group(1);
            matcher.appendReplacement(buffer,
                    colorChar + "x"
                            + colorChar + hex.charAt(0)
                            + colorChar + hex.charAt(1)
                            + colorChar + hex.charAt(2)
                            + colorChar + hex.charAt(3)
                            + colorChar + hex.charAt(4)
                            + colorChar + hex.charAt(5)
            );
        }

        return matcher.appendTail(buffer).toString();
    }

    public static String SafeMiniMessage(String message) {
        try {
            Component component = MINI_MESSAGE.deserialize(message);
            return LEGACY_SERIALIZER.serialize(component);
        } catch (Exception ignored) {
            return message;
        }
    }
}