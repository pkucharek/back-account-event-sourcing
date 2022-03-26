package com.kucharek.bankaccounteventsourcing.domain.account;

import java.io.Serializable;

sealed interface AccountHolderCommand
    extends Serializable
    permits AddMoney, ChargeAccount, CreateAccount {
}

record CreateAccount(AccountHolderId id) implements AccountHolderCommand {
}

record AddMoney(AccountHolderId from, Integer amount) implements AccountHolderCommand {
}

record ChargeAccount(AccountHolderId by, Integer amount) implements AccountHolderCommand {
}
