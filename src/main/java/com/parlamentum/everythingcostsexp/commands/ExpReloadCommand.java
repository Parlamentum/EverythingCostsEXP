package com.parlamentum.everythingcostsexp.commands;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import com.parlamentum.everythingcostsexp.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ExpReloadCommand implements CommandExecutor {

    private final EverythingCostsEXP plugin;

    public ExpReloadCommand(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("exp.admin")) {
            sender.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getMessage("no-permission")));
            return true;
        }

        try {
            plugin.getConfigManager().reloadConfig();
            sender.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getMessage("reload-success")));
        } catch (Exception e) {
            sender.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getMessage("reload-fail")));
            e.printStackTrace();
        }
        return true;
    }
}
