package io.hhplus.tdd.point;

import lombok.Getter;

public record PointHistory(
        long id,
        @Getter
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
