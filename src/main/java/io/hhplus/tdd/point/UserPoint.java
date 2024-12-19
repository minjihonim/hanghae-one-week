package io.hhplus.tdd.point;

import lombok.Getter;
import lombok.Setter;

public record UserPoint(
        @Getter
        long id,
        @Getter
        @Setter
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
