package com.parlamentum.everythingcostsexp.manager;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import com.parlamentum.everythingcostsexp.util.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class XPManager {

    private final EverythingCostsEXP plugin;

    public XPManager(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    public boolean hasEnoughXP(Player player, int cost) {
        return player.getLevel() >= cost;
    }

    public void deductXP(Player player, int cost, String actionName) {
        if (cost <= 0)
            return;

        player.setLevel(player.getLevel() - cost);

        // Send feedback
        sendFeedback(player, cost, actionName);
    }

    public void addXP(Player player, int amount) {
        player.setLevel(player.getLevel() + amount);
    }

    public void setXP(Player player, int level) {
        player.setLevel(level);
    }

    public int getXPLevel(Player player) {
        return player.getLevel();
    }

    private void sendFeedback(Player player, int cost, String actionName) {
        String paidMsg = plugin.getConfigManager().getMessage("paid")
                .replace("%cost%", String.valueOf(cost))
                .replace("%action%", actionName);

        // Action Bar
        if (plugin.getConfigManager().isActionBarEnabled()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(MessageUtil.colorize(paidMsg)));
        } else {
            player.sendMessage(MessageUtil.colorize(paidMsg));
        }

        // Sound
        if (plugin.getConfigManager().isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
    }

    public void sendInsufficientXPMessage(Player player, int cost) {
        String noXpMsg = plugin.getConfigManager().getMessage("no-xp")
                .replace("%cost%", String.valueOf(cost));

        player.sendMessage(MessageUtil.colorize(noXpMsg));

        // Title Warning
        if (plugin.getConfigManager().isTitleWarningEnabled()) {
            player.sendTitle(MessageUtil.colorize("&cInsufficient XP"),
                    MessageUtil.colorize("&eNeed " + cost + " levels"), 10, 40, 10);
        }

        // Sound
        if (plugin.getConfigManager().isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1.0f);
        }
    }
}
