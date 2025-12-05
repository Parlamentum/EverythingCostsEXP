package com.parlamentum.everythingcostsexp;

import com.parlamentum.everythingcostsexp.commands.*;
import com.parlamentum.everythingcostsexp.config.ConfigManager;
import com.parlamentum.everythingcostsexp.listeners.BlockListener;
import com.parlamentum.everythingcostsexp.listeners.CommandListener;
import com.parlamentum.everythingcostsexp.listeners.InventoryListener;
import com.parlamentum.everythingcostsexp.listeners.PlayerActionListener;
import com.parlamentum.everythingcostsexp.listeners.TeleportListener;
import com.parlamentum.everythingcostsexp.manager.ConfirmationManager;
import com.parlamentum.everythingcostsexp.manager.XPManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EverythingCostsEXP extends JavaPlugin {

    private static EverythingCostsEXP instance;
    private ConfigManager configManager;
    private XPManager xpManager;
    private ConfirmationManager confirmationManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.xpManager = new XPManager(this);
        this.confirmationManager = new ConfirmationManager(this);

        // Register commands
        getCommand("expcheck").setExecutor(new ExpCheckCommand(this));
        getCommand("expreload").setExecutor(new ExpReloadCommand(this));
        getCommand("expset").setExecutor(new ExpSetCommand(this));
        getCommand("expadd").setExecutor(new ExpAddCommand(this));
        getCommand("expconfirm").setExecutor(new ExpConfirmCommand(this));
        getCommand("xpmenu").setExecutor(new ExpMenuCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerActionListener(this), this);

        getLogger().info("EverythingCostsEXP has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("EverythingCostsEXP has been disabled!");
    }

    public static EverythingCostsEXP getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public XPManager getXPManager() {
        return xpManager;
    }

    public ConfirmationManager getConfirmationManager() {
        return confirmationManager;
    }
}
