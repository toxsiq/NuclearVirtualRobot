package com.nuclearvirtualrobot.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Locale;

public enum RobotType {
    SIMPLES("Simples", NamedTextColor.GRAY, 1.0),
    SUPREMO("Supremo", NamedTextColor.DARK_PURPLE, 100.0);

    private final String displayName;
    private final NamedTextColor color;
    private final double multiplier;

    RobotType(String displayName, NamedTextColor color, double multiplier) {
        this.displayName = displayName;
        this.color = color;
        this.multiplier = multiplier;
    }

    public Component displayComponent() {
        return Component.text(displayName, color)
                .decoration(TextDecoration.ITALIC, false);
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public static RobotType fromInput(String input) {
        return RobotType.valueOf(input.toUpperCase(Locale.ROOT));
    }
}
