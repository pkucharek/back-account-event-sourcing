package com.kucharek.bankaccounteventsourcing.domain.account;

import java.io.Serializable;

sealed interface AccountHolderEvent
    extends Serializable
    permits MoneyReceived, AccountCharged, AccountCreated {
    AccountHolderId id();
}

record MoneyReceived(AccountHolderId id, Integer amount) implements AccountHolderEvent {}
record AccountCharged(AccountHolderId id, Integer amount) implements AccountHolderEvent {}
record AccountCreated(AccountHolderId id, Integer initialAmount) implements AccountHolderEvent {}
