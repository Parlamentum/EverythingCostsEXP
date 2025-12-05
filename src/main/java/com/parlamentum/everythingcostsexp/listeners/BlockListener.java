package com.parlamentum.everythingcostsexp.listeners;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final EverythingCostsEXP plugin;

    public BlockListener(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String materialName = event.getBlock().getType().name();

        // Check general block break cost or specific material cost
        int cost = plugin.getConfigManager().getBlockBreakCost(materialName);
        if (cost == 0) {
            cost = plugin.getConfigManager().getActionCost("block-break");
        }

        if (cost <= 0)
            return;

        // Check bypass permission
        if (player.hasPermission("exp.bypass.action.block-break") || player.hasPermission("exp.bypass.*")) {
            return;
        }

        // Check XP
        if (!plugin.getXPManager().hasEnoughXP(player, cost)) {
            event.setCancelled(true);
            plugin.getXPManager().sendInsufficientXPMessage(player, cost);
            return;
        }

        // Deduct XP
        plugin.getXPManager().deductXP(player, cost, "breaking " + materialName);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String materialName = event.getBlock().getType().name();

        // Check general block place cost or specific material cost
        int cost = plugin.getConfigManager().getBlockPlaceCost(materialName);
        if (cost == 0) {
            cost = plugin.getConfigManager().getActionCost("block-place");
        }

        if (cost <= 0)
            return;

        // Check bypass permission
        if (player.hasPermission("exp.bypass.action.block-place") || player.hasPermission("exp.bypass.*")) {
            return;
        }

        // Check XP
        if (!plugin.getXPManager().hasEnoughXP(player, cost)) {
            event.setCancelled(true);
            plugin.getXPManager().sendInsufficientXPMessage(player, cost);
            return;
        }

        // Deduct XP
        plugin.getXPManager().deductXP(player, cost, "placing " + materialName);
    }
}
