package io.hhplus.tdd;

import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.service.impl.PointServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PointControllerTest {
    @Mock
    private PointServiceImpl pointService;
    @InjectMocks
    private PointController pointController;
    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_id_파라미터_실패_테스트() throws Exception {
        //given
        long id = 0; // id 값
        long amount = 10;

        // when & then
        assertThrows(RuntimeException.class, () -> {
            pointController.charge(id, amount);
        });
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_id_파라미터_성공_테스트() throws Exception {
        // given
        long id = 1; // id 값
        long amount = 10;

        // when & then
        pointController.charge(id, amount);
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_amount_파라미터_실패_테스트() throws Exception {
        //given
        long id = 1; // id 값
        long amount = 0;

        // when & then
        assertThrows(RuntimeException.class, () -> {
            pointController.charge(id, amount);
        });
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능")
    public void 유저_포인트_충전_amount_파라미터_성공_테스트() throws Exception {
        //given
        long id = 1; // id 값
        long amount = 10;

        // when & then
        pointController.charge(id, amount);
    }
}
