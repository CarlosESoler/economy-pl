package org.economy.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.economy.EconomyPlugin;
import org.economy.datautils.cache.LocalCacheRepository;
import org.economy.datautils.database.MySqlStorage;
import org.economy.model.Wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class WalletService {

    private final Logger logger = Bukkit.getLogger();

    private EconomyPlugin economyPlugin;

    public WalletService(EconomyPlugin economyPlugin) {
    }

    public boolean sendMoney(Player playerSender, Player playerReceiver, BigDecimal value) {
        return true;
    }
}
