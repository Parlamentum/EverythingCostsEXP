package com.parlamentum.everythingcostsexp.manager;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfirmationManager {

    private final EverythingCostsEXP plugin;
    private final Map<UUID, PendingCommand> pendingCommands = new HashMap<>();
    private final Set<String> confirmedHistory = new HashSet<>();
    private File historyFile;

    public enum ConfirmationMode {
        ALWAYS, ONCE, NEVER
    }

    public static class PendingCommand {
        private final String command; // or action identifier
        private final int cost;
        private final long timestamp;
        private final Runnable action;

        public PendingCommand(String command, int cost, Runnable action) {
            this.command = command;
            this.cost = cost;
            this.action = action;
            this.timestamp = System.currentTimeMillis();
        }

        public String getCommand() {
            return command;
        }

        public int getCost() {
            return cost;
        }

        public boolean isExpired(int timeoutSeconds) {
            return System.currentTimeMillis() - timestamp > timeoutSeconds * 1000L;
        }

        public void execute() {
            action.run();
        }
    }

    public ConfirmationManager(EverythingCostsEXP plugin) {
        this.plugin = plugin;
        loadHistory();
    }

    private void loadHistory() {
        historyFile = new File(plugin.getDataFolder(), "confirmations.yml");
        if (!historyFile.exists())
            return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(historyFile);
        List<String> history = config.getStringList("history");
        confirmedHistory.addAll(history);
    }

    public void saveHistory() {
        if (historyFile == null)
            return;
        YamlConfiguration config = new YamlConfiguration();
        config.set("history", new ArrayList<>(confirmedHistory));
        try {
            config.save(historyFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save confirmation history: " + e.getMessage());
        }
    }

    public boolean requiresConfirmation(Player player, String identifier) {
        String modeStr = plugin.getConfig().getString("confirmation.mode", "ONCE").toUpperCase();
        ConfirmationMode mode;
        try {
            mode = ConfirmationMode.valueOf(modeStr);
        } catch (IllegalArgumentException e) {
            mode = ConfirmationMode.ONCE;
        }

        if (mode == ConfirmationMode.NEVER)
            return false;
        if (mode == ConfirmationMode.ALWAYS)
            return true;

        // ONCE mode
        String key = player.getUniqueId() + ":" + identifier;
        return !confirmedHistory.contains(key);
    }

    public void requestConfirmation(Player player, String identifier, int cost, Runnable action) {
        pendingCommands.put(player.getUniqueId(), new PendingCommand(identifier, cost, action));

        // Send message
        String msg = plugin.getConfigManager().getMessage("confirmation-required")
                .replace("%cost%", String.valueOf(cost))
                .replace("%action%", identifier);
        player.sendMessage(com.parlamentum.everythingcostsexp.util.MessageUtil.colorize(msg));
    }

    public void confirm(Player player) {
        PendingCommand pending = pendingCommands.remove(player.getUniqueId());
        if (pending == null) {
            player.sendMessage(com.parlamentum.everythingcostsexp.util.MessageUtil
                    .colorize(plugin.getConfigManager().getMessage("no-pending-command")));
            return;
        }

        int timeout = plugin.getConfig().getInt("confirmation.timeout", 30);
        if (pending.isExpired(timeout)) {
            player.sendMessage(com.parlamentum.everythingcostsexp.util.MessageUtil
                    .colorize(plugin.getConfigManager().getMessage("confirmation-expired")));
            return;
        }

        // Check XP again just in case
        if (!plugin.getXPManager().hasEnoughXP(player, pending.getCost())) {
            plugin.getXPManager().sendInsufficientXPMessage(player, pending.getCost());
            return;
        }

        // Execute
        pending.execute();

        // Record history if ONCE mode
        String modeStr = plugin.getConfig().getString("confirmation.mode", "ONCE").toUpperCase();
        if ("ONCE".equals(modeStr)) {
            confirmedHistory.add(player.getUniqueId() + ":" + pending.getCommand());
            saveHistory(); // Save immediately or periodically? Immediate is safer for now.
        }
    }

    public boolean hasPending(Player player) {
        return pendingCommands.containsKey(player.getUniqueId());
    }
}
