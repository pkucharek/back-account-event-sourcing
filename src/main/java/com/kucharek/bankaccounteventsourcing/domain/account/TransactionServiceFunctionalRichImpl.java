package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.http.ResponseEntity;

import java.util.function.BiFunction;

import static io.vavr.control.Either.left;

class TransactionServiceFunctionalRichImpl implements TransactionService {

    AccountService accountService;

    @Override
    public Either<AccountHolderCommandError, TransactionAddingResult>
         performTransaction(NewTransactionDTO transaction) {
        return computeIfBothPresentOrElse(
            accountService.findById(transaction.from()),
            accountService.findById(transaction.to()),
            (fromAccountHolder, toAccountHolder) -> {
                return computeIfBothRightOrElseLeft(
                    fromAccountHolder.process(new ChargeAccount(
                        fromAccountHolder.getId(),
                        transaction.amount()
                    )),
                    toAccountHolder.process(new AddMoney(
                        toAccountHolder.getId(), transaction.amount()
                    )),
                    (chargeAccountResult, addMoneyResult) -> {
                        accountService.saveEvents(
                            Stream.concat(chargeAccountResult, addMoneyResult).toList()
                        );
                        return Either.<AccountHolderCommandError, TransactionAddingResult>right(TransactionAddingResult.TRANSACTION_ADDED);
                    });
            },
            left(AccountHolderCommandError.FROM_NOT_FOUND)
        );
    }

    private <T, E, R> Either<E, R> computeIfBothPresentOrElse(
        Option<T> first,
        Option<T> second,
        BiFunction<T, T, Either<E, R>> action,
        Either<E, R> defaultLeft
    ) {
        if (first.isDefined() && second.isDefined()) {
            return action.apply(first.get(), second.get());
        }
        return defaultLeft;
    }

    private <E, R, T> Either<E, R> computeIfBothRightOrElseLeft(
        Either<E, R> first,
        Either<E, R> second,
        BiFunction<R, R, Either<E, T>> action
    ) {
        if (first.isLeft()) {
            return first;
        }
        if (second.isLeft()) {
            return second;
        }
        Either<E, T> result = first.flatMap(f ->
            second.flatMap(s ->
                action.apply(f, s)
            )
        );
        return result;
    }

    @Override
    public ResponseEntity<?> findTransactionsByAccountId(AccountHolderId accountHolderId) {
        return null;
    }
}
