package com.parlamentum.everythingcostsexp.commands;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ExpMenuCommand implements CommandExecutor {

    private final EverythingCostsEXP plugin;

    public ExpMenuCommand(EverythingCostsEXP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        openMenu(player);
        return true;
    }

    private void openMenu(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        if (meta == null)
            return;

        meta.setTitle("XP Costs");
        meta.setAuthor("Server");

        ComponentBuilder builder = new ComponentBuilder();

        builder.append("§lXP Costs Menu§r\n\n");
        builder.append("§nCommands:§r\n");

        ConfigurationSection commands = plugin.getConfig().getConfigurationSection("costs.commands");
        if (commands != null) {
            for (String cmd : commands.getKeys(false)) {
                int cost = commands.getInt(cmd);
                builder.append("/" + cmd + ": §e" + cost + " XP§r\n");
            }
        }

        builder.append("\n§nActions:§r\n");
        ConfigurationSection actions = plugin.getConfig().getConfigurationSection("costs.actions");
        if (actions != null) {
            for (String action : actions.getKeys(false)) {
                // Skip block-break/place sub-sections for brevity or handle them differently
                if (actions.isConfigurationSection(action))
                    continue;

                int cost = actions.getInt(action);
                builder.append(action + ": §e" + cost + " XP§r\n");
            }
        }

        // Add page
        meta.spigot().addPage(builder.create());

        // Block costs page
        builder = new ComponentBuilder();
        builder.append("§lBlock Costs§r\n\n");

        ConfigurationSection breakCosts = plugin.getConfig().getConfigurationSection("costs.actions.block-break");
        if (breakCosts != null) {
            builder.append("§nBreaking:§r\n");
            for (String mat : breakCosts.getKeys(false)) {
                int cost = breakCosts.getInt(mat);
                builder.append(mat + ": §e" + cost + " XP§r\n");
            }
        }

        builder.append("\n");
        ConfigurationSection placeCosts = plugin.getConfig().getConfigurationSection("costs.actions.block-place");
        if (placeCosts != null) {
            builder.append("§nPlacing:§r\n");
            for (String mat : placeCosts.getKeys(false)) {
                int cost = placeCosts.getInt(mat);
                builder.append(mat + ": §e" + cost + " XP§r\n");
            }
        }

        meta.spigot().addPage(builder.create());

        book.setItemMeta(meta);
        player.openBook(book);
    }
}
