package org.economy.service;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.economy.EconomyPlugin;
import org.economy.model.Wallet;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

public class WalletService {

    private EconomyPlugin economyPlugin;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private final Map<UUID, Wallet> cachedWallet = new ConcurrentHashMap<>();

    private final List<Wallet> cachedTopTenWallets = new LinkedList<>();

    private final long expirationTime = TimeUnit.MINUTES.toMillis(5);
    private long lastUpdate;

    public WalletService(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    public CompletableFuture<Boolean> sendMoney(Player playerSender, UUID playerReceiverKey, BigDecimal value) {
        return CompletableFuture.supplyAsync(() -> {
            Wallet walletSender = walletSender = fetchWallet(playerSender.getUniqueId()).join();
            Wallet walletReceiver = fetchWallet(playerReceiverKey).join();
            if (walletReceiver == null) {
                playerSender.sendMessage("O usuário que você está enviando o dinheiro não possui carteira.");
                return false;
            }
            if(walletSender.getUuid().equals(walletReceiver.getUuid())) {
                playerSender.sendMessage("Você não pode enviar money pra você mesmo!");
                return false;
            }
            if (walletSender.getBalance().compareTo(value) < 0) {
                playerSender.sendMessage("Você não possui saldo suficiente para realizar essa transação.");
                return false;
            }

            walletSender.setBalance(walletSender.getBalance().subtract(value));
            economyPlugin.walletRepository.updateWallet(walletSender.getUuid(), walletSender.getBalance());

            walletReceiver.setBalance(walletReceiver.getBalance().add(value));
            economyPlugin.walletRepository.updateWallet(walletReceiver.getUuid(), walletReceiver.getBalance());
            return true;
        }, executor);
    }

    public CompletableFuture<List<Wallet>> fetchTopTenAsync() {
        if (!hasCachedExpired()) {
            return CompletableFuture.completedFuture(cachedTopTenWallets);
        }
        return economyPlugin.walletRepository.fetchTopTenAsync().thenApply((wallets) -> {
            cachedTopTenWallets.clear();
            cachedTopTenWallets.addAll(wallets);
            setLastUpdate();
            return wallets;
        });
    }
    public List<Wallet> fetchTopTen() {
        return economyPlugin.walletRepository.fetchTopTen();
    }

    public CompletableFuture<Boolean> updateWalletAsync(Wallet wallet) {
        return economyPlugin.walletRepository.updateWalletAsync(wallet.getUuid(), wallet.getBalance());
    }

    public Boolean updateWallet(Wallet wallet) {
        return economyPlugin.walletRepository.updateWallet(wallet.getUuid(), wallet.getBalance());
    }


    public CompletableFuture<Wallet> fetchWallet(UUID key) {
        Wallet wallet = cachedWallet.get(key);
        if (wallet != null) {
            return CompletableFuture.completedFuture(wallet);
        }
        return economyPlugin.walletRepository.fetchByKeyAsync(key);
    }

    public void putWalletInCache(Wallet wallet) {
        cachedWallet.put(wallet.getUuid(), wallet);
    }

    public void saveWallet(Wallet wallet) {
        economyPlugin.walletRepository.saveWalletAsync(wallet).whenComplete((hasWallet, error) -> {
            if (error != null) {
                Throwables.propagate(error);
                return;
            }
            if(!hasWallet) {
                Bukkit.getLogger().log(Level.SEVERE,
                    "Aconteceu alguma coisa ao salvar a Wallet.");
                return;
            }
            putWalletInCache(wallet);
        });
    }

    public void removeWalletOnCache(UUID key) {
        cachedWallet.remove(key);
    }

    private boolean hasCachedExpired() {
        return System.currentTimeMillis() - lastUpdate > expirationTime;
    }

    private void setLastUpdate() {
        this.lastUpdate = System.currentTimeMillis();
    }
}
