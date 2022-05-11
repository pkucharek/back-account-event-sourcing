package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.http.ResponseEntity;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

class TransactionServiceFunctionalImpl implements TransactionService {

    AccountService accountService;

    @Override
    public Either<AccountHolderCommandError, TransactionAddingResult>
        performTransaction(NewTransactionDTO transaction) {

        return accountService
            .findById(transaction.from())
            .map(fromAccountHolder -> accountService
                .findById(transaction.to())
                .map(toAccountHolder -> fromAccountHolder
                    .process(new ChargeAccount(fromAccountHolder.getId(), transaction.amount()))
                    .map(chargeAccountResult -> toAccountHolder
                        .process(new AddMoney(toAccountHolder.getId(), transaction.amount()))
                        .map(addMoneyResult -> {
                            accountService.saveEvents(Stream.concat(chargeAccountResult, addMoneyResult).toList());
                            return Either.<AccountHolderCommandError, TransactionAddingResult>right(TransactionAddingResult.TRANSACTION_ADDED);
                        })
                        .getOrElseGet(Either::left))
                    .getOrElseGet(Either::left))
                .getOrElse(() -> left(AccountHolderCommandError.FROM_NOT_FOUND)))
            .getOrElse(() -> left(AccountHolderCommandError.FROM_NOT_FOUND));
    }

    private Either<AccountHolderCommandError, Tuple2<AccountHolder, AccountHolder>>
        retrieveHolders(NewTransactionDTO transaction) {
        return tupleIfBothPresentOrElse(
            accountService.findById(transaction.from()),
            accountService.findById(transaction.to()),
            left(AccountHolderCommandError.FROM_NOT_FOUND)
        );
    }

    private <T, E> Either<E, Tuple2<T, T>>
        tupleIfBothPresentOrElse(
            Option<T> first,
            Option<T> second,
            Either<E, Tuple2<T, T>> defaultLeft
    ) {
        if (first.isDefined() && second.isDefined()) {
            return right(Tuple.of(first.get(), second.get()));
        }
        return defaultLeft;
    }


    @Override
    public ResponseEntity<?> findTransactionsByAccountId(AccountHolderId accountHolderId) {
        return null;
    }
}
