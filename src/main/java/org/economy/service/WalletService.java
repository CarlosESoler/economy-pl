package org.economy.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.economy.EconomyPlugin;
import org.economy.datautils.database.MySqlStorage;
import org.economy.model.Wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class WalletService {

    private EconomyPlugin economyPlugin;

    private final Map<UUID, Wallet> cachedWallet = new ConcurrentHashMap<>();

    public WalletService(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    public boolean sendMoney(Player playerSender, Player playerReceiver, BigDecimal value) {
        return true;
    }

    public CompletableFuture<Wallet> fetchWallet(Player player) {
        Wallet wallet = cachedWallet.get(player.getUniqueId());
        if(wallet != null) {
            return CompletableFuture.completedFuture(wallet);
        }
        return economyPlugin.walletRepository.fetchByKey(player.getUniqueId());
    }

    public void putWalletInCache(Wallet wallet) {
        cachedWallet.put(wallet.getUuid(), wallet);
    }

    public boolean removeCache() {
        return true;
    }
}
