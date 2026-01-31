package com.nuclearvirtualrobot;

import com.nuclearvirtualrobot.commands.AdminRobotCommand;
import com.nuclearvirtualrobot.commands.RobotCommand;
import com.nuclearvirtualrobot.api.RobotAPI;
import com.nuclearvirtualrobot.menus.RobotItemListener;
import com.nuclearvirtualrobot.menus.RobotMenuExample;
import com.nuclearvirtualrobot.service.EconomyAdapter;
import com.nuclearvirtualrobot.service.RobotService;
import com.nuclearvirtualrobot.store.RobotManager;
import com.nuclearvirtualrobot.util.RobotActivatorItem;
import org.bukkit.plugin.java.JavaPlugin;

public class RobotPlugin extends JavaPlugin {
    private RobotService robotService;
    private RobotAPI robotAPI;

    @Override
    public void onEnable() {
        RobotActivatorItem activatorItem = new RobotActivatorItem(this);
        RobotManager manager = new RobotManager();
        EconomyAdapter economyAdapter = new EconomyAdapter();
        robotService = new RobotService(manager, activatorItem, economyAdapter);
        RobotMenuExample.setService(robotService);
        robotAPI = new RobotAPI(robotService);

        if (getCommand("robo") != null) {
            getCommand("robo").setExecutor(new RobotCommand());
        }
        if (getCommand("arobo") != null) {
            getCommand("arobo").setExecutor(new AdminRobotCommand(activatorItem));
        }

        getServer().getPluginManager().registerEvents(new RobotItemListener(robotService), this);
    }

    public RobotService getRobotService() {
        return robotService;
    }

    public RobotAPI getRobotAPI() {
        return robotAPI;
    }
}
