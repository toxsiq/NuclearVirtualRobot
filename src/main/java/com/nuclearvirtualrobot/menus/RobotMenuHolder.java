package com.nuclearvirtualrobot.menus;

import com.nuclearvirtualrobot.model.RobotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class RobotMenuHolder implements InventoryHolder {
    private final RobotMenuType menuType;
    private final RobotType robotType;

    public RobotMenuHolder(RobotMenuType menuType, RobotType robotType) {
        this.menuType = menuType;
        this.robotType = robotType;
    }

    public RobotMenuType getMenuType() {
        return menuType;
    }

    public RobotType getRobotType() {
        return robotType;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
