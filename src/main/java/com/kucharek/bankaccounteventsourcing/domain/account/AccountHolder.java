package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.collection.List;
import io.vavr.control.Either;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

class AccountHolder {

    private AccountHolderId id;
    private Integer currentMoney;

    private AccountHolder(AccountHolderId id, Integer amount) {
        this.id = id;
        this.currentMoney = amount;
    }

    private AccountHolder() { }

    public static AccountHolder createEmpty() {
        return new AccountHolder();
    }

    AccountHolderId getId() {
        return id;
    }

    Either<AccountHolderCommandError, List<AccountHolderEvent>> process(AccountHolderCommand command) {
        return switch (command) {
            case CreateAccount createAccount -> handleAccountCreation(createAccount);
            case AddMoney addMoney -> handleMoneyReceive(addMoney);
            case ChargeAccount chargeAccount -> handleAccountCharge(chargeAccount);
        };
    }

    private Either<AccountHolderCommandError, List<AccountHolderEvent>> handleAccountCreation(CreateAccount createAccount) {
        return right(List.of(new AccountCreated(createAccount.id(), 0)));
    }

    private Either<AccountHolderCommandError, List<AccountHolderEvent>> handleMoneyReceive(AddMoney addMoney) {
        return right(List.of(new MoneyReceived(addMoney.from(), addMoney.amount())));
    }

    private Either<AccountHolderCommandError, List<AccountHolderEvent>> handleAccountCharge(ChargeAccount chargeAccount) {
        if (currentMoney - chargeAccount.amount() <= 0) {
            return left(AccountHolderCommandError.INSUFFICIENT_FUNDS);
        }
        return right(List.of(new AccountCharged(chargeAccount.by(), chargeAccount.amount())));
    }

    AccountHolder apply(AccountHolderEvent event) {
        return switch (event) {
            case AccountCreated accountCreated -> applyAccountCreated(accountCreated);
            case MoneyReceived moneyReceived -> applyMoneyReceived(moneyReceived);
            case AccountCharged accountCharged -> applyAccountCharged(accountCharged);
        };
    }

    private AccountHolder applyAccountCreated(AccountCreated accountCreated) {
        return new AccountHolder(accountCreated.id(), accountCreated.initialAmount());
    }

    private AccountHolder applyMoneyReceived(MoneyReceived moneyReceived) {
        return new AccountHolder(id, currentMoney + moneyReceived.amount());
    }

    private AccountHolder applyAccountCharged(AccountCharged accountCharged) {
        return new AccountHolder(id, currentMoney - accountCharged.amount());
    }

}
