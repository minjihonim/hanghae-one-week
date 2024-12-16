package io.hhplus.tdd.point.service.impl;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointLimitType;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointServiceImpl implements PointService {

    private final UserPointTable userPointTable;

    private final PointHistoryTable pointHistoryTable;

    public PointServiceImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public UserPoint chargeUserPoint(long id, long amount) throws Exception {

        // 최대포인트 지정 ( 1백만 )
        if(amount > PointLimitType.MAX_POINT) {
            throw new RuntimeException("최대 충전포인트는 백만포인트 입니다.");
        }

        // 유저 정보 가져오기
        UserPoint userInfo = userPointTable.selectById(id);

        // 응답 객체
        UserPoint result = userPointTable.insertOrUpdate(userInfo.getId(), userInfo.getPoint() + amount);

        // 포인트 히스토리 저장
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE,System.currentTimeMillis());

        return result;
    }

    @Override
    public UserPoint getUserPoint(long id) throws Exception {
        return userPointTable.selectById(id);
    }

    @Override
    public UserPoint useUserPoint(long id, long amount) throws Exception {

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

    @Override
    public List<PointHistory> getHistory(long id) throws Exception {

        // 유저의 포인트 사용내역 조회
        List<PointHistory> result = pointHistoryTable.selectAllByUserId(id);

        return result;
    }
}
