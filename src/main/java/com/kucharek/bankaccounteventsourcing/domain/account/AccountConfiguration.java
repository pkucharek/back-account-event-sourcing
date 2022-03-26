package com.kucharek.bankaccounteventsourcing.domain.account;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AccountConfiguration {

    @Bean
    TransactionService accountService() {
        return new TransactionService();
    }
}
