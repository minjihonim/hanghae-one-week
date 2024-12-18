package io.hhplus.tdd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointLimitType;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.impl.PointServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointTest {

    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;
    @InjectMocks
    private PointServiceImpl pointService;

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_테스트_최대포인트_충전_실패() throws Exception {
        // given
        long id = 1L;
        long amount = PointLimitType.MAX_POINT+1L;
        long currentTime = System.currentTimeMillis();

        // when & then
        assertThrows(RuntimeException.class, () ->  pointService.chargeUserPoint(id, amount, currentTime));
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_최대_잔고_테스트() throws Exception {
        // 최대 잔고가 100만이 넘게될 경우 실패
        //given
        long id = 1L;
        long amount = PointLimitType.MAX_POINT;
        long currentTime = System.currentTimeMillis();

        when(userPointTable.selectById(id)).thenReturn(new UserPoint(id, 600_000L, currentTime));

        // when & then
        assertThrows(RuntimeException.class, () ->  pointService.chargeUserPoint(id, amount, currentTime));
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_테스트_성공() throws Exception {
        //given
        long id = 1L;
        long amount = PointLimitType.MAX_POINT;
        long currentTime = System.currentTimeMillis();

        // 유저의 포인트를 조회
        when(userPointTable.selectById(id)).thenReturn(new UserPoint(id, 0, currentTime));
        // 포인트 충전 처리
        when(userPointTable.insertOrUpdate(id, amount)).thenReturn(new UserPoint(id, amount, currentTime));
        // 포인트 충전 내역 등록
        when(pointHistoryTable.insert(id, amount, TransactionType.CHARGE, currentTime))
                .thenReturn(new PointHistory(1, id, amount, TransactionType.CHARGE, currentTime));

        // when
        UserPoint result = pointService.chargeUserPoint(id, amount, currentTime);
        pointService.processChargeQueueAutomatically();

        // then
        assertEquals(id, result.getId());
        assertEquals(PointLimitType.MAX_POINT, result.getPoint());
    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용하는 기능")
    public void 유저_포인트_사용_테스트_보유포인트보다_많은_포인트_사용_실패() throws Exception {
        // given
        long id = 1L;
        long chargePoint = 0L; // 보유 포인트
        long amount = 20L;  // 사용 포인트
        long currentTime = System.currentTimeMillis();

        // 유저의 포인트를 조회
        when(userPointTable.selectById(id)).thenReturn(new UserPoint(id, chargePoint, currentTime));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            pointService.useUserPoint(id, amount, currentTime);
        });
    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용하는 기능")
    public void 유저_포인트_사용_테스트_성공() throws Exception {
        // given
        long id = 1L;
        long chargePoint = 100L; // 보유 포인트
        long amount = 20L;  // 사용 포인트
        long currentTime = System.currentTimeMillis();

        // 유저의 포인트를 조회
        when(userPointTable.selectById(id)).thenReturn(new UserPoint(id, chargePoint, currentTime));
        // 포인트 사용 처리
        when(userPointTable.insertOrUpdate(id, chargePoint - amount)).thenReturn(new UserPoint(id, chargePoint - amount, currentTime));
        // 포인트 사용 내역 등록
        when(pointHistoryTable.insert(id, amount, TransactionType.USE, currentTime))
                .thenReturn(new PointHistory(1, id, amount, TransactionType.USE, currentTime));
        // when
        UserPoint result = pointService.useUserPoint(id, amount, currentTime);
        pointService.processUseQueueAutomatically();

        // then
        assertEquals(id, result.getId());
        assertEquals(chargePoint - amount, result.getPoint());
    }

    @Test
    @DisplayName("포인트 충전/사용 내역 조회")
    public void 포인트_충전_사용_내역_조회_테스트() throws Exception {
        // given
        long id = 1L;   // 아이디
        long amount = 20L;  // 포인트

        List<PointHistory> pointHistoryList = new ArrayList<>();
        // 포인트 데이터 생성
        for(int i=0; i<6; i++) {
            // 충전
            if(i <= 2) {
                PointHistory point = new PointHistory(i, id, amount, TransactionType.CHARGE, System.currentTimeMillis());
                pointHistoryList.add(point);
            }
            // 사용
            if(i>2) {
                PointHistory point = new PointHistory(i, id, amount, TransactionType.USE, System.currentTimeMillis());
                pointHistoryList.add(point);
            }
        }

        when(pointHistoryTable.selectAllByUserId(id)).thenReturn(pointHistoryList);

        // when
        List<PointHistory> result = pointService.getHistory(id);
        // then
        // Gson 객체 생성
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // List를 JSON 문자열로 변환
        String json = gson.toJson(result);
        System.out.println(json);

        assertEquals(6, result.size());
    }

    @Test
    @DisplayName("유저 포인트 조회")
    public void 유저_포인트_조회_테스트() throws Exception {
        // given
        long id = 1L;

        when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));

        // when
        UserPoint result = pointService.getUserPoint(id);

        // then
        assertEquals(id, result.getId());

    }
}
