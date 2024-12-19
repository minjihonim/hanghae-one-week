package io.hhplus.tdd.point.service.impl;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointLimitType;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class PointServiceImpl implements PointService {

    private final UserPointTable userPointTable;

    private final PointHistoryTable pointHistoryTable;

    public PointServiceImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    // 동시성 제어 포인트 충전 Queue
    private final Queue<UserPoint> chargePointQueue = new ConcurrentLinkedQueue<>();
    // 포인트 충전 Queue 작업 상태 boolean
    boolean chargePointQueueStatus = false;

    // 동시성 제어 포인트 소비(사용) Queue
    private final Queue<UserPoint> usePointQueue = new ConcurrentLinkedQueue<>();
    // 포인트 소비(사용) Queue 작업 상태 boolean
    boolean usePointQueueStatus = false;
    
    // 큐에 데이터

    @Override
    public UserPoint chargeUserPoint(long id, long amount, long currentTime) throws Exception {
        // 최대포인트 지정 ( 오십만 )
        if(amount > PointLimitType.MAX_POINT) {
            throw new RuntimeException("최대 충전포인트는 오십만 포인트입니다.");
        }

        // 유저 정보 가져오기
        UserPoint userInfo = userPointTable.selectById(id);

        // 포인트 충전 시 유저 보유 포인트가 100만이 초과될 수 없음
        if(userInfo.getPoint() + amount > PointLimitType.MAX_LIMIT_POINT) {
            throw new RuntimeException("최대 보유 포인트는 100만 까지 입니다.");
        }

        // 동시성 제어를 위해 Queue 를 사용, 포인트 충전 전용 Queue
        if(chargePointQueue.add(new UserPoint(id, amount, currentTime))) {
            return new UserPoint(id, userInfo.point() + amount, currentTime);
        }

        throw new RuntimeException("충전에 실패 하였습니다.");
    }

    // 매 1초마다 큐를 처리
    @Scheduled(fixedRate = 1000)
    public void processChargeQueueAutomatically() throws InterruptedException {
        if(chargePointQueueStatus) {
            return;
        }
        processChargeQueue();
    }

    private void processChargeQueue() throws InterruptedException {
        // 작업 중 상태로 변환
        chargePointQueueStatus = true;
        while(!chargePointQueue.isEmpty()) {
            // 충전 포인트 request info
            UserPoint chargePointInfo = chargePointQueue.poll();

            // 유저 정보 가져오기
            UserPoint userInfo = userPointTable.selectById(chargePointInfo.id());

            // 포인트 저장
            userPointTable.insertOrUpdate(userInfo.id(), userInfo.point() + chargePointInfo.point());

            // 포인트 히스토리 저장
            pointHistoryTable.insert(userInfo.id(), chargePointInfo.point(), TransactionType.CHARGE, chargePointInfo.updateMillis());
        }
        // 작업종료 상태로 변환
        chargePointQueueStatus = false;
    }

    @Override
    public UserPoint getUserPoint(long id) throws Exception {
        return userPointTable.selectById(id);
    }

    @Override
    public UserPoint useUserPoint(long id, long amount, long currentTime) throws Exception {

        // 유저의 포인트를 조회
        UserPoint userPoint = userPointTable.selectById(id);
        // 보유 포인트 보다 사용 포인트가 큼으로 실패처리
        if(userPoint.getPoint() < amount) {
            throw new RuntimeException("보유 포인트가 부족합니다.");
        }

        // 동시성 제어를 위해 Queue 를 사용, 포인트 충전 전용 Queue
        if(usePointQueue.add(new UserPoint(id, amount, currentTime))) {
            return new UserPoint(id, userPoint.point() - amount, currentTime);
        }

        throw new RuntimeException("포인트 사용에 실패 하였습니다.");
    }

    // 매 1초마다 큐를 처리
    @Scheduled(fixedRate = 1000)
    public void processUseQueueAutomatically() throws InterruptedException {
        if(usePointQueueStatus) {
            return;
        }
        processUseQueue();
    }

    private void processUseQueue() throws InterruptedException {
        // 작업 중 상태로 변환
        usePointQueueStatus = true;
        while(!usePointQueue.isEmpty()) {
            // 사용 포인트 request info
            UserPoint usePointInfo = usePointQueue.poll();

            // 유저 정보 가져오기
            UserPoint userInfo = userPointTable.selectById(usePointInfo.id());

            // 포인트 사용처리
            long beforePoint = userInfo.getPoint() - usePointInfo.point();
            userPointTable.insertOrUpdate(usePointInfo.id(), beforePoint);
            // 포인트 히스토리 저장
            pointHistoryTable.insert(usePointInfo.id(), usePointInfo.point(), TransactionType.USE , usePointInfo.updateMillis());
        }
        // 작업종료 상태로 변환
        usePointQueueStatus = false;
    }

    @Override
    public List<PointHistory> getHistory(long id) throws Exception {

        // 유저의 포인트 사용내역 조회
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public void failChargeUserPoint(long id, long amount, long currentTime) {
        // 최대포인트 지정 ( 오십만 )
        if(amount > PointLimitType.MAX_POINT) {
            throw new RuntimeException("최대 충전포인트는 오십만 포인트입니다.");
        }

        // 유저 정보 가져오기
        UserPoint userInfo = userPointTable.selectById(id);

        // 포인트 충전 시 유저 보유 포인트가 100만이 초과될 수 없음
        if(userInfo.getPoint() + amount > PointLimitType.MAX_LIMIT_POINT) {
            throw new RuntimeException("최대 보유 포인트는 100만 까지 입니다.");
        }

        // 포인트 저장
        userPointTable.insertOrUpdate(userInfo.id(), userInfo.point() + amount);

        // 포인트 히스토리 저장
        pointHistoryTable.insert(userInfo.id(), amount, TransactionType.CHARGE, currentTime);

        throw new RuntimeException("충전에 실패 하였습니다.");
    }
}
