package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

class TransactionService {

    AccountService accountService;

    Either<AccountHolderCommandError, TransactionAddingResult>
        performTransaction(NewTransactionDTO newTransactionDTO) {

        Option<AccountHolder> from = accountService.findById(newTransactionDTO.from());
        Option<AccountHolder> to = accountService.findById(newTransactionDTO.to());
        if (from.isEmpty()) {
            return left(AccountHolderCommandError.FROM_NOT_FOUND);
        }
        if (to.isEmpty()) {
            return left(AccountHolderCommandError.TO_NOT_FOUND);
        }

        return accountService.findById(newTransactionDTO.from()).map(fromAccountHolder -> {
            return accountService.findById(newTransactionDTO.to()).map(toAccountHolder -> {
                return fromAccountHolder.process(new ChargeAccount(fromAccountHolder.getId(), newTransactionDTO.amount()))
                    .map(chargeAccountResult -> {
                        return toAccountHolder.process(new AddMoney(toAccountHolder.getId(), newTransactionDTO.amount()))
                            .map(addMoneyResult -> {
                                accountService.saveEvents(Stream.concat(chargeAccountResult, addMoneyResult).toList());
                                return right(TransactionAddingResult.TRANSACTION_ADDED);
                            }).getLeft();
                    }).getLeft();
            }).getOrElse(left(AccountHolderCommandError.TO_NOT_FOUND));
        }).getOrElse(left(AccountHolderCommandError.FROM_NOT_FOUND));

        AccountHolder fromAccountHolder = from.get();
        Either<AccountHolderCommandError, List<AccountHolderEvent>> chargeAccountResult
            = fromAccountHolder.process(new ChargeAccount(fromAccountHolder.getId(), newTransactionDTO.amount()));
        if (chargeAccountResult.isLeft()) {
            return left(chargeAccountResult.getLeft());
        }
//        chargeAccountResult.get().forEach(fromAccountHolder::apply);

        AccountHolder toAccountHolder = from.get();
        Either<AccountHolderCommandError, List<AccountHolderEvent>> addMoneyResult
            = toAccountHolder.process(new AddMoney(toAccountHolder.getId(), newTransactionDTO.amount()));
        if (chargeAccountResult.isLeft()) {
            return left(addMoneyResult.getLeft());
        }
//        addMoneyResult.get().forEach(toAccountHolder::apply);

        accountService.saveEvents(Stream.concat(chargeAccountResult.get(), addMoneyResult.get()).toList());

        return right(TransactionAddingResult.TRANSACTION_ADDED);
    }
}
