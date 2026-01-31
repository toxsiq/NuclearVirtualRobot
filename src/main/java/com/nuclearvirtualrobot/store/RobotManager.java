package com.nuclearvirtualrobot.store;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RobotManager {
    public static final int BASE_DELAY_SECONDS = 60;
    public static final int MIN_DELAY_SECONDS = 10;
    public static final int DELAY_STEP_SECONDS = 5;
    public static final double BASE_GENERATION = 1000.0;
    public static final double MAX_GENERATION = 5000.0;
    public static final double GENERATION_STEP = 100.0;

    private final Map<UUID, Map<RobotType, Map<RobotEconomy, RobotState>>> data = new HashMap<>();

    public RobotState getState(UUID uuid, RobotType type, RobotEconomy economy) {
        return data
                .computeIfAbsent(uuid, key -> new EnumMap<>(RobotType.class))
                .computeIfAbsent(type, key -> new EnumMap<>(RobotEconomy.class))
                .computeIfAbsent(economy, key -> new RobotState(0, BASE_DELAY_SECONDS, BASE_GENERATION, System.currentTimeMillis()));
    }

    public void addRobots(UUID uuid, RobotType type, RobotEconomy economy, int amount) {
        RobotState state = getState(uuid, type, economy);
        state.addAmount(amount);
    }

    public boolean upgradeDelay(UUID uuid, RobotType type, RobotEconomy economy) {
        RobotState state = getState(uuid, type, economy);
        if (state.getDelaySeconds() <= MIN_DELAY_SECONDS) {
            return false;
        }
        state.setDelaySeconds(state.getDelaySeconds() - DELAY_STEP_SECONDS);
        return true;
    }

    public boolean upgradeGeneration(UUID uuid, RobotType type, RobotEconomy economy) {
        RobotState state = getState(uuid, type, economy);
        if (state.getBaseGeneration() >= MAX_GENERATION) {
            return false;
        }
        state.setBaseGeneration(Math.min(MAX_GENERATION, state.getBaseGeneration() + GENERATION_STEP));
        return true;
    }

    public double getPending(UUID uuid, RobotType type, RobotEconomy economy) {
        RobotState state = getState(uuid, type, economy);
        if (state.getAmount() <= 0) {
            return 0.0;
        }
        long now = System.currentTimeMillis();
        long elapsedMillis = now - state.getLastCollectMillis();
        if (elapsedMillis < 0) {
            state.setLastCollectMillis(now);
            return 0.0;
        }
        long cycles = elapsedMillis / (state.getDelaySeconds() * 1000L);
        if (cycles <= 0) {
            return 0.0;
        }
        double perRobot = state.getBaseGeneration() * type.getMultiplier();
        return cycles * perRobot * state.getAmount();
    }

    public double collect(UUID uuid, RobotType type, RobotEconomy economy) {
        RobotState state = getState(uuid, type, economy);
        if (state.getAmount() <= 0) {
            return 0.0;
        }
        long now = System.currentTimeMillis();
        long elapsedMillis = now - state.getLastCollectMillis();
        if (elapsedMillis < 0) {
            state.setLastCollectMillis(now);
            return 0.0;
        }
        long cycleMillis = state.getDelaySeconds() * 1000L;
        long cycles = elapsedMillis / cycleMillis;
        if (cycles <= 0) {
            return 0.0;
        }
        double perRobot = state.getBaseGeneration() * type.getMultiplier();
        double total = cycles * perRobot * state.getAmount();
        state.setLastCollectMillis(state.getLastCollectMillis() + cycles * cycleMillis);
        return total;
    }
}
