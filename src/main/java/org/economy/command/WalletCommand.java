package org.economy.command;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.economy.EconomyPlugin;
import org.economy.parser.ParserUtils;

import java.math.BigDecimal;
import java.util.logging.Level;

public class WalletCommand extends Command {

    EconomyPlugin economyPlugin;

    public WalletCommand(EconomyPlugin economyPlugin) {
        super("money");
        this.economyPlugin = economyPlugin;
    }

    private void showMoney(CommandSender commandSender, Player player) {
        economyPlugin.walletService.fetchWallet(player.getUniqueId()).thenAccept((wallet) -> {
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
            if(args.length == 3) {
                OfflinePlayer playerReceiver = Bukkit.getOfflinePlayer(args[1]);
                Player playerSender = (Player) commandSender;
                BigDecimal convertedValue = ParserUtils.parseToBigDecimal(args[2]);
                economyPlugin.walletService.sendMoney(playerSender, playerReceiver.getUniqueId(), convertedValue).whenComplete((hasCompleted, throwable) -> {
                    if(!hasCompleted) {
                        Bukkit.getLogger().log(Level.INFO,
                            "Aconteceu algum erro ao realizar a transação");
                    }
                });
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!commandSender.hasPermission("set")) {
                commandSender.sendMessage("Você não tem permissão para fazer isso!.");
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
                        commandSender.sendMessage("Money setado, balance atual da carteira é: " + wallet.getBalance().toString());
                    }
                });
                return true;
            }
            return false;
        }
        OfflinePlayer playerToSeeMoney = Bukkit.getOfflinePlayer(args[0]);
        if(playerToSeeMoney != null) {
            showMoney(commandSender, (Player) playerToSeeMoney);
            return true;
        }
        return false;
    }
}
