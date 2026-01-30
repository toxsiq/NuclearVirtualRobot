package com.nuclearvirtualrobot.util;

import com.nuclearvirtualrobot.model.RobotEconomy;
import com.nuclearvirtualrobot.model.RobotType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RobotActivatorItem {
    private final NamespacedKey typeKey;
    private final NamespacedKey economyKey;
    private final NamespacedKey amountKey;

    public RobotActivatorItem(JavaPlugin plugin) {
        this.typeKey = new NamespacedKey(plugin, "robot_type");
        this.economyKey = new NamespacedKey(plugin, "robot_economy");
        this.amountKey = new NamespacedKey(plugin, "robot_amount");
    }

    public ItemStack createActivator(RobotType type, RobotEconomy economy, int amount) {
        ItemStack item = switch (economy) {
            case TOKENS -> CustomHeadFactory.createHead(RobotHeadTextures.HEAD_ROBO_TOKENS);
            case TOXINA -> CustomHeadFactory.createHead(RobotHeadTextures.HEAD_ROBO_TOXINA);
            case CASH -> CustomHeadFactory.createHead(RobotHeadTextures.HEAD_ROBO_CASH);
        };

        if (item.getType() == Material.AIR) {
            item = new ItemStack(Material.PLAYER_HEAD);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component name = Component.text("Robô de ", NamedTextColor.GRAY)
                    .append(economy.highlightComponent());
            meta.displayName(name);
            meta.lore(List.of(
                    Component.text("Tipo: ", NamedTextColor.GRAY).append(type.displayComponent()),
                    Component.text("Quantidade: ", NamedTextColor.GRAY)
                            .append(Component.text(amount, NamedTextColor.WHITE)),
                    Component.text("Shift + clique direito para stackar.", NamedTextColor.DARK_GRAY)
            ));

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(typeKey, PersistentDataType.STRING, type.name());
            container.set(economyKey, PersistentDataType.STRING, economy.name());
            container.set(amountKey, PersistentDataType.INTEGER, amount);
            item.setItemMeta(meta);
        }

        item.setAmount(1);
        return item;
    }

    public boolean isActivator(ItemStack item) {
        if (item == null || item.getType() != Material.PLAYER_HEAD) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(typeKey, PersistentDataType.STRING)
                && container.has(economyKey, PersistentDataType.STRING)
                && container.has(amountKey, PersistentDataType.INTEGER);
    }

    public RobotType getType(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        String raw = meta.getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);
        if (raw == null) {
            return null;
        }
        return RobotType.valueOf(raw);
    }

    public RobotEconomy getEconomy(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        String raw = meta.getPersistentDataContainer().get(economyKey, PersistentDataType.STRING);
        if (raw == null) {
            return null;
        }
        return RobotEconomy.valueOf(raw);
    }

    public int getAmount(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }
        Integer amount = meta.getPersistentDataContainer().get(amountKey, PersistentDataType.INTEGER);
        return amount == null ? 0 : amount;
    }
}
