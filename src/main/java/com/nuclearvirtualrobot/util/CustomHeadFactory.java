package com.nuclearvirtualrobot.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public final class CustomHeadFactory {
    private static final String PROFILE_FIELD = "profile";

    private CustomHeadFactory() {
    }

    public static ItemStack createHead(String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) {
            return item;
        }

        if (applyProfileApi(skullMeta, base64)) {
            item.setItemMeta(skullMeta);
        }

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

    private static boolean applyProfileApi(SkullMeta meta, String base64) {
        try {
            Method createProfile;
            try {
                createProfile = Bukkit.class.getMethod("createProfile", UUID.class, String.class);
            } catch (NoSuchMethodException ex) {
                createProfile = Bukkit.class.getMethod("createPlayerProfile", UUID.class, String.class);
            }
            UUID uuid = UUID.randomUUID();
            Object profile = createProfile.invoke(null, uuid, "head-" + uuid);
            Class<?> profilePropertyClass = Class.forName("org.bukkit.profile.ProfileProperty");
            Object property = profilePropertyClass.getConstructor(String.class, String.class)
                    .newInstance("textures", base64);
            Method setProperty = profile.getClass().getMethod("setProperty", profilePropertyClass);
            setProperty.invoke(profile, property);
            Method setOwnerProfile = meta.getClass().getMethod("setOwnerProfile", profile.getClass());
            setOwnerProfile.invoke(meta, profile);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    // Authlib fallback intentionally removed to avoid inconsistent skull meta warnings.
}
