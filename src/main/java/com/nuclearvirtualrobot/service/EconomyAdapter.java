package com.nuclearvirtualrobot.service;

import com.nuclearvirtualrobot.model.RobotEconomy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public class EconomyAdapter {
    private final Object economyApi;
    private final Method addBalanceMethod;
    private final Class<?> economyTypeClass;

    public EconomyAdapter() {
        Object api = null;
        Method addBalance = null;
        Class<?> economyType = null;
        try {
            Class<?> apiClass = Class.forName("com.nucleareconomy.EconomyAPI");
            economyType = Class.forName("com.nucleareconomy.EconomyType");
            Object plugin = Bukkit.getPluginManager().getPlugin("NuclearEconomy");
            if (plugin != null) {
                Method getter = plugin.getClass().getMethod("getEconomyAPI");
                api = getter.invoke(plugin);
            }
            if (api != null) {
                addBalance = apiClass.getMethod("addBalance", UUID.class, String.class, economyType, double.class);
            }
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Nao foi possivel carregar NuclearEconomy.", ex);
        }
        this.economyApi = api;
        this.addBalanceMethod = addBalance;
        this.economyTypeClass = economyType;
    }

    public boolean isAvailable() {
        return economyApi != null && addBalanceMethod != null && economyTypeClass != null;
    }

    public boolean addBalance(Player player, RobotEconomy economy, double amount) {
        if (!isAvailable()) {
            return false;
        }
        try {
            Object economyType = Enum.valueOf((Class<Enum>) economyTypeClass, economy.name());
            addBalanceMethod.invoke(economyApi, player.getUniqueId(), player.getName(), economyType, amount);
            return true;
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Falha ao adicionar saldo do NuclearEconomy.", ex);
            return false;
        }
    }
}
