package io.hhplus.tdd;

import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
                .is5xxServerError());   // id 값이 0 이므로 500 에러
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_id_파라미터_성공_테스트() throws Exception {
        String amount = "10";

        mockMvc.perform(patch("/point/{id}/charge", 1)
                        .contentType(APPLICATION_JSON)
                        .content(amount))
                .andExpect(status()
                        .is2xxSuccessful());   // id 값이 0 이므로 500 에러
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_amount_파라미터_실패_테스트() throws Exception {
        String amount = "0";

        mockMvc.perform(patch("/point/{id}/charge", 1)
                        .contentType(APPLICATION_JSON)
                        .content(amount))
                .andExpect(status()
                        .is5xxServerError());   // id 값이 0 이므로 500 에러
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_amount_파라미터_성공_테스트() throws Exception {
        String amount = "10";

        mockMvc.perform(patch("/point/{id}/charge", 1)
                        .contentType(APPLICATION_JSON)
                        .content(amount))
                .andExpect(status()
                        .is2xxSuccessful());   // id 값이 0 이므로 500 에러
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_테스트_성공() throws Exception {
        UserPoint result = pointService.chargeUserPoint(1, 10);
        assertEquals(1, result.getId());
        assertEquals(10, result.getPoint());
    }

    @Test
    public void 유저_포인트_조회_id_파라미터_유효성_체크() throws Exception {
        mockMvc.perform(get("/point/{id}", 0).contentType(APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
