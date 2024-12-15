package io.hhplus.tdd.point.service.impl;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
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
        // 포인트 저장
        UserPoint result = userPointTable.insertOrUpdate(id, amount);
        // 포인트 히스토리 저장
        PointHistory affectedRows =
                pointHistoryTable.insert(id, amount, TransactionType.CHARGE,System.currentTimeMillis());
        if(affectedRows.getUserId() == id) {
            return result;
        }

        throw new RuntimeException("충전 실패");
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



        return null;
    }
}
