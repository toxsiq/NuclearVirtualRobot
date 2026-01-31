package com.nuclearvirtualrobot.menus;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import com.nuclearvirtualrobot.service.RobotService;
import com.nuclearvirtualrobot.store.RobotManager;
import com.nuclearvirtualrobot.store.RobotState;
import com.nuclearvirtualrobot.util.CustomHeadFactory;
import com.nuclearvirtualrobot.util.NumberFormatter;
import com.nuclearvirtualrobot.util.RobotHeadTextures;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class RobotMenuExample {
    private static RobotService service;

    private RobotMenuExample() {
    }

    public static void setService(RobotService robotService) {
        service = robotService;
    }

    public static void openMainMenu(Player player) {
        RobotMenuHolder holder = new RobotMenuHolder(RobotMenuType.MAIN, null, null);
        Inventory inventory = Bukkit.createInventory(holder, 27, title("Robôs"));

        inventory.setItem(11, createTypeItem(RobotType.SIMPLES));
        inventory.setItem(15, createTypeItem(RobotType.SUPREMO));

        player.openInventory(inventory);
    }

    public static void openEconomyMenu(Player player, RobotType type) {
        RobotMenuHolder holder = new RobotMenuHolder(RobotMenuType.ECONOMY, type, null);
        Inventory inventory = Bukkit.createInventory(holder, 27, title("Robôs " + type.getDisplayName()));

        inventory.setItem(11, createEconomyItem(RobotEconomy.TOKENS));
        inventory.setItem(13, createEconomyItem(RobotEconomy.TOXINA));
        inventory.setItem(15, createEconomyItem(RobotEconomy.CASH));
        inventory.setItem(22, createBackItem());

        player.openInventory(inventory);
    }

    public static void openActionsMenu(Player player, RobotType type, RobotEconomy economy) {
        RobotMenuHolder holder = new RobotMenuHolder(RobotMenuType.ACTIONS, type, economy);
        Inventory inventory = Bukkit.createInventory(holder, 27, title("Robô " + economy.getDisplayName()));

        inventory.setItem(11, createCollectItem(player, type, economy));
        inventory.setItem(13, createInfoItem(player, type, economy));
        inventory.setItem(15, createActionItem(Material.ANVIL, "Upgrades", "Melhore o robô."));
        inventory.setItem(22, createBackItem());

        player.openInventory(inventory);
    }

    public static void openUpgradeMenu(Player player, RobotType type, RobotEconomy economy) {
        RobotMenuHolder holder = new RobotMenuHolder(RobotMenuType.UPGRADES, type, economy);
        Inventory inventory = Bukkit.createInventory(holder, 27, title("Upgrades " + economy.getDisplayName()));

        inventory.setItem(11, createDelayUpgradeItem(player, type, economy));
        inventory.setItem(15, createGenerationUpgradeItem(player, type, economy));
        inventory.setItem(22, createBackItem());

        player.openInventory(inventory);
    }

    private static ItemStack createTypeItem(RobotType type) {
        ItemStack item = new ItemStack(Material.COMPARATOR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(text("Robô ", NamedTextColor.GRAY)
                    .append(type.displayComponent()));
            meta.lore(List.of(text("Clique para escolher.", NamedTextColor.DARK_GRAY)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createEconomyItem(RobotEconomy economy) {
        String texture = switch (economy) {
            case TOKENS -> RobotHeadTextures.HEAD_ROBO_TOKENS;
            case TOXINA -> RobotHeadTextures.HEAD_ROBO_TOXINA;
            case CASH -> RobotHeadTextures.HEAD_ROBO_CASH;
        };

        return CustomHeadFactory.createHead(
                texture,
                text("Robô de ", NamedTextColor.GRAY)
                        .append(economy.highlightComponent()),
                List.of(text("Clique para escolher.", NamedTextColor.DARK_GRAY))
        );
    }

    private static ItemStack createActionItem(Material material, String title, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(text(title, NamedTextColor.GRAY));
            meta.lore(List.of(text(description, NamedTextColor.DARK_GRAY)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createInfoItem(Player player, RobotType type, RobotEconomy economy) {
        RobotState state = getState(player, type, economy);
        double generationPerRobot = state.getBaseGeneration() * type.getMultiplier();
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(text("Informações do Robô", NamedTextColor.GRAY));
            meta.lore(List.of(
                    text("Tipo: ", NamedTextColor.GRAY).append(type.displayComponent()),
                    text("Economia: ", NamedTextColor.GRAY).append(economy.highlightComponent()),
                    text("Robôs ativos: " + state.getAmount(), NamedTextColor.GOLD),
                    text("Geração por robô: " + NumberFormatter.format(generationPerRobot), NamedTextColor.GOLD),
                    text("Delay: " + state.getDelaySeconds() + "s", NamedTextColor.GOLD)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createDelayUpgradeItem(Player player, RobotType type, RobotEconomy economy) {
        RobotState state = getState(player, type, economy);
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(text("Reduzir Delay", NamedTextColor.GRAY));
            int current = state.getDelaySeconds();
            int next = Math.max(RobotManager.MIN_DELAY_SECONDS, current - RobotManager.DELAY_STEP_SECONDS);
            String line = current <= RobotManager.MIN_DELAY_SECONDS
                    ? "Delay: " + current + "s (máx)"
                    : "Delay: " + current + "s -> " + next + "s";
            meta.lore(List.of(
                    text(line, NamedTextColor.DARK_GRAY),
                    text("Diminui o tempo entre gerações.", NamedTextColor.DARK_GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createGenerationUpgradeItem(Player player, RobotType type, RobotEconomy economy) {
        RobotState state = getState(player, type, economy);
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(text("Aumentar Geração", NamedTextColor.GRAY));
            double maxGeneration = RobotManager.MAX_GENERATION * type.getMultiplier();
            double current = state.getBaseGeneration() * type.getMultiplier();
            double next = Math.min(maxGeneration, current + RobotManager.GENERATION_STEP * type.getMultiplier());
            String line = current >= maxGeneration
                    ? "Geração Base: " + NumberFormatter.format(current) + " (máx)"
                    : "Geração Base: " + NumberFormatter.format(current) + " -> " + NumberFormatter.format(next);
            meta.lore(List.of(
                    text(line, NamedTextColor.DARK_GRAY),
                    text("Aumenta a produção por ciclo.", NamedTextColor.DARK_GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createBackItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(text("Voltar", NamedTextColor.GRAY));
            meta.lore(List.of(text("Retornar ao menu anterior.", NamedTextColor.DARK_GRAY)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static Component text(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    private static Component title(String text) {
        return Component.text(text, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
    }

    private static ItemStack createCollectItem(Player player, RobotType type, RobotEconomy economy) {
        double pending = getPending(player, type, economy);
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(text("Recolher", NamedTextColor.GRAY));
            meta.lore(List.of(
                    text("Saldo pronto: " + NumberFormatter.format(pending), NamedTextColor.GREEN),
                    text("Recolher a produção do robô.", NamedTextColor.DARK_GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static RobotState getState(Player player, RobotType type, RobotEconomy economy) {
        if (service == null) {
            return new RobotState(0, RobotManager.BASE_DELAY_SECONDS, RobotManager.BASE_GENERATION, System.currentTimeMillis());
        }
        return service.getState(player, type, economy);
    }

    private static double getPending(Player player, RobotType type, RobotEconomy economy) {
        if (service == null) {
            return 0.0;
        }
        return service.getManager().getPending(player.getUniqueId(), type, economy);
    }

}
