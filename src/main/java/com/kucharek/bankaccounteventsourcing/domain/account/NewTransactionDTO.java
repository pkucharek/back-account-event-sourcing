package com.kucharek.bankaccounteventsourcing.domain.account;

public record NewTransactionDTO(AccountHolderId from, AccountHolderId to, Integer amount) {
}
