package com.nuclearvirtualrobot.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

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

        if (!applyProfileApi(skullMeta, base64)) {
            applyAuthlibProfile(skullMeta, base64);
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
            Method createProfile = Bukkit.class.getMethod("createProfile", UUID.class, String.class);
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

    private static void applyAuthlibProfile(SkullMeta meta, String base64) {
        try {
            UUID uuid = UUID.randomUUID();
            GameProfile profile = new GameProfile(uuid, "head-" + uuid);
            profile.getProperties().put("textures", new Property("textures", base64));
            var field = meta.getClass().getDeclaredField(PROFILE_FIELD);
            field.setAccessible(true);
            field.set(meta, profile);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Falha ao aplicar textura customizada na head.", ex);
        }
    }
}
