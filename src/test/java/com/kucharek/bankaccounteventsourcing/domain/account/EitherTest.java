package com.kucharek.bankaccounteventsourcing.domain.account;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.assertj.core.api.Assertions.assertThat;

public class EitherTest {

    @Test
    void either_tests() {
        //given
        Either<String, Integer> either = left("12");

        //when
        Either<String, Integer> result = either
                .map(Either::<String, Integer>right)
                .getOrElseGet(Either::left);

        //then
//        assertThat(result.get()).isEqualTo(12);
        assertThat(result.getLeft()).isEqualTo("12");
    }
}
