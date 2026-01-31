package com.nuclearvirtualrobot.store;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class RobotStorage {
    private final JavaPlugin plugin;
    private final File file;

    public RobotStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "robots.yml");
    }

    public void save(RobotManager manager) {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Map<RobotType, Map<RobotEconomy, RobotState>>> entry : manager.getAllStates().entrySet()) {
            UUID uuid = entry.getKey();
            for (Map.Entry<RobotType, Map<RobotEconomy, RobotState>> typeEntry : entry.getValue().entrySet()) {
                for (Map.Entry<RobotEconomy, RobotState> economyEntry : typeEntry.getValue().entrySet()) {
                    String path = uuid + "." + typeEntry.getKey().name() + "." + economyEntry.getKey().name();
                    RobotState state = economyEntry.getValue();
                    config.set(path + ".amount", state.getAmount());
                    config.set(path + ".delay", state.getDelaySeconds());
                    config.set(path + ".base", state.getBaseGeneration());
                    config.set(path + ".lastCollect", state.getLastCollectMillis());
                }
            }
        }
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Falha ao salvar robos.yml", ex);
        }
    }

    public void load(RobotManager manager) {
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String uuidKey : config.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidKey);
            } catch (IllegalArgumentException ex) {
                continue;
            }
            ConfigurationSection typeSection = config.getConfigurationSection(uuidKey);
            if (typeSection == null) {
                continue;
            }
            for (String typeKey : typeSection.getKeys(false)) {
                RobotType type;
                try {
                    type = RobotType.valueOf(typeKey);
                } catch (IllegalArgumentException ex) {
                    continue;
                }
                ConfigurationSection economySection = typeSection.getConfigurationSection(typeKey);
                if (economySection == null) {
                    continue;
                }
                for (String economyKey : economySection.getKeys(false)) {
                    RobotEconomy economy;
                    try {
                        economy = RobotEconomy.valueOf(economyKey);
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }
                    ConfigurationSection stateSection = economySection.getConfigurationSection(economyKey);
                    if (stateSection == null) {
                        continue;
                    }
                    int amount = stateSection.getInt("amount", 0);
                    int delay = stateSection.getInt("delay", RobotManager.BASE_DELAY_SECONDS);
                    double base = stateSection.getDouble("base", RobotManager.BASE_GENERATION);
                    long lastCollect = stateSection.getLong("lastCollect", System.currentTimeMillis());
                    RobotState state = new RobotState(amount, delay, base, lastCollect);
                    manager.setState(uuid, type, economy, state);
                }
            }
        }
    }
}
