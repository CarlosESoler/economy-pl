package org.economy.repository;

import org.economy.EconomyPlugin;
import org.economy.model.Wallet;
import org.economy.parser.ParserUtils;

import java.math.BigDecimal;
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
    private static final String UPDATE_WALLET_VALUE_QUERY = "UPDATE WALLET SET BALANCE = ? WHERE uuid = ?";
    private static final String INSERT_WALLET = "INSERT INTO WALLET VALUES(?, ?)";

    public WalletRepository(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    public CompletableFuture<Wallet> fetchByKeyAsync(UUID key) {
        return CompletableFuture.supplyAsync(() -> fetchByKey(key));
    }

    public Wallet fetchByKey(UUID key) {
        try(PreparedStatement preparedStatement = economyPlugin.mySqlStorage.getConnection().prepareStatement(SELECT_QUERY)) {
            preparedStatement.setString(1, key.toString());
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
    }

    public CompletableFuture<List<Wallet>> fetchAllAsync() {
        return CompletableFuture.supplyAsync(() -> fetchAll());
    }

    public List<Wallet> fetchAll() {
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
    }

    public CompletableFuture<Boolean> updateWalletValueAsync(UUID key, BigDecimal value) {
        return CompletableFuture.supplyAsync(() -> updateWalletValue(key, value));
    }

    public Boolean updateWalletValue(UUID key, BigDecimal value) {
        try(PreparedStatement preparedStatement = economyPlugin.mySqlStorage.getConnection().prepareStatement(UPDATE_WALLET_VALUE_QUERY)) {
            preparedStatement.setObject(1, value);
            preparedStatement.setObject(2, key.toString());
            preparedStatement.executeUpdate();
            return true;
        } catch(Exception e) {
            economyPlugin.getLogger().log(Level.WARNING,
                "Alguma coisa deu errada na query");
            return false;
        }
    }

    public CompletableFuture<Wallet> saveWalletAsync(Wallet wallet) {
        return CompletableFuture.supplyAsync(() -> saveWallet(wallet));
    }

    public Wallet saveWallet(Wallet wallet) {
        try(PreparedStatement preparedStatement = economyPlugin.mySqlStorage.getConnection().prepareStatement(UPDATE_WALLET_VALUE_QUERY)) {
            preparedStatement.setObject(1, wallet.getUuid().toString());
            preparedStatement.setObject(2, wallet.getBalance());
            preparedStatement.executeUpdate();
            return wallet;
        } catch(Exception e) {
            economyPlugin.getLogger().log(Level.WARNING,
                "Alguma coisa deu errada na query");
        }
        return null;
    }
}
