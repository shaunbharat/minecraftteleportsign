package io.github.shaunbharat.minecraftteleportsign;

import io.github.shaunbharat.minecraftteleportsign.listeners.TeleportSignListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftTeleportSign extends JavaPlugin {
    public static final String PERMISSION_TELEPORT_SIGN = "minecraftteleportsign.teleportsign";
    public static final String PREFIX_COORDINATES = "//";
    public static final String PREFIX_FACING = "///";
    public static final String SEPARATOR = ",";

    @Override
    public void onEnable() {
        getLogger().info(getName() + " has been enabled!");
        getServer().getPluginManager().registerEvents(new TeleportSignListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info(getName() + " has been disabled!");
    }
}
