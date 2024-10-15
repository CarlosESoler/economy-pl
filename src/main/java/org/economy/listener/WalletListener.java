package org.economy.listener;

import com.google.common.base.Throwables;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.economy.EconomyPlugin;

public class WalletListener implements Listener {

    EconomyPlugin economyPlugin;

    public WalletListener(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        economyPlugin.walletService.fetchWallet(event.getPlayer()).whenComplete(((wallet, error) -> {
            if(error != null) {
                Throwables.propagate(error);
                return;
            }
            economyPlugin.walletService.putWalletInCache(wallet);
        }));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }
}
