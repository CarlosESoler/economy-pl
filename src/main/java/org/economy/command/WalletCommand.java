package org.economy.command;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.economy.EconomyPlugin;
import org.economy.model.Wallet;
import org.economy.parser.ParserUtils;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class WalletCommand extends Command {

    EconomyPlugin economyPlugin;

    public WalletCommand(EconomyPlugin economyPlugin) {
        super("money");
        this.economyPlugin = economyPlugin;
    }

    private void showMoney(CommandSender commandSender, OfflinePlayer player) {
        economyPlugin.walletService.fetchWallet(player.getUniqueId()).thenAccept((wallet) -> {
            if(wallet == null) {
                commandSender.sendMessage("O jogador não tem carteira!");
                return;
            }
            commandSender.sendMessage("O money é: " + wallet.getBalance().toString());
        });
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 0) {
            showMoney(commandSender, (Player) commandSender);
            return true;
        }

        if (args[0].equalsIgnoreCase("send")) {
            if (args.length == 1) {
                commandSender.sendMessage("Insira a pessoa que você deseja enviar o money.");
                return false;
            }
            if (args.length == 2) {
                commandSender.sendMessage("Insira a quantidade a ser enviada.");
                return false;
            }
            if(ParserUtils.parseToBigDecimal(args[2]).compareTo(BigDecimal.ZERO) <= 0 ) {
                commandSender.sendMessage("Você não pode enviar valores abaixo de 0 ou negativos...");
                return false;
            }
            if(args.length == 3) {
                CompletableFuture.runAsync(() -> {
                    OfflinePlayer playerReceiver = Bukkit.getOfflinePlayer(args[1]);
                    Player playerSender = (Player) commandSender;
                    BigDecimal convertedValue = ParserUtils.parseToBigDecimal(args[2]);
                    economyPlugin.walletService.sendMoney(playerSender, playerReceiver.getUniqueId(), convertedValue).whenComplete((hasCompleted, throwable) -> {
                        if(throwable != null) {
                            Throwables.propagate(throwable);
                            return;
                        }
                        if(!hasCompleted) {
                            return;
                        }
                        playerSender.sendMessage("Você enviou: " + convertedValue);
                        playerReceiver.getPlayer().sendMessage("Você recebeu: " + convertedValue);
                    });
                });
                return true;
            }
        }

        if(args[0].equalsIgnoreCase("top")) {
            economyPlugin.walletService.fetchTopTenAsync().thenAccept((walletList) -> {
                commandSender.sendMessage("TOP 10");
                for (int i = 0; i < walletList.size(); i++) {
                    Wallet wallet = walletList.get(i);
                    commandSender.sendMessage((i + 1) + " - " + Bukkit.getOfflinePlayer(wallet.getUuid()).getName() + " - " + wallet.getBalance());
                }

            });
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!commandSender.hasPermission("set")) {
                commandSender.sendMessage("Você não tem permissão para fazer isso!");
                return false;
            }
            if(args.length == 1) {
                commandSender.sendMessage("Infome o jogador que você quer setar o dinheiro.");
                return false;
            }
            if(args.length == 2) {
                commandSender.sendMessage("Informe o valor");
                return false;
            }
            if(args.length == 3) {
                OfflinePlayer playerWallet = Bukkit.getOfflinePlayer(args[1]);
                economyPlugin.walletService.fetchWallet(playerWallet.getUniqueId()).thenAccept((wallet) -> {
                    wallet.setBalance(ParserUtils.parseToBigDecimal(args[2]));
                    if(economyPlugin.walletService.updateWallet(wallet)) {
                        commandSender.sendMessage("Money setado, balance atual da carteira é: " + wallet.getBalance());
                    }
                });
                return true;
            }
            return false;
        }
        if(args.length == 1) {
            CompletableFuture.runAsync(() -> {
                OfflinePlayer playerToSeeMoney = Bukkit.getOfflinePlayer(args[0]);
                if(playerToSeeMoney == null) {
                    commandSender.sendMessage("O jogador não foi encontrado!");
                    return;
                }
                showMoney(commandSender, playerToSeeMoney);
            });
            return true;
        }

        commandSender.sendMessage("A estrutura do seu comando está incorreta.");
        return false;
    }
}
