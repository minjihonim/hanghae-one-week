package io.hhplus.tdd;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.impl.PointServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * 동시성 제어 통합 테스트
 */
@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private PointServiceImpl pointService;

    @Test
    @DisplayName("포인트 충전 동시성 테스트")
    public void 동일_사용자_포인트_충전_동시성_제어_테스트_통과() throws Exception {
        long id = 1;    // userId
        long amount = 10;   // 충전 포인트
        int threadCount = 3; // 동시에 실행할 스레드 개수
        int iterations = 3; // 각 스레드가 수행할 작업 횟수

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);    // 멀티쓰레드 생성

        // 포인트 충전 큐 add
        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < iterations; j++){
                    try {
                        pointService.chargeUserPoint(id, amount, System.currentTimeMillis());   // 포인트 충전 기능
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        // 포인트 충전 큐 poll()
        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                // 충전 로직 큐 수행
                try {
                    pointService.processChargeQueueAutomatically();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // 모든 작업 완료 대기
        executorService.shutdown();
        boolean completed = executorService.awaitTermination(20, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Test timed out");
        }

        // 기대값은 threadCount * iterations, 하지만 Race Condition 때문에 달라질 가능성 있음
        long expectedPoint = amount * iterations * threadCount;
        UserPoint userInfo = pointService.getUserPoint(id);
        long actualPoint = userInfo.getPoint();

        // Race Condition 이 없는 경우만 성공
        assertEquals(expectedPoint, actualPoint);

    }

    @Test
    @DisplayName("포인트 충전 동시성 테스트")
    public void 동일_사용자_포인트_충전_동시성_이슈_발생_테스트() throws Exception {
        long id = 1;    // userId
        long amount = 10;   // 충전 포인트
        int threadCount = 3; // 동시에 실행할 스레드 개수
        int iterations = 3; // 각 스레드가 수행할 작업 횟수

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);    // 멀티쓰레드 생성

        // 포인트 충전 큐 add
        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < iterations; j++){
                    try {
                        pointService.failChargeUserPoint(id, amount, System.currentTimeMillis());   // 포인트 충전 기능
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        // 모든 작업 완료 대기
        executorService.shutdown();
        boolean completed = executorService.awaitTermination(20, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Test timed out");
        }

        // 기대값은 threadCount * iterations, 하지만 Race Condition 때문에 달라질 가능성 있음
        long expectedPoint = amount * iterations * threadCount;
        UserPoint userInfo = pointService.getUserPoint(id);
        long actualPoint = userInfo.getPoint();

        // Race Condition 이 없는 경우만 성공
        assertNotEquals(expectedPoint, actualPoint);
    }

    @Test
    @DisplayName("포인트 충전 동시성 테스트")
    public void 여러_사용자_포인트_충전_동시성_제어_테스트_통과() throws Exception {
        long id = 1;    // userId
        long amount = 10;   // 충전 포인트
        int threadCount = 3; // 동시에 실행할 스레드 개수
        int iterations = 3; // 각 스레드가 수행할 작업 횟수

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);    // 멀티쓰레드 생성

        // 포인트 충전 큐 add
        for (int i=0; i<threadCount; i++) {
            final long userId = i+id;
            executorService.submit(() -> {
                for (int j = 0; j < iterations; j++){
                    try {
                        pointService.chargeUserPoint(userId, amount, System.currentTimeMillis());   // 포인트 충전 기능
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        // 모든 작업 완료 대기
        executorService.shutdown();
        boolean completed = executorService.awaitTermination(20, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Test timed out");
        }

        // 포인트 충전 큐 poll
        pointService.processChargeQueueAutomatically();

        // 기대값은 threadCount * iterations, 하지만 Race Condition 때문에 달라질 가능성 있음
        for(int i=0; i<threadCount; i++) {
            final long userId = i+id;
            long expectedPoint = amount * iterations;
            UserPoint userInfo = pointService.getUserPoint(userId);
            long actualPoint = userInfo.getPoint();
            // Race Condition 이 없는 경우만 성공
            assertEquals(expectedPoint, actualPoint);
        }

    }


    @Test
    @DisplayName("포인트 사용 동시성 테스트")
    public void 동일_사용자_포인트_사용_동시성_제어_테스트_통과() throws Exception {
        long id = 1;    // userId
        long amount = 10;   // 사용 포인트
        long chargePoint = 100;     // 충전 포인트
        long currentTime = System.currentTimeMillis();

        // 포인트 사용을 위한 포인트 충전
        pointService.chargeUserPoint(id, chargePoint, currentTime);
        pointService.processChargeQueueAutomatically();

        int threadCount = 3; // 동시에 실행할 스레드 개수
        int iterations = 3; // 각 스레드가 수행할 작업 횟수

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // 포인트 충전 큐 add
        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < iterations; j++){
                    try {
                        pointService.useUserPoint(id, amount, currentTime);   // 포인트 사용 기능
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        // 포인트 충전 큐 poll()
        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                // 소비 로직 큐 수행
                try {
                    pointService.processUseQueueAutomatically();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // 모든 작업 완료 대기
        executorService.shutdown();
        boolean completed = executorService.awaitTermination(20, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Test timed out");
        }


        // 기대값은 threadCount * iterations, 하지만 Race Condition 때문에 달라질 가능성 있음
        long expectedPoint = chargePoint - (amount * iterations * threadCount); // 100 - 90
        UserPoint userInfo = pointService.getUserPoint(id);
        long actualPoint = userInfo.getPoint();

        // Race Condition 이 없는 경우만 성공
        assertEquals(expectedPoint, actualPoint);

    }
}
