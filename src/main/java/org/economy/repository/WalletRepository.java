package org.economy.repository;

import org.economy.EconomyPlugin;
import org.economy.model.Wallet;
import org.economy.parser.ParserUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class WalletRepository {

    EconomyPlugin economyPlugin;

    private static final String SELECT_QUERY = "SELECT * FROM WALLET WHERE UUID = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM WALLET";

    public WalletRepository(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    public CompletableFuture<Wallet> fetchByKey(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            try(PreparedStatement preparedStatement = economyPlugin.mySqlStorage.getConnection().prepareStatement(SELECT_QUERY)) {
                preparedStatement.setString(1, id.toString());
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        return Wallet.parseFromResultSet(resultSet);
                    }
                }
            } catch (Exception e) {
                economyPlugin.getLogger().log(Level.WARNING,
                    "Alguma coisa deu errada na query");
            }
            return null;
        });
    }

    public CompletableFuture<List<Wallet>> fetchAll() {
        return CompletableFuture.supplyAsync(() -> {
            try(PreparedStatement preparedStatement = economyPlugin.mySqlStorage.getConnection().prepareStatement(SELECT_ALL_QUERY)) {
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<Wallet> wallets = new ArrayList<>();
                    while(resultSet.next()) {
                        wallets.add(Wallet.parseFromResultSet(resultSet));
                    }
                    return wallets;
                }
            } catch (Exception e) {
                economyPlugin.getLogger().log(Level.WARNING,
                    "Alguma coisa deu errada na query");
            }
            return null;
        });
    }
}
