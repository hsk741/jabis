package com.jabis.refund.support.csv;

import java.util.List;

public record CsvDefinitions(
        String[] header,
        List<String[]> contents
) {
}
