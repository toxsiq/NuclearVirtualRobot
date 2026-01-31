package com.nuclearvirtualrobot.service;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import com.nuclearvirtualrobot.store.RobotManager;
import com.nuclearvirtualrobot.store.RobotState;
import com.nuclearvirtualrobot.util.RobotActivatorItem;
import org.bukkit.entity.Player;

public class RobotService {
    private final RobotManager manager;
    private final RobotActivatorItem activatorItem;
    private final EconomyAdapter economyAdapter;

    public RobotService(RobotManager manager, RobotActivatorItem activatorItem, EconomyAdapter economyAdapter) {
        this.manager = manager;
        this.activatorItem = activatorItem;
        this.economyAdapter = economyAdapter;
    }

    public RobotManager getManager() {
        return manager;
    }

    public RobotActivatorItem getActivatorItem() {
        return activatorItem;
    }

    public EconomyAdapter getEconomyAdapter() {
        return economyAdapter;
    }

    public void activateRobots(Player player, RobotType type, RobotEconomy economy, int amount) {
        manager.addRobots(player.getUniqueId(), type, economy, amount);
    }

    public RobotState getState(Player player, RobotType type, RobotEconomy economy) {
        return manager.getState(player.getUniqueId(), type, economy);
    }
}
