package org.economy.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Wallet {
    private UUID uuid;
    private BigDecimal balance;
}
