package com.nuclearvirtualrobot.menus;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import com.nuclearvirtualrobot.util.RobotActivatorItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RobotItemListener implements Listener {
    private final RobotActivatorItem activatorItem;

    public RobotItemListener(RobotActivatorItem activatorItem) {
        this.activatorItem = activatorItem;
    }

    @EventHandler
    public void onStackActivator(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getClick() != ClickType.SHIFT_RIGHT) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || clickedInventory != player.getInventory()) {
            return;
        }

        ItemStack current = event.getCurrentItem();
        if (!activatorItem.isActivator(current)) {
            return;
        }

        RobotType type = activatorItem.getType(current);
        RobotEconomy economy = activatorItem.getEconomy(current);
        if (type == null || economy == null) {
            return;
        }

        int total = 0;
        List<Integer> slotsToClear = new ArrayList<>();
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (!activatorItem.isActivator(item)) {
                continue;
            }
            if (type == activatorItem.getType(item) && economy == activatorItem.getEconomy(item)) {
                total += activatorItem.getAmount(item);
                slotsToClear.add(i);
            }
        }

        if (total <= 0) {
            return;
        }

        event.setCancelled(true);
        for (Integer slot : slotsToClear) {
            player.getInventory().setItem(slot, null);
        }

        ItemStack stacked = activatorItem.createActivator(type, economy, total);
        player.getInventory().addItem(stacked);
        player.sendMessage(Component.text("Itens combinados em um único ativador.", NamedTextColor.GREEN));
    }
}
