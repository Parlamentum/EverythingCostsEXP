package com.parlamentum.everythingcostsexp.listeners;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    private final EverythingCostsEXP plugin;

    public CommandListener(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage(); // e.g., "/home" or "/home name"
        String command = message.split(" ")[0].substring(1); // "home"

        // Check if command has a cost
        int cost = plugin.getConfigManager().getCommandCost(command);
        if (cost <= 0)
            return;

        // Check bypass permission
        if (player.hasPermission("exp.bypass.command." + command) || player.hasPermission("exp.bypass.*")) {
            return;
        }

        // Check XP
        if (!plugin.getXPManager().hasEnoughXP(player, cost)) {
            event.setCancelled(true);
            plugin.getXPManager().sendInsufficientXPMessage(player, cost);
            return;
        }

        // Check Confirmation
        if (plugin.getConfirmationManager().requiresConfirmation(player, "command." + command)) {
            event.setCancelled(true);
            plugin.getConfirmationManager().requestConfirmation(player, "command." + command, cost, () -> {
                player.performCommand(message.substring(1)); // Execute original command without slash
                plugin.getXPManager().deductXP(player, cost, "command /" + command);
            });
            return;
        }

        // Deduct XP immediately if no confirmation needed
        plugin.getXPManager().deductXP(player, cost, "command /" + command);
    }
}
