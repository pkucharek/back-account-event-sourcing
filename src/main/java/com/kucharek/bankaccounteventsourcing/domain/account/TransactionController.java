package com.kucharek.bankaccounteventsourcing.domain.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/transactions")
class TransactionController {

    private final TransactionService transactionService;

    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    ResponseEntity<Object> addTransaction(NewTransactionDTO newTransactionDTO) {
        return transactionService
                .performTransaction(newTransactionDTO)
                .map(result -> ResponseEntity.ok().build())
                .getOrElseGet(error -> ResponseEntity.badRequest().body(error.toString()));
    }

}
