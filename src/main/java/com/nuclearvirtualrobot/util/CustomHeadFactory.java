package com.nuclearvirtualrobot.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.ProfileProperty;

import java.lang.reflect.Field;
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

        UUID uuid = UUID.randomUUID();
        PlayerProfile profile = Bukkit.createProfile(uuid, "head-" + uuid);
        profile.setProperty(new ProfileProperty("textures", base64));
        try {
            skullMeta.setOwnerProfile(profile);
            item.setItemMeta(skullMeta);
        } catch (NoSuchMethodError ignored) {
            try {
                GameProfile legacyProfile = new GameProfile(uuid, "head-" + uuid);
                legacyProfile.getProperties().put("textures", new Property("textures", base64));
                Field profileField = skullMeta.getClass().getDeclaredField(PROFILE_FIELD);
                profileField.setAccessible(true);
                profileField.set(skullMeta, legacyProfile);
                item.setItemMeta(skullMeta);
            } catch (NoSuchFieldException | IllegalAccessException exception) {
                Bukkit.getLogger().log(Level.WARNING, "Falha ao aplicar textura customizada na head.", exception);
            }
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
}
