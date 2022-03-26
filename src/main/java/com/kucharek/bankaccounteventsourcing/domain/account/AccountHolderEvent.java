package com.kucharek.bankaccounteventsourcing.domain.account;

import java.io.Serializable;

sealed interface AccountHolderEvent
    extends Serializable
    permits MoneyReceived, AccountCharged, AccountCreated {
}

record MoneyReceived(AccountHolderId from, Integer amount) implements AccountHolderEvent {}
record AccountCharged(AccountHolderId by, Integer amount) implements AccountHolderEvent {}
record AccountCreated(AccountHolderId id, Integer initialAmount) implements AccountHolderEvent {}
