package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;

import java.util.List;

public interface PointService {

    /**
     * 유저 포인트 충전
     * @param id
     * @param amount
     * @return
     */
    UserPoint chargeUserPoint(long id, long amount) throws Exception;

    /**
     * 유저 포인트 충전 ( 동시성 제어, 멀티 쓰레드 환경 )
     * @param id
     * @param amount
     * @return
     */
    UserPoint synchronizedChargeUserPoint(long id, long amount) throws Exception;

    /**
     * 유저의 포인트 정보 조회
     * @param id
     * @return
     */
    UserPoint getUserPoint(long id) throws Exception;

    /**
     * 유저 포인트 사용
     * @param id
     * @param amount
     * @return
     */
    UserPoint useUserPoint(long id, long amount) throws Exception ;

    /**
     * 유저 포인트 사용 ( 동시성 제어, 멀티 쓰레드 환경 )
     * @param id
     * @param amount
     * @return
     */
    UserPoint synchronizedUseUserPoint(long id, long amount) throws Exception ;

    /**
     * 포인트 충전/사용 내역 조회
     * @param id
     * @return
     */
    List<PointHistory> getHistory(long id) throws Exception;
}
