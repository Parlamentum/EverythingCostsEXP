package com.parlamentum.everythingcostsexp.listeners;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerActionListener implements Listener {

    private final EverythingCostsEXP plugin;

    public PlayerActionListener(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH)
            return;

        Player player = event.getPlayer();
        handleAction(player, "fishing", event);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player))
            return;

        Player player = (Player) event.getBreeder();
        handleAction(player, "breeding", event);
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player))
            return;

        Player player = (Player) event.getOwner();
        handleAction(player, "taming", event);
    }

    private void handleAction(Player player, String actionName, org.bukkit.event.Cancellable event) {
        int cost = plugin.getConfigManager().getActionCost(actionName);
        if (cost <= 0)
            return;

        if (player.hasPermission("exp.bypass.action." + actionName) || player.hasPermission("exp.bypass.*")) {
            return;
        }

        if (!plugin.getXPManager().hasEnoughXP(player, cost)) {
            event.setCancelled(true);
            plugin.getXPManager().sendInsufficientXPMessage(player, cost);
            return;
        }

        if (plugin.getConfirmationManager().requiresConfirmation(player, "action." + actionName)) {
            event.setCancelled(true);
            plugin.getConfirmationManager().requestConfirmation(player, "action." + actionName, cost, () -> {
                player.sendMessage(com.parlamentum.everythingcostsexp.util.MessageUtil
                        .colorize("&aConfirmation received. Please try again."));
            });
            return;
        }

        plugin.getXPManager().deductXP(player, cost, actionName);
    }
}
