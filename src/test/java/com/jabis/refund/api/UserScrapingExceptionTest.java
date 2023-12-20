package com.jabis.refund.api;

import com.jabis.refund.core.security.cipher.CryptographyOperations;
import com.jabis.refund.core.security.cipher.OneWayPasswordHashOperations;
import com.jabis.refund.core.security.token.OAuth2Token;
import com.jabis.refund.core.security.token.OAuth2TokenConverter;
import com.jabis.refund.dto.UserProfileDto;
import com.jabis.refund.exception.ResponseCode;
import com.jabis.refund.exception.ScrapFailureException;
import com.jabis.refund.repository.ScrapRefundCollectorRepository;
import com.jabis.refund.repository.UserProfileRepository;
import com.jabis.refund.repository.entity.user.UserProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserScrapingExceptionTest {

    private final MockMvc mockMvc;

    private final UserProfileRepository userProfileRepository;

    private final OneWayPasswordHashOperations hashOperator;

    private final CryptographyOperations cryptoOperator;

    private final OAuth2TokenConverter oAuth2TokenConverter;

    private UserProfileDto userProfileDto;

    @MockBean
    private ScrapRefundCollectorRepository scrapRefundCollectorRepository;

    public UserScrapingExceptionTest(MockMvc mockMvc,
                                     UserProfileRepository userProfileRepository,
                                     OneWayPasswordHashOperations hashOperator,
                                     CryptographyOperations cryptoOperator,
                                     OAuth2TokenConverter oAuth2TokenConverter) {

        this.mockMvc = mockMvc;
        this.userProfileRepository = userProfileRepository;
        this.hashOperator = hashOperator;
        this.cryptoOperator = cryptoOperator;
        this.oAuth2TokenConverter = oAuth2TokenConverter;
    }

    @BeforeEach
    void setUp() {

        userProfileDto = UserProfileDto.builder()
                                       .userId("doolee21")
                                       .name("김둘리")
                                       .password("123456")
                                       .regNo("921108-1582816")
                                       .build();
    }

    @AfterEach
    void tearDown() {
        userProfileRepository.deleteAll();
    }

    @Test
    void 스크래핑서버_통신에러() throws Exception {

        final UserProfile userProfile = UserProfile.builder()
                                                   .userId(userProfileDto.getUserId())
                                                   .password(hashOperator.hash(userProfileDto.getPassword()))
                                                   .name(userProfileDto.getName())
                                                   .regNo(cryptoOperator.encrypt(userProfileDto.getRegNo()))
                                                   .build();

        userProfileRepository.save(userProfile);

        final OAuth2Token encodedToken = oAuth2TokenConverter.encode(userProfileDto.getUserId());

        when(scrapRefundCollectorRepository.getScrapResponse(userProfileDto.getName(), userProfileDto.getRegNo()))
                .thenThrow(new ScrapFailureException(new RestClientException("scrap server communication error!!!")));

        this.mockMvc.perform(post("/szs/scrap")
                                     .accept(MediaType.APPLICATION_JSON)
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .characterEncoding(StandardCharsets.UTF_8)
                                     .header(HttpHeaders.AUTHORIZATION, "Bearer " + encodedToken.getTokenValue()))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ResponseCode.SCRAP_FAILURE.getCode()))
                    .andExpect(result -> assertInstanceOf(ScrapFailureException.class, result.getResolvedException()))
                    .andExpect(jsonPath("$.message").value(ResponseCode.SCRAP_FAILURE.getMessage()));
    }
}
