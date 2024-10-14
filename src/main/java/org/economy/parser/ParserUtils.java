package org.economy.parser;

import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.logging.Level;

public class ParserUtils {

    public static BigDecimal parseToBigDecimal(String arg) {
        try {
            double parseToDouble = Double.parseDouble(arg);
            return BigDecimal.valueOf(parseToDouble);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().log(Level.WARNING,
                "Ocorreu um erro ao dar um parse ai");
        }
        return null;
    }
}
