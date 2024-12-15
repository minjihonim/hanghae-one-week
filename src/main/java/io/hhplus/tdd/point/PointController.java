package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        // id 값 파라미터 확인
        if(id < 1) {
            throw new RuntimeException("id 값이 1미만일 수 없습니다.");
        }
        // 유저가 존재하는지 확인한다.
        try {
            // 존재하면 유저의 포인트를 리턴
            UserPoint result = pointService.getUserPoint(id);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return List.of();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {

        // id 값 파라미터 확인
        validationParam(id, amount);

        UserPoint result = pointService.chargeUserPoint(id, amount);
        return result;
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        validationParam(id, amount);
        UserPoint result = pointService.useUserPoint(id, amount);
        return result;
    }

    /**
     * id , amount 확인 validation
     * @param id
     * @param amount
     */
    private void validationParam(long id, long amount) {
        // id 값 파라미터 확인
        if(id < 1) {
            throw new RuntimeException("id 값이 1미만일 수 없습니다.");
        }
        // amount 가 0 또는 음수 일 수 없음
        if(amount < 1) {
            throw new RuntimeException("충전 포인트가 1미만일 수 없습니다.");
        }
    }
}
