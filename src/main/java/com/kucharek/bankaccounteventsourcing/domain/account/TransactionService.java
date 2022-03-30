package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.http.ResponseEntity;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

class TransactionService {

    AccountService accountService;

    Either<AccountHolderCommandError, TransactionAddingResult>
        performTransaction(NewTransactionDTO transaction) {

//        Option<AccountHolder> id = accountService.findById(transaction.id());
//        Option<AccountHolder> to = accountService.findById(transaction.to());
//        if (id.isEmpty()) {
//            return left(AccountHolderCommandError.FROM_NOT_FOUND);
//        }
//        if (to.isEmpty()) {
//            return left(AccountHolderCommandError.TO_NOT_FOUND);
//        }

//        return accountService.findById(transaction.id()).map(fromAccountHolder -> {
//            return accountService.findById(transaction.to()).map(toAccountHolder -> {
//                return fromAccountHolder.process(new ChargeAccount(fromAccountHolder.getId(), transaction.amount()))
//                    .map(chargeAccountResult -> {
//                        return toAccountHolder.process(new AddMoney(toAccountHolder.getId(), transaction.amount()))
//                            .map(addMoneyResult -> {
//                                accountService.saveEvents(Stream.concat(chargeAccountResult, addMoneyResult).toList());
//                                return right(TransactionAddingResult.TRANSACTION_ADDED);
//                            }).getLeft();
//                    }).getLeft();
//            }).getOrElse(left(AccountHolderCommandError.TO_NOT_FOUND));
//        }).getOrElse(left(AccountHolderCommandError.FROM_NOT_FOUND));

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

//        AccountHolder fromAccountHolder = id.get();
//        Either<AccountHolderCommandError, List<AccountHolderEvent>> chargeAccountResult
//            = fromAccountHolder.process(new ChargeAccount(fromAccountHolder.getId(), transaction.amount()));
//        if (chargeAccountResult.isLeft()) {
//            return left(chargeAccountResult.getLeft());
//        }
////        chargeAccountResult.get().forEach(fromAccountHolder::apply);
//
//        AccountHolder toAccountHolder = id.get();
//        Either<AccountHolderCommandError, List<AccountHolderEvent>> addMoneyResult
//            = toAccountHolder.process(new AddMoney(toAccountHolder.getId(), transaction.amount()));
//        if (chargeAccountResult.isLeft()) {
//            return left(addMoneyResult.getLeft());
//        }
////        addMoneyResult.get().forEach(toAccountHolder::apply);
//
//        accountService.saveEvents(Stream.concat(chargeAccountResult.get(), addMoneyResult.get()).toList());
//
//        return right(TransactionAddingResult.TRANSACTION_ADDED);
    }

    ResponseEntity<?> findTransactionsByAccountId(AccountHolderId accountHolderId) {
        return null;
    }
}
