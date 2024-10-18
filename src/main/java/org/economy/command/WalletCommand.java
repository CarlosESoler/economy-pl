package org.economy.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.economy.EconomyPlugin;
import org.economy.parser.ParserUtils;

import java.math.BigDecimal;

public class WalletCommand extends Command {

    EconomyPlugin economyPlugin;

    public WalletCommand(EconomyPlugin economyPlugin) {
        super("money");
        this.economyPlugin = economyPlugin;
    }

    private void showMoney(CommandSender commandSender, Player player) {
        economyPlugin.walletService.fetchWallet(player.getUniqueId()).thenAccept((wallet) -> {
            commandSender.sendMessage("Seu money é: " + wallet.getBalance().toString());
        });
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 0) {
            showMoney(commandSender, (Player) commandSender);
            return true;
        }

        if (args[0].equalsIgnoreCase("send")) {
            // /money send example 1000
            //        [0,    1,     2]
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
                economyPlugin.walletService.sendMoney(playerSender, playerReceiver.getUniqueId(), convertedValue);
            }
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!commandSender.hasPermission("set")) {
                return false;
            }
        }
        return false;
    }
}
