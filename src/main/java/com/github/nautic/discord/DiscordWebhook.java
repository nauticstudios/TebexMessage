package com.github.nautic.discord;

import org.bukkit.configuration.ConfigurationSection;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import java.util.regex.Pattern;

public final class DiscordWebhook {

    private final String url;

    private static final Pattern LEGACY_COLOR =
            Pattern.compile("(?i)[&§][0-9A-FK-ORX]");

    private static final Pattern HEX_COLOR =
            Pattern.compile("(?i)&#[A-F0-9]{6}");

    private static final Pattern MINI_MESSAGE =
            Pattern.compile("<[^>]+>");


    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void send(ConfigurationSection section, Map<String, String> args) {
        if (section == null || !section.getBoolean("enabled")) return;

        try {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = buildPayload(section, args);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            connection.getInputStream().close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildPayload(ConfigurationSection s, Map<String, String> args) {

        StringBuilder json = new StringBuilder();
        json.append("{\"embeds\":[");

        json.append("{");

        append(json, "title", apply(s.getString("title"), args));

        List<String> desc = s.getStringList("description");
        if (!desc.isEmpty()) {
            json.append("\"description\":\"")
                    .append(apply(String.join("\n", desc), args))
                    .append("\",");
        }

        String color = s.getString("color");
        if (color != null && color.startsWith("#")) {
            int decimalColor = Integer.parseInt(color.substring(1), 16);
            json.append("\"color\":").append(decimalColor).append(",");
        }

        List<Map<?, ?>> fields = s.getMapList("fields");
        if (!fields.isEmpty()) {
            json.append("\"fields\":[");
            for (Map<?, ?> f : fields) {

                Object inlineObj = f.get("inline");
                boolean inline = inlineObj instanceof Boolean && (Boolean) inlineObj;

                json.append("{")
                        .append("\"name\":\"").append(apply((String) f.get("name"), args)).append("\",")
                        .append("\"value\":\"").append(apply((String) f.get("value"), args)).append("\",")
                        .append("\"inline\":").append(inline)
                        .append("},");
            }
            trim(json);
            json.append("],");
        }

        String footer = s.getString("footer");
        if (footer != null) {
            json.append("\"footer\":{")
                    .append("\"text\":\"").append(apply(footer, args)).append("\"")
                    .append("},");
        }

        appendObject(json, "image", "url", apply(s.getString("image"), args));

        appendObject(json, "thumbnail", "url", apply(s.getString("thumbnail"), args));

        trim(json);
        json.append("}");

        json.append("]}");
        return json.toString();
    }

    private void append(StringBuilder json, String key, String value) {
        if (value != null) {
            json.append("\"").append(key).append("\":\"")
                    .append(value).append("\",");
        }
    }

    private void appendObject(StringBuilder json, String key, String sub, String value) {
        if (value != null) {
            json.append("\"").append(key).append("\":{")
                    .append("\"").append(sub).append("\":\"").append(value).append("\"")
                    .append("},");
        }
    }

    private void trim(StringBuilder json) {
        int l = json.length();
        if (json.charAt(l - 1) == ',') {
            json.deleteCharAt(l - 1);
        }
    }

    private String stripColors(String text) {
        if (text == null) return null;

        text = MINI_MESSAGE.matcher(text).replaceAll("");

        text = HEX_COLOR.matcher(text).replaceAll("");

        text = LEGACY_COLOR.matcher(text).replaceAll("");

        return text;
    }

    private String apply(String text, Map<String, String> args) {
        if (text == null) return null;

        for (Map.Entry<String, String> e : args.entrySet()) {
            text = text.replace("{" + e.getKey() + "}", e.getValue());
        }

        text = stripColors(text);

        return text.replace("\"", "\\\"");
    }
}