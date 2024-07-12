package io.github.shaunbharat.minecraftteleportsign.listeners;

import io.github.shaunbharat.minecraftteleportsign.MinecraftTeleportSign;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class TeleportSignListener implements Listener {
    private static final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only handle left clicks on signs. For some reason, one right-click on a block is registered as multiple events.
        // Left-clicks are fine though, and we can just reserve right-clicks for editing the sign anyway.
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        // Ignore if the block is not a sign, or if the player is sneaking, so sneaking can be used to break the sign and not teleport.
        if (clickedBlock == null || !(event.getClickedBlock().getState() instanceof Sign) || event.getPlayer().isSneaking()) {
            return;
        }

        Player player = event.getPlayer();
        Sign sign = (Sign) event.getClickedBlock().getState();
        List<Component> lines = sign.getTargetSide(player).lines();

        for (Component line : lines) {
            String lineText = serializer.serialize(line);

            double x = player.getLocation().getX();
            double y = player.getLocation().getY();
            double z = player.getLocation().getZ();
            float yaw = player.getLocation().getYaw();
            float pitch = player.getLocation().getPitch();

            // Remove all numbers (including negatives with the hyphen and decimals with the period) and commas from the line, so we can check if it is a valid line.
            String prefix = lineText.replaceAll("[0-9-.,]", "");
            if (prefix.equals(MinecraftTeleportSign.PREFIX_COORDINATES)) {
                String[] coordinates = lineText.replace(MinecraftTeleportSign.PREFIX_COORDINATES, "").split(MinecraftTeleportSign.SEPARATOR);
                try {
                    x = Double.parseDouble(coordinates[0]);
                    y = Double.parseDouble(coordinates[1]);
                    z = Double.parseDouble(coordinates[2]);
                } catch (NumberFormatException ignored) {} // If the line is not a valid coordinate line, the values will remain the same.
            }

            if (lineText.startsWith(MinecraftTeleportSign.PREFIX_FACING)) {
                String[] facing = lineText.replace(MinecraftTeleportSign.PREFIX_FACING, "").split(MinecraftTeleportSign.SEPARATOR);
                try {
                    yaw = Float.parseFloat(facing[0]);
                    pitch = Float.parseFloat(facing[1]);
                } catch (NumberFormatException ignored) {} // If the line is not a valid facing line, the values will remain the same.
            }

            player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        for (Component line : event.lines()) {
            String lineText = serializer.serialize(line);
            if ((lineText.startsWith(MinecraftTeleportSign.PREFIX_COORDINATES) || lineText.startsWith(MinecraftTeleportSign.PREFIX_FACING)) && !event.getPlayer().hasPermission(MinecraftTeleportSign.PERMISSION_TELEPORT_SIGN)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(String.format("You do not have permission to create a teleport sign. No lines in your sign can start with '%s' or '%s'.", MinecraftTeleportSign.PREFIX_COORDINATES, MinecraftTeleportSign.PREFIX_FACING));
                return;
            }
        }
    }
}
