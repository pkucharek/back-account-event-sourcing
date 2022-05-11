package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.http.ResponseEntity;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public class TransactionServiceStandardImpl implements TransactionService {
    private final AccountService accountService;

    public TransactionServiceStandardImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Either<AccountHolderCommandError, TransactionAddingResult> performTransaction(NewTransactionDTO transaction) {
        Option<AccountHolder> from = accountService.findById(transaction.from());
        Option<AccountHolder> to = accountService.findById(transaction.to());
        if (from.isEmpty()) {
            return left(AccountHolderCommandError.FROM_NOT_FOUND);
        }
        if (to.isEmpty()) {
            return left(AccountHolderCommandError.TO_NOT_FOUND);
        }

        AccountHolder fromAccountHolder = from.get();
        Either<AccountHolderCommandError, List<AccountHolderEvent>> chargeAccountResult
            = fromAccountHolder.process(new ChargeAccount(fromAccountHolder.getId(), transaction.amount()));
        if (chargeAccountResult.isLeft()) {
            return left(chargeAccountResult.getLeft());
        }
        chargeAccountResult.get().forEach(fromAccountHolder::apply);

        AccountHolder toAccountHolder = from.get();
        Either<AccountHolderCommandError, List<AccountHolderEvent>> addMoneyResult
            = toAccountHolder.process(new AddMoney(toAccountHolder.getId(), transaction.amount()));
        if (chargeAccountResult.isLeft()) {
            return left(addMoneyResult.getLeft());
        }
        addMoneyResult.get().forEach(toAccountHolder::apply);

        accountService.saveEvents(Stream.concat(chargeAccountResult.get(), addMoneyResult.get()).toList());

        return right(TransactionAddingResult.TRANSACTION_ADDED);
    }

    @Override
    public ResponseEntity<?> findTransactionsByAccountId(AccountHolderId accountHolderId) {
        return null;
    }
}
