package org.economy.service;

import org.bukkit.entity.Player;
import org.economy.EconomyPlugin;
import org.economy.model.Wallet;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class WalletService {

    private EconomyPlugin economyPlugin;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private final Map<UUID, Wallet> cachedWallet = new ConcurrentHashMap<>();

    public WalletService(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    public boolean sendMoney(Player playerSender, UUID keyPlayerReceiver, BigDecimal value) {
        fetchWallet(playerSender.getUniqueId()).whenCompleteAsync((wallet, throwable) -> {
           Wallet walletReceiver = fetchWallet(keyPlayerReceiver).join();
            // playerSender.sendMessage(""); Verificar se isso funciona por estar fora da main thread.
        }, executor);

        // Outra forma de fazer
//        executor.execute(() -> {
//            Wallet walletSender = fetchWallet(playerSender.getUniqueId()).join();
//            if(walletSender.getBalance().compareTo(value) < 0) {
//                return;
//            }
//            Wallet walletReceiver = fetchWallet(keyPlayerReceiver).join();
//        });
        return false;
    }

    public CompletableFuture<Wallet> fetchWallet(UUID key) {
        Wallet wallet = cachedWallet.get(key);
        if(wallet != null) {
            return CompletableFuture.completedFuture(wallet);
        }
        return economyPlugin.walletRepository.fetchByKey(key);
    }

    public void putWalletInCache(Wallet wallet) {
        cachedWallet.put(wallet.getUuid(), wallet);
    }

    public Wallet removeCache(UUID key) {
        return cachedWallet.remove(key);
    }
}
