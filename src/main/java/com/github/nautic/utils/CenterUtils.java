package com.github.nautic.utils;

public class CenterUtils {

    private static final int CENTER_PX = 154;

    public static String centerMessage(String message) {
        return centerMessage(message, true, false);
    }

    public static String centerMessage(String message, boolean applyColors, boolean onlyCompensate) {
        if (message == null || message.trim().isEmpty()) return "";

        boolean containsTags = message.contains("<center>") && message.contains("</center>");

        String content = containsTags ? message.replaceAll("(?i)<\\/center>", "").replaceAll("(?i)<center>", "") : message;

        if (applyColors) {
            content = addColor.Set(content);
        }

        String visibleText = stripMiniMessageTags(content);

        int messagePxSize = calculatePixelLength(visibleText);
        int paddingSize = CENTER_PX - (messagePxSize / 2);

        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int spaces = Math.max(0, paddingSize / spaceLength);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            sb.append(" ");
        }

        return onlyCompensate ? sb.toString() : sb.append(content).toString();
    }

    private static int calculatePixelLength(String text) {
        int length = 0;
        boolean isBold = false;
        boolean colorCode = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '§' || c == '&') {
                colorCode = true;
                continue;
            }

            if (colorCode) {
                colorCode = false;
                isBold = (c == 'l' || c == 'L');
                continue;
            }

            DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
            int charWidth = dFI != null
                    ? (isBold ? dFI.getBoldLength() : dFI.getLength())
                    : estimateCharWidth(c);
            length += charWidth + 1;
        }

        return length;
    }

    private static String stripMiniMessageTags(String message) {
        return message.replaceAll("<[^>]+>", "")
                .replaceAll("&#[a-fA-F0-9]{6}", "")
                .replaceAll("&[a-fA-F0-9k-orK-OR]", "")
                .replaceAll("§[a-fA-F0-9k-orK-OR]", "");
    }

    private static int estimateCharWidth(char c) {
        int codePoint = c;
        if (codePoint >= 0x1F300 && codePoint <= 0x1FAFF) {
            return 10;
        }
        if (!Character.isLetterOrDigit(c)) {
            return 5;
        }
        return 4;
    }
}
