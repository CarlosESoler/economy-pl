package org.economy.model;

import java.util.UUID;

public class Transaction {
    UUID uuid;
    UUID playerSender;
    UUID playerReceiver;
    String playerSenderUuidWallet;
    String playerReceiverUuidWallet;
}
