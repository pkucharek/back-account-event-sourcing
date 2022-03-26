package com.kucharek.bankaccounteventsourcing.domain.account;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.*;

@ExtendWith(SpringExtension.class)
public class TransactionIntegrationTest {

    @Test
    void creates_transaction() {
        //given
        NewTransactionDTO newTransactionDTO = new NewTransactionDTO(1, 2, 123);

        //when, then
            with().body(newTransactionDTO)
        .when()
            .post("/api/transactions")
        .then()
            .statusCode(200)
            .and();
    }
}
