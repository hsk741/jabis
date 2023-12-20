package com.jabis.refund.repository;

import com.jabis.refund.dto.scrap.ScrapResponse;

public interface ScrapRefundCollectorRepository {
    ScrapResponse getScrapResponse(String name, String regNo);
}
