package com.github.nautic.manager;

import com.github.nautic.utils.CenterUtils;
import com.github.nautic.utils.addColor;

public class MessageManager {

    public String parse(String text) {
        if (text == null || text.isEmpty()) return "";

        if (text.startsWith("<center>") && text.endsWith("</center>")) {
            text = text.replace("<center>", "").replace("</center>", "");
            return CenterUtils.centerMessage(addColor.Set(text));
        }

        return addColor.Set(text);
    }
}