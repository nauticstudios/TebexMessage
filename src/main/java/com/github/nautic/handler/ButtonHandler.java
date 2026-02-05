package com.github.nautic.handler;

import com.github.nautic.utils.addColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.configuration.ConfigurationSection;

public class ButtonHandler {

    private final ConfigurationSection buttons;
    private final int centerSpaces;

    public ButtonHandler(ConfigurationSection root) {
        this.buttons = root.getConfigurationSection("buttons.list");
        this.centerSpaces = root.getInt("buttons.center", 0);
    }

    public TextComponent buildButtons() {

        TextComponent line = new TextComponent();

        if (centerSpaces > 0) {
            line.addExtra(new TextComponent(" ".repeat(centerSpaces)));
        }

        boolean first = true;

        for (String key : buttons.getKeys(false)) {
            ConfigurationSection b = buttons.getConfigurationSection(key);
            if (b == null) continue;

            if (!first) {
                line.addExtra(new TextComponent(" "));
            }
            first = false;

            BaseComponent[] text =
                    TextComponent.fromLegacyText(
                            addColor.Set(b.getString("text"))
                    );

            TextComponent button = new TextComponent(text);

            BaseComponent[] hover =
                    TextComponent.fromLegacyText(
                            addColor.Set(b.getString("hover"))
                    );

            button.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    hover
            ));

            button.setClickEvent(new ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    b.getString("open_url")
            ));

            line.addExtra(button);
        }

        return line;
    }
}