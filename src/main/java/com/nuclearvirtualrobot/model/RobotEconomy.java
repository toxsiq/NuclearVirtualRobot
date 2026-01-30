package com.nuclearvirtualrobot.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Locale;

public enum RobotEconomy {
    TOKENS("Tokens", NamedTextColor.YELLOW),
    TOXINA("Toxina", NamedTextColor.DARK_GREEN),
    CASH("Cash", NamedTextColor.GOLD);

    private final String displayName;
    private final NamedTextColor highlightColor;

    RobotEconomy(String displayName, NamedTextColor highlightColor) {
        this.displayName = displayName;
        this.highlightColor = highlightColor;
    }

    public Component highlightComponent() {
        return Component.text(displayName, highlightColor);
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RobotEconomy fromInput(String input) {
        return RobotEconomy.valueOf(input.toUpperCase(Locale.ROOT));
    }
}
