package com.nuclearvirtualrobot.menus;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import com.nuclearvirtualrobot.service.EconomyAdapter;
import com.nuclearvirtualrobot.service.RobotService;
import com.nuclearvirtualrobot.store.RobotManager;
import com.nuclearvirtualrobot.util.NumberFormatter;
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
    private final RobotService service;
    private final RobotManager manager;
    private final EconomyAdapter economyAdapter;

    public RobotItemListener(RobotService service) {
        this.service = service;
        this.manager = service.getManager();
        this.economyAdapter = service.getEconomyAdapter();
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
        if (!service.getActivatorItem().isActivator(current)) {
            return;
        }

        RobotType type = service.getActivatorItem().getType(current);
        RobotEconomy economy = service.getActivatorItem().getEconomy(current);
        if (type == null || economy == null) {
            return;
        }

        int total = 0;
        List<Integer> slotsToClear = new ArrayList<>();
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (!service.getActivatorItem().isActivator(item)) {
                continue;
            }
            if (type == service.getActivatorItem().getType(item) && economy == service.getActivatorItem().getEconomy(item)) {
                total += service.getActivatorItem().getAmount(item);
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

        ItemStack stacked = service.getActivatorItem().createActivator(type, economy, total);
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
                double total = manager.getPending(player.getUniqueId(), type, economy);
                if (total <= 0.0) {
                    player.sendMessage(Component.text("Nenhum saldo disponível para sacar.", NamedTextColor.RED)
                            .decoration(TextDecoration.ITALIC, false));
                    return;
                }
                if (economyAdapter.addBalance(player, economy, total)) {
                    manager.collect(player.getUniqueId(), type, economy);
                    player.sendMessage(Component.text("Saque realizado: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(NumberFormatter.format(total), NamedTextColor.YELLOW)
                                    .decoration(TextDecoration.ITALIC, false))
                            .append(Component.text(" ", NamedTextColor.GREEN)
                                    .decoration(TextDecoration.ITALIC, false))
                            .append(economy.highlightComponent()));
                } else {
                    player.sendMessage(Component.text("Economia indisponível. Contate a staff.", NamedTextColor.RED)
                            .decoration(TextDecoration.ITALIC, false));
                }
                player.closeInventory();
            } else if (event.getSlot() == 15) {
                RobotMenuExample.openUpgradeMenu(player, type, economy);
            }
            return;
        }

        if (holder.getMenuType() == RobotMenuType.UPGRADES) {
            if (event.getSlot() == 22 && holder.getRobotType() != null && holder.getEconomy() != null) {
                RobotMenuExample.openActionsMenu(player, holder.getRobotType(), holder.getEconomy());
            } else if (event.getSlot() == 11) {
                boolean upgraded = manager.upgradeDelay(player.getUniqueId(), holder.getRobotType(), holder.getEconomy());
                if (upgraded) {
                    int delay = manager.getState(player.getUniqueId(), holder.getRobotType(), holder.getEconomy()).getDelaySeconds();
                    player.sendMessage(Component.text("Upgrade aplicado: Delay agora em " + delay + "s.", NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false));
                    RobotMenuExample.openUpgradeMenu(player, holder.getRobotType(), holder.getEconomy());
                } else {
                    player.sendMessage(Component.text("Delay já está no mínimo.", NamedTextColor.RED)
                            .decoration(TextDecoration.ITALIC, false));
                }
            } else if (event.getSlot() == 15) {
                boolean upgraded = manager.upgradeGeneration(player.getUniqueId(), holder.getRobotType(), holder.getEconomy());
                if (upgraded) {
                    double generation = manager.getState(player.getUniqueId(), holder.getRobotType(), holder.getEconomy()).getBaseGeneration()
                            * holder.getRobotType().getMultiplier();
                    player.sendMessage(Component.text("Upgrade aplicado: Geração base agora em " + formatK(generation) + ".", NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false));
                    RobotMenuExample.openUpgradeMenu(player, holder.getRobotType(), holder.getEconomy());
                } else {
                    player.sendMessage(Component.text("Geração já está no máximo.", NamedTextColor.RED)
                            .decoration(TextDecoration.ITALIC, false));
                }
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
            if (service.getActivatorItem().isActivator(event.getItem())) {
                event.setCancelled(true);
            }
            return;
        }

        ItemStack item = event.getItem();
        if (!service.getActivatorItem().isActivator(item)) {
            return;
        }

        Player player = event.getPlayer();
        RobotType type = service.getActivatorItem().getType(item);
        RobotEconomy economy = service.getActivatorItem().getEconomy(item);
        int amount = service.getActivatorItem().getAmount(item);
        if (type == null || economy == null || amount <= 0) {
            return;
        }

        event.setCancelled(true);
        service.activateRobots(player, type, economy, amount);
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

    private static String formatK(double value) {
        return NumberFormatter.format(value);
    }
}
