package com.nuclearvirtualrobot;

import com.nuclearvirtualrobot.commands.AdminRobotCommand;
import com.nuclearvirtualrobot.commands.RobotCommand;
import com.nuclearvirtualrobot.menus.RobotItemListener;
import com.nuclearvirtualrobot.util.RobotActivatorItem;
import org.bukkit.plugin.java.JavaPlugin;

public class RobotPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        RobotActivatorItem activatorItem = new RobotActivatorItem(this);

        if (getCommand("robo") != null) {
            getCommand("robo").setExecutor(new RobotCommand());
        }
        if (getCommand("arobo") != null) {
            getCommand("arobo").setExecutor(new AdminRobotCommand(activatorItem));
        }

        getServer().getPluginManager().registerEvents(new RobotItemListener(activatorItem), this);
    }
}
