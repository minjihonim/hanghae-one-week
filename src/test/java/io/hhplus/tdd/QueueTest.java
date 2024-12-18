package io.hhplus.tdd;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class QueueTest {

    /**
     * 멀티쓰레드 환경 Queue Test
     * @throws Exception
     */
    @Test
    public void linked_queue_test() throws Exception {
        long id = 1;    // userId
        long amount = 10;   // 충전 포인트

        int threadCount = 3; // 동시에 실행할 스레드 개수
        int iterations = 3; // 각 스레드가 수행할 작업 횟수

        Queue<UserPoint> queue = new LinkedList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i=0; i<threadCount; i++) {
//            if(i > 0) {
//                id = id+1;
//            }
            executorService.submit(() -> {
                for (int j = 0; j < iterations; j++){
                    try {
//                        String threadName = Thread.currentThread().getName();
//                        System.out.println("작업 스레드 이름: " + threadName);
                        UserPoint user = new UserPoint(id, amount, System.currentTimeMillis());
                        queue.add(user);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Thread.sleep(10); // 콘솔에 출력 시간을 주기 위해 0.01초 일시 정지시킴
        }

        // 모든 작업 완료 대기
        executorService.shutdown();
        boolean completed = executorService.awaitTermination(5, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Test timed out");
        }


        // 기대값은 threadCount * iterations, 하지만 Race Condition 때문에 달라질 가능성 있음
        long expectedPoint = amount * iterations * threadCount;
        long actualPoint = 0L;
        for(UserPoint user : queue) {
            actualPoint += user.getPoint();
        }
        System.out.println("expectedPoint=" + expectedPoint);
        System.out.println("actualPoint=" + actualPoint);

        // Race Condition 이 없는 경우만 성공
        assertEquals(expectedPoint, actualPoint);

//        System.out.println("Queue: " + queue.size());
//        long id1_sum = 0L;
//        long id2_sum = 0L;
//        long id3_sum = 0L;
//        for(UserPoint user : queue) {
//            if(user.getId() == 1) {
//                id1_sum += user.getPoint();
//            }
//            if(user.getId() == 2) {
//                id2_sum += user.getPoint();
//            }
//            if(user.getId() == 3) {
//                id3_sum += user.getPoint();
//            }
//        }
//        System.out.println("id1_sum="+ id1_sum);
//        System.out.println("id2_sum="+ id2_sum);
//        System.out.println("id3_sum="+ id3_sum);
//        for(UserPoint user : queue) {
//            System.out.println(user);
//        }

    }
}
