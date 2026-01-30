package com.nuclearvirtualrobot.menus;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.util.CustomHeadFactory;
import com.nuclearvirtualrobot.util.RobotHeadTextures;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class RobotMenuExample {
    private RobotMenuExample() {
    }

    public static void openExampleMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("Robôs", NamedTextColor.GRAY));

        ItemStack tokensHead = CustomHeadFactory.createHead(
                RobotHeadTextures.HEAD_ROBO_TOKENS,
                Component.text("Robô de ", NamedTextColor.GRAY)
                        .append(RobotEconomy.TOKENS.highlightComponent()),
                List.of(
                        Component.text("Clique para visualizar.", NamedTextColor.DARK_GRAY)
                )
        );

        inventory.setItem(11, tokensHead);
        player.openInventory(inventory);
    }
}
