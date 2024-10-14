package org.economy.datautils.database;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public class StorageConfigConnector {

    private final Plugin plugin;

    public StorageConfigConnector(Plugin plugin) {
        this.plugin = plugin;
    }

    public MySqlStorage establishConnection() {
        final var pluginManager = plugin.getServer().getPluginManager();
        final var config = plugin.getConfig();
        final var logger = plugin.getLogger();

        final var storageSection = config.getConfigurationSection("storage");
        if (storageSection == null) {
            logger.info("The plugin can't find storage section on config.");
            pluginManager.disablePlugin(plugin);
            return null;
        }

        final MySqlStorage sqlStorage = new MySqlStorage();
        ConfigurationSection credentialsSection = storageSection.getConfigurationSection("credentials");
        if (credentialsSection == null) {
            logger.info("The plugin can't find the storage credentials section on config.");
            pluginManager.disablePlugin(plugin);
            return null;
        }

        SqlCredentials sqlCredentials = new SqlCredentials(credentialsSection.getString("host"),
                credentialsSection.getString("password"),
                credentialsSection.getString("database"),
                credentialsSection.getString("user"));

        if (!sqlStorage.startConnection(sqlCredentials)) {
            logger.info("Something is wrong with storage credentials");
            pluginManager.disablePlugin(plugin);
            return null;
        }

        return sqlStorage;
    }
}
