package org.economy.database;

public record SqlCredentials(
         String host,
         String password,
         String database,
         String user
) {
}
