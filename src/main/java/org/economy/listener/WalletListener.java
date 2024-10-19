package org.economy.listener;

import com.google.common.base.Throwables;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.economy.EconomyPlugin;
import org.economy.model.Wallet;

import java.math.BigDecimal;

public class WalletListener implements Listener {

    EconomyPlugin economyPlugin;

    public WalletListener(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        economyPlugin.walletService.fetchWallet(event.getPlayer().getUniqueId()).whenComplete(((wallet, error) -> {
            if(error != null) {
                Throwables.propagate(error);
                return;
            }
            if(wallet == null) {
                Wallet newWallet = new Wallet(event.getPlayer().getUniqueId(), BigDecimal.ZERO);
                economyPlugin.walletService.saveWallet(newWallet);
                return;
            }
            economyPlugin.walletService.putWalletInCache(wallet);
        }));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        economyPlugin.walletService.removeWalletOnCache(event.getPlayer().getUniqueId());
    }
}
