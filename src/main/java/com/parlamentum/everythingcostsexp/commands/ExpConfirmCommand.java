package com.parlamentum.everythingcostsexp.commands;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpConfirmCommand implements CommandExecutor {

    private final EverythingCostsEXP plugin;

    public ExpConfirmCommand(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        plugin.getConfirmationManager().confirm(player);
        return true;
    }
}
