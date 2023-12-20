package com.jabis.refund.repository;

import com.jabis.mock.ScrapingMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
class ScrapRefundCollectorRepositoryTest {

    private final ScrapRefundCollectorRepository scrapRefundCollectorRepository;

    private final ScrapingMockServer scrapingMockServer;

    public ScrapRefundCollectorRepositoryTest() {

        this.scrapingMockServer = new ScrapingMockServer();
        this.scrapRefundCollectorRepository =
                new ScrapRefundCollectorRepositoryImpl(new TestRestTemplate().getRestTemplate(),
                                                       UriComponentsBuilder.newInstance()
                                                                           .scheme("http")
                                                                           .host("localhost")
                                                                           .port(scrapingMockServer.getPort())
                                                                           .path("/v2/scrap")
                                                                           .build()
                                                                           .toUriString());
    }

    @AfterEach
    void tearDown() {
        scrapingMockServer.stop();
    }

    @Test
    void 스크래핑mock서버통신_확인() throws IOException {

        scrapingMockServer.start();
        scrapingMockServer.setUpMockApiRequest("/v2/scrap", HttpMethod.POST.name(), any());

        scrapRefundCollectorRepository.getScrapResponse(any(), any());

        scrapingMockServer.verify("/v2/scrap", HttpMethod.POST.name(), any());
    }
}
