package org.economy.repository;

import org.bukkit.Bukkit;
import org.economy.EconomyPlugin;
import org.economy.model.Wallet;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class WalletRepository {

    EconomyPlugin economyPlugin;

    private static final String SELECT_QUERY = "SELECT * FROM WALLET WHERE UUID = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM WALLET";
    private static final String UPDATE_WALLET_VALUE_QUERY = "UPDATE WALLET SET BALANCE = ? WHERE uuid = ?";
    private static final String INSERT_WALLET_QUERY = "REPLACE INTO WALLET VALUES(?, ?)";
    private static final String SELECT_TOP_TEN_QUERY = "SELECT * FROM WALLET ORDER BY BALANCE DESC LIMIT 10";

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

    public CompletableFuture<Boolean> updateWalletAsync(UUID key, BigDecimal value) {
        return CompletableFuture.supplyAsync(() -> updateWallet(key, value));
    }

    public Boolean updateWallet(UUID key, BigDecimal value) {
        try(PreparedStatement preparedStatement = economyPlugin.mySqlStorage.getConnection().prepareStatement(UPDATE_WALLET_VALUE_QUERY)) {
            preparedStatement.setObject(1, value);
            preparedStatement.setObject(2, key.toString());
            preparedStatement.executeUpdate();
            return true;
        } catch(Exception e) {
            economyPlugin.getLogger().log(Level.WARNING,
                "Alguma coisa deu errada na query");
        }
        return false;
    }

    public CompletableFuture<Boolean> saveWalletAsync(Wallet wallet) {
        return CompletableFuture.supplyAsync(() -> saveWallet(wallet));
    }

    public Boolean saveWallet(Wallet wallet) {
        try(PreparedStatement preparedStatement = economyPlugin.mySqlStorage.getConnection().prepareStatement(INSERT_WALLET_QUERY)) {
            preparedStatement.setObject(1, wallet.getUuid().toString());
            preparedStatement.setObject(2, wallet.getBalance());
            preparedStatement.executeUpdate();
            return true;
        } catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE,
                "Alguma coisa deu errada na query", e);
        }
        return false;
    }

    public CompletableFuture<List<Wallet>> fetchTopTenAsync() {
        return CompletableFuture.supplyAsync(() -> fetchTopTen());
    }

    public List<Wallet> fetchTopTen() {
        try(PreparedStatement preparedStatement = economyPlugin.mySqlStorage.getConnection().prepareStatement(SELECT_TOP_TEN_QUERY)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Wallet> walletsTopTen = new LinkedList<>();
                while(resultSet.next()) {
                    walletsTopTen.add(Wallet.parseFromResultSet(resultSet));
                }
                return walletsTopTen;
            }
        } catch (Exception e) {
            economyPlugin.getLogger().log(Level.WARNING,
                "Alguma coisa deu errada na query");
        }
        return null;
    }
}
