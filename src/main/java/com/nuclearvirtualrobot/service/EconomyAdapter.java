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
        Class<?> apiClass = null;
        try {
            apiClass = Class.forName("com.nucleareconomy.EconomyAPI");
            economyType = Class.forName("com.nucleareconomy.EconomyType");
            Object plugin = Bukkit.getPluginManager().getPlugin("NuclearEconomy");
            if (plugin != null) {
                try {
                    Method getter = plugin.getClass().getMethod("getEconomyAPI");
                    Object result = getter.invoke(plugin);
                    if (apiClass.isInstance(result)) {
                        api = result;
                    }
                } catch (Exception ignored) {
                }
                if (api == null) {
                    api = resolveApi(plugin, apiClass);
                }
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

    private Object resolveApi(Object plugin, Class<?> apiClass) {
        try {
            Method getter = plugin.getClass().getMethod("getEconomyAPI");
            Object result = getter.invoke(plugin);
            if (apiClass.isInstance(result)) {
                return result;
            }
        } catch (Exception ignored) {
        }
        try {
            Method getter = plugin.getClass().getMethod("getApi");
            Object result = getter.invoke(plugin);
            if (apiClass.isInstance(result)) {
                return result;
            }
        } catch (Exception ignored) {
        }
        for (var method : plugin.getClass().getMethods()) {
            if (apiClass.equals(method.getReturnType()) && method.getParameterCount() == 0) {
                try {
                    Object result = method.invoke(plugin);
                    if (apiClass.isInstance(result)) {
                        return result;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        try {
            var field = plugin.getClass().getDeclaredField("economyAPI");
            field.setAccessible(true);
            Object result = field.get(plugin);
            if (apiClass.isInstance(result)) {
                return result;
            }
        } catch (Exception ignored) {
        }
        for (var field : plugin.getClass().getDeclaredFields()) {
            if (apiClass.equals(field.getType())) {
                try {
                    field.setAccessible(true);
                    Object result = field.get(plugin);
                    if (apiClass.isInstance(result)) {
                        return result;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        Bukkit.getLogger().log(Level.WARNING, "Nao foi possivel acessar EconomyAPI no NuclearEconomy.");
        return null;
    }
}
