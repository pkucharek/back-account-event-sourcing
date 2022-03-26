package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import java.util.Objects;

class AccountService {

    private final Map<AccountHolderId, List<AccountHolderEvent>> accountHoldersEventStore;

    AccountService(Map<AccountHolderId, List<AccountHolderEvent>> accountHoldersEventStore) {
        this.accountHoldersEventStore = accountHoldersEventStore;
    }

    Option<AccountHolder> findById(AccountHolderId fromId) {
        return accountHoldersEventStore
            .find(accountHolderIdListTuple -> accountHolderIdListTuple._1.equals(fromId))
            .map(tuple -> {
                AccountHolder accountHolder = AccountHolder.createEmpty();
                tuple._2.forEach(accountHolder::apply);
                return accountHolder;
            });
    }

    void saveEvents(List<AccountHolderEvent> events) {

    }
}
