package com.parlamentum.everythingcostsexp.listeners;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    private final EverythingCostsEXP plugin;

    public TeleportListener(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // We only want to charge for specific teleport causes if configured
        // Or if it's a general teleport cost
        // Note: Command teleports (/home, /tp) are usually handled by CommandListener
        // But some plugins might trigger teleports directly.

        // For this implementation, we'll focus on the requirement "Teleporting"
        // We should be careful not to double charge if the command already charged
        // them.
        // So we might want to check the cause.

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND
                || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            // If it was a command, it might have been caught by CommandListener.
            // However, if we want a blanket "teleport cost", we can add it here.
            // Let's check for a generic "teleport" action cost.

            int cost = plugin.getConfigManager().getActionCost("teleport");
            if (cost <= 0)
                return;

            // Check if we should ignore command-induced teleports to avoid double charging
            // This is a design decision. The prompt says "Using /home", "Using /back",
            // "Teleporting".
            // If /home costs 10, and teleport costs 5, does /home cost 15?
            // Usually specific overrides general.
            // Let's assume "teleport" action applies to non-command teleports or if
            // explicitly set.
            // But to be safe and simple, let's just check if there is a cost for 'teleport'
            // and apply it
            // ONLY if it wasn't a command that we likely already charged for.
            // Actually, CommandListener runs BEFORE the command executes.
            // So if /home is run, CommandListener charges. Then the command executes and
            // triggers TeleportEvent.
            // If we charge here too, it's double charging.

            // Strategy: Only charge here if it's NOT a command cause, OR if we want to
            // catch things that aren't commands (like ender pearls).
            // Let's add support for ender pearls specifically if needed, or general
            // teleport.

            if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                int pearlCost = plugin.getConfigManager().getActionCost("ender-pearl");
                if (pearlCost > 0) {
                    if (player.hasPermission("exp.bypass.action.ender-pearl") || player.hasPermission("exp.bypass.*"))
                        return;

                    if (!plugin.getXPManager().hasEnoughXP(player, pearlCost)) {
                        event.setCancelled(true);
                        plugin.getXPManager().sendInsufficientXPMessage(player, pearlCost);
                        return;
                    }
                    plugin.getXPManager().deductXP(player, pearlCost, "using ender pearl");
                }
            }
        }
    }
}
