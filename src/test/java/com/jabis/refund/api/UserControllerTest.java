package com.jabis.refund.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabis.refund.core.security.cipher.CryptographyOperations;
import com.jabis.refund.core.security.cipher.OneWayPasswordHashOperations;
import com.jabis.refund.core.security.token.DefaultOAuth2Token;
import com.jabis.refund.core.security.token.OAuth2Token;
import com.jabis.refund.core.security.token.OAuth2TokenConverter;
import com.jabis.refund.dto.UserIdPasswordToken;
import com.jabis.refund.dto.UserProfileDto;
import com.jabis.refund.dto.UserProfileResponse;
import com.jabis.refund.exception.*;
import com.jabis.refund.repository.DeductionRepository;
import com.jabis.refund.repository.SalaryRepository;
import com.jabis.refund.repository.UserProfileRepository;
import com.jabis.refund.repository.entity.user.UserProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final UserProfileRepository userProfileRepository;

    private final SalaryRepository salaryRepository;

    private final DeductionRepository deductionRepository;

    private final OneWayPasswordHashOperations hashOperator;

    private final CryptographyOperations cryptoOperator;

    private final OAuth2TokenConverter oAuth2TokenConverter;

    private UserProfileDto testUserProfileDto;

    public UserControllerTest(MockMvc mockMvc,
                              ObjectMapper objectMapper,
                              UserProfileRepository userProfileRepository,
                              SalaryRepository salaryRepository,
                              DeductionRepository deductionRepository,
                              OneWayPasswordHashOperations hashOperator,
                              CryptographyOperations cryptoOperator,
                              OAuth2TokenConverter oAuth2TokenConverter) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userProfileRepository = userProfileRepository;
        this.salaryRepository = salaryRepository;
        this.deductionRepository = deductionRepository;
        this.hashOperator = hashOperator;
        this.cryptoOperator = cryptoOperator;
        this.oAuth2TokenConverter = oAuth2TokenConverter;
    }

    @BeforeEach
    void setUp() {

        testUserProfileDto = UserProfileDto.builder()
                                           .userId("bezeeteo21")
                                           .name("베지터")
                                           .password("123456")
                                           .regNo("910411-1656116")
                                           .build();
    }

    @AfterEach
    void tearDown() {

        userProfileRepository.deleteAll();
        salaryRepository.deleteAll();
        deductionRepository.deleteAll();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 회원가입요청_사용자아이디_파라미터검증(String validatingUserId) throws Exception {

        UserProfileDto userProfileDto = UserProfileDto.builder().userId(validatingUserId).build();

        this.mockMvc.perform(post("/szs/signup")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userProfileDto))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.CONSTRAINT_VIOLATION.getCode()))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(result -> jsonPath("$.body").value(Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"123456-7292923", "1234567292923", "1234-1223456", "123412-12234"})
    void 회원가입요청_주민등록번호_파라미터검증(String validatingRegNo) throws Exception {

        UserProfileDto userProfileDto = UserProfileDto.builder().regNo(validatingRegNo).build();

        this.mockMvc.perform(post("/szs/signup")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userProfileDto))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.CONSTRAINT_VIOLATION.getCode()))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(result -> jsonPath("$.body").value(Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void 회원가입_불가능() throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                                                   .userId("notEnabledUserId")
                                                   .password(hashOperator.hash("123456"))
                                                   .name("notEnabledUserName")
                                                   .regNo("123456-1876543")
                                                   .build();

        this.mockMvc.perform(post("/szs/signup")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userProfile))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ResponseCode.NOT_ENABLED_USER.getCode()))
                    .andExpect(result -> assertInstanceOf(NotEnabledUserException.class, result.getResolvedException()))
                    .andExpect(result -> jsonPath("$.body").value(Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @ParameterizedTest
    @MethodSource("existUser")
    void 회원가입_기가입자(UserProfileDto userProfileDto) throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                                                   .userId(userProfileDto.getUserId())
                                                   .password(hashOperator.hash(userProfileDto.getPassword()))
                                                   .name(userProfileDto.getName())
                                                   .regNo(cryptoOperator.encrypt(userProfileDto.getRegNo()))
                                                   .build();

        userProfileRepository.save(userProfile);

        this.mockMvc.perform(post("/szs/signup")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userProfileDto))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ResponseCode.ALREADY_EXISTED_USER.getCode()))
                    .andExpect(result -> assertInstanceOf(AlreadyExistedUserException.class, result.getResolvedException()))
                    .andExpect(result -> jsonPath("$.body").value(Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @ParameterizedTest
    @MethodSource("createUser")
    void 회원가입(UserProfileDto userProfileDto, UserProfileResponse userProfileResponse) throws Exception {

        this.mockMvc.perform(post("/szs/signup")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userProfileDto))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userProfileResponse.getUserId()))
                .andExpect(jsonPath("$.name").value(userProfileResponse.getName()));
    }

    private static Stream<Arguments> createUser() {
        return Stream.of(Arguments.of(
                                 UserProfileDto.builder()
                                               .userId("hong21")
                                               .name("홍길동")
                                               .password("123456")
                                               .regNo("860824-1655068")
                                               .build(),
                                 new UserProfileResponse("hong21", "홍길동")
                         ),
                         Arguments.of(
                                 UserProfileDto.builder()
                                               .userId("doolee21")
                                               .name("김둘리")
                                               .password("123456")
                                               .regNo("921108-1582816")
                                               .build(),
                                 new UserProfileResponse("doolee21", "김둘리")
                         ),
                         Arguments.of(
                                 UserProfileDto.builder()
                                               .userId("mazinga21")
                                               .name("마징가")
                                               .password("123456")
                                               .regNo("880601-2455116")
                                               .build(),
                                 new UserProfileResponse("mazinga21", "마징가")
                         ),
                         Arguments.of(
                                 UserProfileDto.builder()
                                               .userId("bezeeteo21")
                                               .name("베지터")
                                               .password("123456")
                                               .regNo("910411-1656116")
                                               .build(),
                                 new UserProfileResponse("bezeeteo21", "베지터")
                         ),
                         Arguments.of(
                                 UserProfileDto.builder()
                                               .userId("monkey21")
                                               .name("손오공")
                                               .password("123456")
                                               .regNo("820326-2715702")
                                               .build(),
                                 new UserProfileResponse("monkey21", "손오공")
                         )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 로그인_사용자ID_공백검증(String validatingUserId) throws Exception {

        UserIdPasswordToken userIdPasswordToken = UserIdPasswordToken.builder()
                                                                     .userId(validatingUserId)
                                                                     .password("123456")
                                                                     .build();
        this.mockMvc.perform(post("/szs/login")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userIdPasswordToken))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.CONSTRAINT_VIOLATION.getCode()))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value(ResponseCode.CONSTRAINT_VIOLATION.getMessage()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"notFoundUser"})
    void 로그인_미존재_사용자ID_검증(String validatingUserId) throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                                                   .userId(testUserProfileDto.getUserId())
                                                   .password(hashOperator.hash(testUserProfileDto.getPassword()))
                                                   .name(testUserProfileDto.getName())
                                                   .regNo(cryptoOperator.encrypt(testUserProfileDto.getRegNo()))
                                                   .build();

        userProfileRepository.save(userProfile);

        UserIdPasswordToken userIdPasswordToken = UserIdPasswordToken.builder()
                                                                     .userId(validatingUserId)
                                                                     .password(testUserProfileDto.getRegNo())
                                                                     .build();
        this.mockMvc.perform(post("/szs/login")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userIdPasswordToken))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.NOT_FOUND_USER.getCode()))
                .andExpect(result -> assertInstanceOf(NotFoundUserException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value(ResponseCode.NOT_FOUND_USER.getMessage()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 로그인_암호_공백검증(String validatingPassword) throws Exception {

        UserIdPasswordToken userIdPasswordToken = UserIdPasswordToken.builder()
                                                                     .userId("notBlankPassword")
                                                                     .password(validatingPassword)
                                                                     .build();
        this.mockMvc.perform(post("/szs/login")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userIdPasswordToken))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.CONSTRAINT_VIOLATION.getCode()))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value(ResponseCode.CONSTRAINT_VIOLATION.getMessage()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"different_password"})
    void 로그인_암호_파라미터검증(String validatingPassword) throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                .userId(testUserProfileDto.getUserId())
                .password(hashOperator.hash(testUserProfileDto.getPassword()))
                .name(testUserProfileDto.getName())
                .regNo(cryptoOperator.encrypt(testUserProfileDto.getRegNo()))
                .build();

        userProfileRepository.save(userProfile);

        UserIdPasswordToken userIdPasswordToken = UserIdPasswordToken.builder()
                                                                     .userId(testUserProfileDto.getUserId())
                                                                     .password(validatingPassword)
                                                                     .build();
        this.mockMvc.perform(post("/szs/login")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userIdPasswordToken))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.NOT_MATCHED_PASSWORD.getCode()))
                .andExpect(result -> assertInstanceOf(NotMatchedPasswordException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value(ResponseCode.NOT_MATCHED_PASSWORD.getMessage()));
    }

    @ParameterizedTest
    @MethodSource("existUser")
    void 로그인(UserProfileDto userProfileDto) throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                .userId(userProfileDto.getUserId())
                .password(hashOperator.hash(userProfileDto.getPassword()))
                .name(userProfileDto.getName())
                .regNo(cryptoOperator.encrypt(userProfileDto.getRegNo()))
                .build();

        userProfileRepository.save(userProfile);

        UserIdPasswordToken userIdPasswordToken =
                new UserIdPasswordToken(userProfileDto.getUserId(), userProfileDto.getPassword());

        this.mockMvc.perform(post("/szs/login")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(userIdPasswordToken))
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuedAt", notNullValue()))
                .andExpect(jsonPath("$.expiresAt", notNullValue()))
                .andExpect(jsonPath("$.tokenValue", notNullValue()));
    }

    @Test
    void Authorization헤더_Bearer_토큰_미존재() throws Exception {

        this.mockMvc.perform(get("/szs/me")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.MALFORMED_TOKEN.getCode()))
                .andExpect(result -> assertInstanceOf(InvalidBearerTokenException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value(ResponseCode.MALFORMED_TOKEN.getMessage()));
    }

    @Test
    void Authorization헤더_Bearer_토큰_만료() throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                .userId(testUserProfileDto.getUserId())
                .password(hashOperator.hash(testUserProfileDto.getPassword()))
                .name(testUserProfileDto.getName())
                .regNo(cryptoOperator.encrypt(testUserProfileDto.getRegNo()))
                .build();

        userProfileRepository.save(userProfile);

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.minus(3600, ChronoUnit.SECONDS);

        final OAuth2Token encodedToken = new DefaultOAuth2Token(oAuth2TokenConverter.generate(testUserProfileDto.getUserId(), issuedAt, expiresAt));

        this.mockMvc.perform(get("/szs/me")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .header(HttpHeaders.AUTHORIZATION, "Bearer " + encodedToken.getTokenValue()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.EXPIRATION_TOKEN.getCode()))
                .andExpect(result -> assertInstanceOf(ExpiredTokenException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value(ResponseCode.EXPIRATION_TOKEN.getMessage()));
    }

    @ParameterizedTest
    @MethodSource("existUser")
    void 회원정보조회(UserProfileDto userProfileDto) throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                .userId(userProfileDto.getUserId())
                .password(hashOperator.hash(userProfileDto.getPassword()))
                .name(userProfileDto.getName())
                .regNo(cryptoOperator.encrypt(userProfileDto.getRegNo()))
                .build();

        userProfileRepository.save(userProfile);

        final OAuth2Token encodedToken = oAuth2TokenConverter.encode(userProfileDto.getUserId());

        this.mockMvc.perform(get("/szs/me")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .header(HttpHeaders.AUTHORIZATION, "Bearer " + encodedToken.getTokenValue()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userProfileDto.getUserId()))
                .andExpect(jsonPath("$.name").value(userProfileDto.getName()))
                .andExpect(jsonPath("$.regNo").value(userProfileDto.getRegNo()));
    }


    @ParameterizedTest
    @MethodSource("existUser")
    void 스크래핑_결정세액과퇴직연세액공제금액조회(UserProfileDto userProfileDto) throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                                                   .userId(userProfileDto.getUserId())
                                                   .password(hashOperator.hash(userProfileDto.getPassword()))
                                                   .name(userProfileDto.getName())
                                                   .regNo(cryptoOperator.encrypt(userProfileDto.getRegNo()))
                                                   .build();

        userProfileRepository.save(userProfile);

        final OAuth2Token encodedToken = oAuth2TokenConverter.encode(userProfileDto.getUserId());

        mockMvc.perform(post("/szs/scrap")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + encodedToken.getTokenValue()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.급여[0].소득내역", is("급여")))
               .andExpect(jsonPath("$.급여[0].총지급액", notNullValue()))
               .andExpect(jsonPath("$.급여[0].소득구분", is("근로소득(연간)")))
               .andExpect(jsonPath("$.산출세액", notNullValue()))
               .andExpect(jsonPath("$.소득공제[0].소득구분", is("보험료")))
               .andExpect(jsonPath("$.소득공제[0].금액", notNullValue()))
               .andExpect(jsonPath("$.소득공제[1].소득구분", is("교육비")))
               .andExpect(jsonPath("$.소득공제[1].금액", notNullValue()))
               .andExpect(jsonPath("$.소득공제[2].소득구분", is("기부금")))
               .andExpect(jsonPath("$.소득공제[2].금액", notNullValue()))
               .andExpect(jsonPath("$.소득공제[3].소득구분", is("의료비")))
               .andExpect(jsonPath("$.소득공제[3].금액", notNullValue()))
               .andExpect(jsonPath("$.소득공제[4].소득구분", is("퇴직연금")))
               .andExpect(jsonPath("$.소득공제[4].금액", notNullValue()));

        mockMvc.perform(get("/szs/refund")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + encodedToken.getTokenValue()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.이름").value(userProfileDto.getName()))
               .andExpect(jsonPath("$.결정세액", notNullValue()))
               .andExpect(jsonPath("$.퇴직연금세액공제", notNullValue()));
    }

    @Test
    void 스크래핑정보미존재() throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                                                   .userId(testUserProfileDto.getUserId())
                                                   .password(hashOperator.hash(testUserProfileDto.getPassword()))
                                                   .name(testUserProfileDto.getName())
                                                   .regNo(cryptoOperator.encrypt(testUserProfileDto.getRegNo()))
                                                   .build();

        userProfileRepository.save(userProfile);

        final OAuth2Token encodedToken = oAuth2TokenConverter.encode(testUserProfileDto.getUserId());

        mockMvc.perform(get("/szs/refund")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + encodedToken.getTokenValue()))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.code").value(ResponseCode.NOT_FOUND_SCRAPING_DATA.getCode()))
               .andExpect(result -> assertInstanceOf(NotFoundScrapingDataException.class, result.getResolvedException()));
    }

    private static Stream<UserProfileDto> existUser() {
        return Stream.of(UserProfileDto.builder()
                                       .userId("hong21")
                                       .name("홍길동")
                                       .password("123456")
                                       .regNo("860824-1655068")
                                       .build(),
                         UserProfileDto.builder()
                                       .userId("doolee21")
                                       .name("김둘리")
                                       .password("123456")
                                       .regNo("921108-1582816")
                                       .build(),
                         UserProfileDto.builder()
                                       .userId("mazinga21")
                                       .name("마징가")
                                       .password("123456")
                                       .regNo("880601-2455116")
                                       .build(),
                         UserProfileDto.builder()
                                       .userId("bezeeteo21")
                                       .name("베지터")
                                       .password("123456")
                                       .regNo("910411-1656116")
                                       .build(),
                         UserProfileDto.builder()
                                       .userId("monkey21")
                                       .name("손오공")
                                       .password("123456")
                                       .regNo("820326-2715702")
                                       .build()
        );
    }
}
