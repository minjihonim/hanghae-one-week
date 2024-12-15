package io.hhplus.tdd.point;

import lombok.Getter;

public record UserPoint(
        @Getter
        long id,
        @Getter
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
