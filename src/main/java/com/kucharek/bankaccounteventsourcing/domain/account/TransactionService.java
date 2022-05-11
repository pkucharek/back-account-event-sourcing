package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.control.Either;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    Either<AccountHolderCommandError, TransactionAddingResult>
        performTransaction(NewTransactionDTO transaction);

    ResponseEntity<?> findTransactionsByAccountId(AccountHolderId accountHolderId);
}
