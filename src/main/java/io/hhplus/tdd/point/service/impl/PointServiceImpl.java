package io.hhplus.tdd.point.service.impl;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointLimitType;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.springframework.stereotype.Service;

@Service
public class PointServiceImpl implements PointService {

    private final UserPointTable userPointTable;

    private final PointHistoryTable pointHistoryTable;

    public PointServiceImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public UserPoint chargeUserPoint(long id, long amount) {
        // 응답 객체
        UserPoint result = new UserPoint(id, amount, System.currentTimeMillis());

        // 최대포인트 지정 ( 1백만 )
        if(amount > PointLimitType.MAX_POINT) {
            throw new RuntimeException("최대 충전포인트는 백만포인트 입니다.");
        }

        // 유저 존재여부 확인
        UserPoint userInfo = userPointTable.selectById(id);
        if(userInfo == null) {
            // 포인트 저장
            result = userPointTable.insertOrUpdate(id, amount);
        } else {
            // 충전포인트 재지정
            long chargePoint = userInfo.getPoint() + amount;
            // 포인트 저장
            result = userPointTable.insertOrUpdate(id, chargePoint);
        }
        // 포인트 히스토리 저장
        PointHistory pointHistory =
                pointHistoryTable.insert(id, amount, TransactionType.CHARGE,System.currentTimeMillis());
        if(pointHistory.getUserId() == id) {
            return result;
        }

        throw new RuntimeException("포인트 충전 실패");
    }

    @Override
    public UserPoint getUserPoint(long id) {
        // 유저가 존재하는지 확인한다.
        try {
            // 존재하면 유저의 포인트를 리턴
            UserPoint result = userPointTable.selectById(id);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserPoint useUserPoint(long id, long amount) {

        // 유저의 포인트를 조회
        UserPoint userPoint = userPointTable.selectById(id);
        // 보유 포인트 보다 사용 포인트가 큼으로 실패처리
        if(userPoint.getPoint() < amount) {
            throw new RuntimeException("보유 포인트가 부족합니다.");
        }
        // 포인트 사용처리
        long beforePoint = userPoint.getPoint() - amount;
        UserPoint result = userPointTable.insertOrUpdate(id, beforePoint);
        // 포인트 히스토리 저장
        PointHistory pointHistory =
                pointHistoryTable.insert(id, amount, TransactionType.USE ,System.currentTimeMillis());

        if(pointHistory.getUserId() == id) {
            return result;
        }

        throw new RuntimeException("포인트 사용 실패");
    }
}
