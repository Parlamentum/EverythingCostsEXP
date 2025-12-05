package com.parlamentum.everythingcostsexp.commands;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import com.parlamentum.everythingcostsexp.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpAddCommand implements CommandExecutor {

    private final EverythingCostsEXP plugin;

    public ExpAddCommand(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("exp.admin")) {
            sender.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getMessage("no-permission")));
            return true;
        }

        if (args.length < 2) {
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getMessage("player-not-found")));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getMessage("invalid-number")));
            return true;
        }

        plugin.getXPManager().addXP(target, amount);

        String message = plugin.getConfigManager().getMessage("add-success")
                .replace("%player%", target.getName())
                .replace("%amount%", String.valueOf(amount));
        sender.sendMessage(MessageUtil.colorize(message));

        return true;
    }
}
