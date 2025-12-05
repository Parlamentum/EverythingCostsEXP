package com.parlamentum.everythingcostsexp.config;

import com.parlamentum.everythingcostsexp.EverythingCostsEXP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    private final EverythingCostsEXP plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public ConfigManager(EverythingCostsEXP plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
        saveDefaultMessages();
    }

    public void saveDefaultConfig() {
        plugin.saveDefaultConfig();
    }

    public void saveDefaultMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        reloadMessages();
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource("messages.yml");
        if (defConfigStream != null) {
            messagesConfig.setDefaults(YamlConfiguration
                    .loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
        }
    }

    public FileConfiguration getMessages() {
        if (messagesConfig == null) {
            reloadMessages();
        }
        return messagesConfig;
    }

    public String getMessage(String path) {
        return getMessages().getString("messages." + path, "&cMessage not found: " + path);
    }

    public int getCommandCost(String command) {
        return plugin.getConfig().getInt("costs.commands." + command, 0);
    }

    public int getActionCost(String action) {
        return plugin.getConfig().getInt("costs.actions." + action, 0);
    }

    public int getBlockBreakCost(String material) {
        return plugin.getConfig().getInt("costs.actions.block-break." + material, 0);
    }

    public int getBlockPlaceCost(String material) {
        return plugin.getConfig().getInt("costs.actions.block-place." + material, 0);
    }

    public boolean isActionBarEnabled() {
        return plugin.getConfig().getBoolean("display.action-bar", true);
    }

    public boolean isSoundEnabled() {
        return plugin.getConfig().getBoolean("display.sound", true);
    }

    public boolean isTitleWarningEnabled() {
        return plugin.getConfig().getBoolean("display.title-warning", false);
    }
}
