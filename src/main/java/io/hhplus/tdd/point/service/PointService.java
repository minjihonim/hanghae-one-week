package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.UserPoint;

public interface PointService {

    /**
     * 유저 포인트 충전
     * @param id
     * @param amount
     * @return
     */
    UserPoint chargeUserPoint(long id, long amount);
    
    /**
     * 유저의 포인트 정보 조회
     * @param id
     * @return
     */
    UserPoint getUserPoint(long id);

    /**
     * 유저 포인트 사용
     * @param id
     * @param amount
     * @return
     */
    UserPoint useUserPoint(long id, long amount);
}
