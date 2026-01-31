package com.nuclearvirtualrobot.menus;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import com.nuclearvirtualrobot.util.RobotActivatorItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

        if (!(event.getClick() == ClickType.SHIFT_RIGHT || (event.isShiftClick() && event.isRightClick()))) {
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

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof RobotMenuHolder holder)) {
            return;
        }

        if (event.getRawSlot() >= inventory.getSize()) {
            return;
        }

        event.setCancelled(true);
        ItemStack current = event.getCurrentItem();
        if (current == null || current.getType().isAir()) {
            return;
        }

        if (holder.getMenuType() == RobotMenuType.MAIN) {
            if (event.getSlot() == 11) {
                RobotMenuExample.openEconomyMenu(player, RobotType.SIMPLES);
            } else if (event.getSlot() == 15) {
                RobotMenuExample.openEconomyMenu(player, RobotType.SUPREMO);
            }
            return;
        }

        if (holder.getMenuType() == RobotMenuType.ECONOMY) {
            RobotType type = holder.getRobotType();
            if (type == null) {
                return;
            }
            if (event.getSlot() == 22) {
                RobotMenuExample.openMainMenu(player);
            } else if (event.getSlot() == 11) {
                RobotMenuExample.openActionsMenu(player, type, RobotEconomy.TOKENS);
            } else if (event.getSlot() == 13) {
                RobotMenuExample.openActionsMenu(player, type, RobotEconomy.TOXINA);
            } else if (event.getSlot() == 15) {
                RobotMenuExample.openActionsMenu(player, type, RobotEconomy.CASH);
            }
            return;
        }

        if (holder.getMenuType() == RobotMenuType.ACTIONS) {
            RobotType type = holder.getRobotType();
            RobotEconomy economy = holder.getEconomy();
            if (type == null || economy == null) {
                return;
            }
            if (event.getSlot() == 22) {
                RobotMenuExample.openEconomyMenu(player, type);
            } else if (event.getSlot() == 11) {
                player.sendMessage(Component.text("Produção recolhida para ", NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(economy.highlightComponent())
                        .append(Component.text(".", NamedTextColor.GREEN)
                                .decoration(TextDecoration.ITALIC, false)));
                player.closeInventory();
            } else if (event.getSlot() == 13) {
                RobotMenuExample.openInfoMenu(player, type, economy);
            } else if (event.getSlot() == 15) {
                RobotMenuExample.openUpgradeMenu(player, type, economy);
            }
            return;
        }

        if (holder.getMenuType() == RobotMenuType.INFO) {
            if (event.getSlot() == 22 && holder.getRobotType() != null && holder.getEconomy() != null) {
                RobotMenuExample.openActionsMenu(player, holder.getRobotType(), holder.getEconomy());
            }
            return;
        }

        if (holder.getMenuType() == RobotMenuType.UPGRADES) {
            if (event.getSlot() == 22 && holder.getRobotType() != null && holder.getEconomy() != null) {
                RobotMenuExample.openActionsMenu(player, holder.getRobotType(), holder.getEconomy());
            } else if (event.getSlot() == 11) {
                player.sendMessage(Component.text("Upgrade de delay aplicado (em breve).", NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false));
                player.closeInventory();
            } else if (event.getSlot() == 15) {
                player.sendMessage(Component.text("Upgrade de quantidade aplicado (em breve).", NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false));
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onActivateRobot(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getPlayer().isSneaking()) {
            return;
        }

        ItemStack item = event.getItem();
        if (!activatorItem.isActivator(item)) {
            return;
        }

        Player player = event.getPlayer();
        RobotType type = activatorItem.getType(item);
        RobotEconomy economy = activatorItem.getEconomy(item);
        int amount = activatorItem.getAmount(item);
        if (type == null || economy == null || amount <= 0) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(Component.text("Ativador usado: ", NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(amount, NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false))
                .append(Component.text(" robôs ", NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false))
                .append(economy.highlightComponent())
                .append(Component.text(" (" + type.getDisplayName() + ")", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
        player.getInventory().removeItem(item);
    }
}
