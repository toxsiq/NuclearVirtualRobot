package com.nuclearvirtualrobot.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Locale;

public enum RobotType {
    SIMPLES("Simples", NamedTextColor.GRAY),
    SUPREMO("Supremo", NamedTextColor.DARK_PURPLE);

    private final String displayName;
    private final NamedTextColor color;

    RobotType(String displayName, NamedTextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public Component displayComponent() {
        return Component.text(displayName, color)
                .decoration(TextDecoration.ITALIC, false);
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RobotType fromInput(String input) {
        return RobotType.valueOf(input.toUpperCase(Locale.ROOT));
    }
}
