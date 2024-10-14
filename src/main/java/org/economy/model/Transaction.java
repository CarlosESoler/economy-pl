package org.economy.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {
    UUID uuid;
    UUID playerSender;
    UUID playerReceiver;
    Wallet playerSenderWallet;
    Wallet playerReceiverWallet;
}
