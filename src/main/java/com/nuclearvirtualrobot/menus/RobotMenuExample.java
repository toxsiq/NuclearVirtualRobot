package com.nuclearvirtualrobot.menus;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import com.nuclearvirtualrobot.util.CustomHeadFactory;
import com.nuclearvirtualrobot.util.RobotHeadTextures;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class RobotMenuExample {
    private RobotMenuExample() {
    }

    public static void openMainMenu(Player player) {
        RobotMenuHolder holder = new RobotMenuHolder(RobotMenuType.MAIN, null);
        Inventory inventory = Bukkit.createInventory(holder, 27, Component.text("Robôs", NamedTextColor.GRAY));

        inventory.setItem(11, createTypeItem(RobotType.SIMPLES));
        inventory.setItem(15, createTypeItem(RobotType.SUPREMO));

        player.openInventory(inventory);
    }

    public static void openEconomyMenu(Player player, RobotType type) {
        RobotMenuHolder holder = new RobotMenuHolder(RobotMenuType.ECONOMY, type);
        Inventory inventory = Bukkit.createInventory(holder, 27, Component.text("Robôs " + type.getDisplayName(), NamedTextColor.GRAY));

        inventory.setItem(11, createEconomyItem(RobotEconomy.TOKENS));
        inventory.setItem(13, createEconomyItem(RobotEconomy.TOXINA));
        inventory.setItem(15, createEconomyItem(RobotEconomy.CASH));

        player.openInventory(inventory);
    }

    private static ItemStack createTypeItem(RobotType type) {
        ItemStack item = new ItemStack(Material.COMPARATOR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Robô ", NamedTextColor.GRAY)
                    .append(type.displayComponent()));
            meta.lore(List.of(Component.text("Clique para escolher.", NamedTextColor.DARK_GRAY)));
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
                Component.text("Robô de ", NamedTextColor.GRAY)
                        .append(economy.highlightComponent()),
                List.of(Component.text("Clique para escolher.", NamedTextColor.DARK_GRAY))
        );
    }
}
