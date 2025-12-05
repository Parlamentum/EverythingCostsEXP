package com.parlamentum.everythingcostsexp.commands;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import com.parlamentum.everythingcostsexp.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCheckCommand implements CommandExecutor {

    private final EverythingCostsEXP plugin;

    public ExpCheckCommand(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("exp.pay.check")) {
            player.sendMessage(MessageUtil.colorize(plugin.getConfigManager().getMessage("no-permission")));
            return true;
        }

        int level = plugin.getXPManager().getXPLevel(player);
        String message = plugin.getConfigManager().getMessage("balance")
                .replace("%level%", String.valueOf(level));

        player.sendMessage(MessageUtil.colorize(message));
        return true;
    }
}
