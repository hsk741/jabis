package com.jabis.refund.support.csv;

import java.io.InputStream;

public interface CsvProcessor {
    void processCsv(InputStream inputStream, ImportedCsvProcessor importedCsvProcessor);
}
