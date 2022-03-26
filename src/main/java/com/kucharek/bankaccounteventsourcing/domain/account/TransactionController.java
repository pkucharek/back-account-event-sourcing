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
    ResponseEntity<Void> addTransaction(NewTransactionDTO newTransactionDTO) {
        transactionService.performTransaction(newTransactionDTO);
    }

}
