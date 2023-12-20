package com.jabis.refund.repository;

import com.jabis.refund.dto.ScrapRequest;
import com.jabis.refund.dto.scrap.ScrapResponse;
import com.jabis.refund.exception.ScrapFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
@Slf4j
public class ScrapRefundCollectorRepositoryImpl implements ScrapRefundCollectorRepository {
    private final RestTemplate restTemplate;

    private final String host;

    public ScrapRefundCollectorRepositoryImpl(RestTemplate restTemplate, @Value("${scrap.host}") String host) {
        this.restTemplate = restTemplate;
        this.host = host;
    }

    @Override
    public ScrapResponse getScrapResponse(String name, String regNo) {

        final ScrapRequest scrapRequest = new ScrapRequest(name, regNo);
        final ResponseEntity<ScrapResponse> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    host,
                    HttpMethod.POST,
                    new HttpEntity<>(scrapRequest),
                    ScrapResponse.class);
        } catch (RestClientException e) {

            log.error("scrap request failure", e);

            throw new ScrapFailureException(e);
        } catch (Exception e) {

            log.error("scrap request error", e);

            throw new RuntimeException(e);
        }

        return responseEntity.getBody();
    }
}
