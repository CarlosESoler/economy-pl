package org.economy.service;

import com.google.common.base.Throwables;
import org.bukkit.entity.Player;
import org.economy.EconomyPlugin;
import org.economy.model.Wallet;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class WalletService {

    private EconomyPlugin economyPlugin;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private final Map<UUID, Wallet> cachedWallet = new ConcurrentHashMap<>();

    public WalletService(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    public CompletableFuture<Boolean> sendMoney(Player playerSender, UUID keyPlayerReceiver, BigDecimal value) {
        fetchWallet(playerSender.getUniqueId()).whenCompleteAsync((walletSender, throwable) -> {
           Wallet walletReceiver = fetchWallet(keyPlayerReceiver).join();
           if(walletReceiver == null) {
               playerSender.sendMessage("O usuário que você está enviando o dinheiro não possui carteira.");
               return;
           }
           if(walletSender.getBalance().compareTo(walletReceiver.getBalance()) < 0) {
                playerSender.sendMessage("Você não possui saldo suficiente para realizar essa transação.");
                return;
           }
           walletSender.setBalance(walletSender.getBalance().subtract(value));
           economyPlugin.walletRepository.updateWalletValue(walletSender.getUuid(), walletSender.getBalance());

        }, executor);
    }

    public CompletableFuture<Wallet> fetchWallet(UUID key) {
        Wallet wallet = cachedWallet.get(key);
        if(wallet != null) {
            return CompletableFuture.completedFuture(wallet);
        }
        return economyPlugin.walletRepository.fetchByKeyAsync(key);
    }

    public void putWalletInCache(Wallet wallet) {
        cachedWallet.put(wallet.getUuid(), wallet);
    }

    public void saveWallet(Wallet wallet) {
        economyPlugin.walletRepository.saveWalletAsync(wallet).whenComplete((walletSaved, error) ->{
            if(error != null) {
                Throwables.propagate(error);
                return;
            }
            putWalletInCache(wallet);
        });

    }

    public Wallet removeCache(UUID key) {
        return cachedWallet.remove(key);
    }
}
