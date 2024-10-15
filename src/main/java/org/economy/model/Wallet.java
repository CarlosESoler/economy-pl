package org.economy.model;

import lombok.Data;
import org.bukkit.Bukkit;
import org.economy.parser.ParserUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.logging.Level;

@Data
public class Wallet {
    private UUID uuid;
    private BigDecimal balance;

    public Wallet(UUID uuid, BigDecimal balance) {
        this.uuid = uuid;
        this.balance = balance;
    }

    public static Wallet parseFromResultSet(ResultSet resultSet) {
        try {
            return new Wallet(UUID.fromString(resultSet.getString(1)), ParserUtils.parseToBigDecimal(resultSet.getString(2)));
        } catch(Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Algo deu errado no parse");
            return null;
        }
    }
}
