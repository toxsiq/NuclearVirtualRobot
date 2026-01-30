package com.nuclearvirtualrobot.menus;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class RobotMenuHolder implements InventoryHolder {
    private final RobotMenuType menuType;
    private final RobotType robotType;
    private final RobotEconomy economy;

    public RobotMenuHolder(RobotMenuType menuType, RobotType robotType, RobotEconomy economy) {
        this.menuType = menuType;
        this.robotType = robotType;
        this.economy = economy;
    }

    public RobotMenuType getMenuType() {
        return menuType;
    }

    public RobotType getRobotType() {
        return robotType;
    }

    public RobotEconomy getEconomy() {
        return economy;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
