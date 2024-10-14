package org.economy.repository;

import org.economy.EconomyPlugin;
import org.economy.datautils.Repository;
import org.economy.model.Wallet;
import org.economy.parser.ParserUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class WalletRepository implements Repository<UUID, Wallet> {

    EconomyPlugin economyPlugin;
    private static final String SELECT_QUERY = "SELECT * FROM WALLET WHERE UUID = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM WALLET";

    public WalletRepository(EconomyPlugin economyPlugin) {
        this.economyPlugin = economyPlugin;
    }

    @Override
    public Collection<Wallet> fetchAll() {
        return List.of();
    }

    @Override
    public Wallet fetchByKey(UUID key) {
        try {
            return this.completableFutureFetchByKey(key).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Wallet saveData(UUID key, Wallet data) {
        return null;
    }

    @Override
    public Wallet removeData(UUID key) {
        return null;
    }

    private CompletableFuture<Wallet> completableFutureFetchByKey(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            try(PreparedStatement statement = economyPlugin.mySqlStorage.getConnection().prepareStatement(SELECT_QUERY)) {
                statement.setString(1, id.toString());
                try(ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next()) {
                        Wallet wallet = new Wallet();
                        wallet.setUuid(UUID.fromString(resultSet.getString(1)));
                        wallet.setBalance(ParserUtils.parseToBigDecimal(resultSet.getString(2)));
                        return wallet;
                    }
                    return null;
                }
            } catch (Exception e) {
                economyPlugin.getLogger().log(Level.WARNING,
                    "Alguma coisa deu errada na query");
            }
            return null;
        });
    }
}
