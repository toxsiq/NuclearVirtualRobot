package com.nuclearvirtualrobot.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.ProfileProperty;

import java.util.List;
import java.util.UUID;

public final class CustomHeadFactory {
    private CustomHeadFactory() {
    }

    public static ItemStack createHead(String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) {
            return item;
        }

        UUID uuid = UUID.randomUUID();
        PlayerProfile profile = Bukkit.createProfile(uuid, "head-" + uuid);
        profile.setProperty(new ProfileProperty("textures", base64));
        skullMeta.setOwnerProfile(profile);
        item.setItemMeta(skullMeta);

        return item;
    }

    public static ItemStack createHead(String base64, Component name, List<Component> lore) {
        ItemStack item = createHead(base64);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        if (name != null) {
            meta.displayName(name);
        }
        if (lore != null) {
            meta.lore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
