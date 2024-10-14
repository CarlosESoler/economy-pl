package org.economy;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.economy.command.WalletCommand;
import org.economy.datautils.database.MySqlStorage;
import org.economy.datautils.database.StorageConfigConnector;
import org.economy.repository.WalletRepository;
import org.economy.service.WalletService;

@Getter
public class EconomyPlugin extends JavaPlugin {

    public WalletService walletService;
    public WalletRepository walletRepository;
    public MySqlStorage mySqlStorage;
    public WalletCommand walletCommand;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        walletService = new WalletService(this);
        walletRepository = new WalletRepository(this);
        mySqlStorage = new StorageConfigConnector(this).establishConnection();

        walletCommand = new WalletCommand(this);

        System.out.printf("Economy plugin enabled!");
    }
}