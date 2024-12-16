package io.hhplus.tdd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointLimitType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PointTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PointService pointService;

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_id_파라미터_실패_테스트() throws Exception {
        String amount = "10";

        mockMvc.perform(patch("/point/{id}/charge", 0)
                        .contentType(APPLICATION_JSON)
                        .content(amount))
                .andExpect(status()
                .is5xxServerError())
                .andDo(print())
                .andReturn();;   // id 값이 0 이므로 500 에러
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_id_파라미터_성공_테스트() throws Exception {
        String amount = "10";

        mockMvc.perform(patch("/point/{id}/charge", 1)
                        .contentType(APPLICATION_JSON)
                        .content(amount))
                .andExpect(status()
                        .is2xxSuccessful())
                .andDo(print())
                .andReturn();;   // id 값이 1 이므로 200 성공
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_amount_파라미터_실패_테스트() throws Exception {
        String amount = "0";

        mockMvc.perform(patch("/point/{id}/charge", 1)
                        .contentType(APPLICATION_JSON)
                        .content(amount))
                .andExpect(status()
                        .is5xxServerError())
                .andDo(print())
                .andReturn();;   // 포인트 충전 값이 0 이므로 500 에러
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_amount_파라미터_성공_테스트() throws Exception {
        String amount = String.valueOf(PointLimitType.MAX_POINT);

        mockMvc.perform(patch("/point/{id}/charge", 1)
                        .contentType(APPLICATION_JSON)
                        .content(amount))
                .andExpect(status()
                        .is2xxSuccessful())
                .andDo(print())
                .andReturn();;   // 포인트 충전 값이 10 이므로 200 성공
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_테스트_최대포인트_충전_실패() throws Exception {
        String amount = String.valueOf(PointLimitType.MAX_POINT+1);

        // 맥스 충전 포인트는 100만이므로 500 실패처리
        mockMvc.perform(patch("/point/{id}/charge", 1)
                        .contentType(APPLICATION_JSON)
                        .content(amount))
                .andExpect(status()
                        .is5xxServerError())
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_테스트_성공() throws Exception {
        UserPoint result = pointService.chargeUserPoint(1, 1000000);
        assertEquals(1, result.getId());
        assertEquals(1000000, result.getPoint());
    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용하는 기능")
    public void 유저_포인트_사용_테스트_성공() throws Exception {
        pointService.chargeUserPoint(1, 1000000); // 포인트 충전
        UserPoint result = pointService.useUserPoint(1, 10);
        assertEquals(1, result.getId());
        assertEquals(999990, result.getPoint());
    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용하는 기능")
    public void 유저_포인트_사용_테스트_보유포인트보다_많은_포인트_사용_실패() throws Exception {
        // 포인트 충전 진행
        long id = 1;
        long amount = 20;
        // 20 포인트 충전
        UserPoint user = pointService.chargeUserPoint(id, amount);
        // user id 와 user point 확인
        System.out.println("id="+user.getId());
        System.out.println("point="+user.getPoint());

        // 40 포인트 사용
        String usePoint = "40";
        // 포인트 사용 API 수행
        mockMvc.perform(patch("/point/{id}/use", id)
                        .contentType(APPLICATION_JSON)
                        .content(usePoint))
                .andExpect(status()
                        .is5xxServerError())
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("포인트 충전/사용 내역 조회")
    public void 포인트_충전_사용_내역_조회_테스트() throws Exception {
        // parameter
        long id = 1;
        long amount = 20;

        // 포인트 충전
        for(int i=0; i<3; i++) {
            pointService.chargeUserPoint(id, amount);
        }

        // 포인트 사용
        for(int i=0; i<3; i++) {
            pointService.useUserPoint(id, amount);
        }

        List<PointHistory> pointHistoryList = pointService.getHistory(id);
        // Gson 객체 생성
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // List를 JSON 문자열로 변환
        String json = gson.toJson(pointHistoryList);
        System.out.println(json);
        
        // 포인트 충전/사용 내역 API 호출
        mockMvc.perform(get("/point/{id}/histories", id)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();
        
        // 정보조회
        mockMvc.perform(get("/point/{id}", id).contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("유저 포인트 조회")
    public void 유저_포인트_조회_id_파라미터_값_검증_테스트() throws Exception {
        mockMvc.perform(get("/point/{id}", -1).contentType(APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("유저 포인트 조회")
    public void 유저_포인트_조회_테스트() throws Exception {
        long id = 1;
        mockMvc.perform(get("/point/{id}", id).contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();
    }
}
