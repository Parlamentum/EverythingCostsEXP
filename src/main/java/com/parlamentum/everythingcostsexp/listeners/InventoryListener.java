package com.parlamentum.everythingcostsexp.listeners;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryListener implements Listener {

    private final EverythingCostsEXP plugin;

    public InventoryListener(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null)
            return;

        // Handle Crafting specific items
        if (event.getClickedInventory().getType() == InventoryType.WORKBENCH
                || event.getClickedInventory().getType() == InventoryType.CRAFTING) {
            if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                if (event.getCurrentItem() == null)
                    return;

                String itemType = event.getCurrentItem().getType().name().toLowerCase().replace("_", "-");
                String configKey = "craft-" + itemType;

                int cost = plugin.getConfigManager().getActionCost(configKey);
                if (cost > 0) {
                    if (player.hasPermission("exp.bypass.action.craft") || player.hasPermission("exp.bypass.*")) {
                        return;
                    }

                    if (!plugin.getXPManager().hasEnoughXP(player, cost)) {
                        event.setCancelled(true);
                        plugin.getXPManager().sendInsufficientXPMessage(player, cost);
                        return;
                    }

                    if (plugin.getConfirmationManager().requiresConfirmation(player, "action." + configKey)) {
                        event.setCancelled(true);
                        plugin.getConfirmationManager().requestConfirmation(player, "action." + configKey, cost, () -> {
                            player.sendMessage(com.parlamentum.everythingcostsexp.util.MessageUtil
                                    .colorize("&aConfirmation received. Please click the item again."));
                        });
                        return;
                    }

                    plugin.getXPManager().deductXP(player, cost, "crafting " + itemType);
                }
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        int cost = plugin.getConfigManager().getActionCost("enchant");

        if (cost <= 0)
            return;

        if (player.hasPermission("exp.bypass.action.enchant") || player.hasPermission("exp.bypass.*")) {
            return;
        }

        if (!plugin.getXPManager().hasEnoughXP(player, cost)) {
            event.setCancelled(true);
            plugin.getXPManager().sendInsufficientXPMessage(player, cost);
            return;
        }

        if (plugin.getConfirmationManager().requiresConfirmation(player, "action.enchant")) {
            event.setCancelled(true);
            plugin.getConfirmationManager().requestConfirmation(player, "action.enchant", cost, () -> {
                player.sendMessage(com.parlamentum.everythingcostsexp.util.MessageUtil
                        .colorize("&aConfirmation received. Please try enchanting again."));
            });
            return;
        }

        plugin.getXPManager().deductXP(player, cost, "enchanting");
    }
}
