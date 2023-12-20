package com.jabis.refund.api;

import com.jabis.refund.core.context.RequestContextHolder;
import com.jabis.refund.core.security.token.OAuth2Token;
import com.jabis.refund.dto.ScrapRefundResponse;
import com.jabis.refund.dto.UserIdPasswordToken;
import com.jabis.refund.dto.UserProfileDto;
import com.jabis.refund.dto.UserProfileResponse;
import com.jabis.refund.dto.scrap.JsonList;
import com.jabis.refund.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/szs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입", description = "사용자 환급 서비스에 가입한다.")
    public UserProfileResponse createUser(@Valid @RequestBody UserProfileDto userProfileDto) {
        return userService.createUser(userProfileDto);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 환급 서비스에 로그인한다.")
    public OAuth2Token login(@Valid @RequestBody UserIdPasswordToken userIdPasswordToken) {
        return userService.login(userIdPasswordToken);
    }

    @GetMapping("/me")
    @Operation(summary = "가입정보", description = "로그인한 사용자의 정보를 조회한다.")
    @SecurityRequirement(name = "Authorization")
    public UserProfileDto getMyProfile() {
        return userService.getMyProfile(RequestContextHolder.get().getUserId());
    }

    @PostMapping("/scrap")
    @Operation(summary = "스크랩", description = "로그인한 사용자 정보를 스크랩한다.")
    @SecurityRequirement(name = "Authorization")
    public JsonList scrap() {
        return userService.scrap(RequestContextHolder.get().getUserId());
    }

    @GetMapping("/refund")
    @Operation(summary = "결정세액 및 퇴직연금세액공제금 계산", description = "로그인한 사용자 정보의 결정세액 및 퇴직연금세액공제금을 계산 및 조회한다.")
    @SecurityRequirement(name = "Authorization")
    public ScrapRefundResponse getRefund() {
        return userService.getRefund(RequestContextHolder.get().getUserId());
    }
}
