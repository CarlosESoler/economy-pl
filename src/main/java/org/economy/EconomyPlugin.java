package org.economy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.economy.command.WalletCommand;
import org.economy.database.MySqlStorage;
import org.economy.database.StorageConfigConnector;
import org.economy.listener.WalletListener;
import org.economy.repository.WalletRepository;
import org.economy.service.WalletService;
import org.economy.util.CommandMapFetcher;

@Getter
public class EconomyPlugin extends JavaPlugin {

    public WalletService walletService;
    public WalletRepository walletRepository;
    public MySqlStorage mySqlStorage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        walletService = new WalletService(this);
        walletRepository = new WalletRepository(this);
        mySqlStorage = new StorageConfigConnector(this).establishConnection();

        Bukkit.getPluginManager().registerEvents(new WalletListener(this), this);

        CommandMapFetcher.registerCommands(new WalletCommand(this));
        System.out.printf("Economy plugin enabled!");
    }
}