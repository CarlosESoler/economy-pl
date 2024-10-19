package org.economy.database;
import lombok.Getter;
import org.bukkit.Bukkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public class MySqlStorage {

    private Connection connection;

    public boolean startConnection(SqlCredentials sqlCredentials) {
        final String url = "jdbc:mysql://" + sqlCredentials.host() + "/" + sqlCredentials.database();
        String password = !sqlCredentials.password().isEmpty() ? sqlCredentials.password() : null;

        try {
            connection = DriverManager.getConnection(url, sqlCredentials.user(), password);
            return true;
        } catch (SQLException exception) {
            Bukkit.getLogger().log(
                Level.WARNING,
                "Something bad happened on MySQL connection",
                exception
            );
            return false;
        }
    }

    public boolean hasConnection() {
        return connection != null;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}