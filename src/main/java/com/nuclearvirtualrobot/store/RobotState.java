package com.nuclearvirtualrobot.store;

public class RobotState {
    private int amount;
    private int delaySeconds;
    private double baseGeneration;
    private long lastCollectMillis;

    public RobotState(int amount, int delaySeconds, double baseGeneration, long lastCollectMillis) {
        this.amount = amount;
        this.delaySeconds = delaySeconds;
        this.baseGeneration = baseGeneration;
        this.lastCollectMillis = lastCollectMillis;
    }

    public int getAmount() {
        return amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public double getBaseGeneration() {
        return baseGeneration;
    }

    public void setBaseGeneration(double baseGeneration) {
        this.baseGeneration = baseGeneration;
    }

    public long getLastCollectMillis() {
        return lastCollectMillis;
    }

    public void setLastCollectMillis(long lastCollectMillis) {
        this.lastCollectMillis = lastCollectMillis;
    }
}
