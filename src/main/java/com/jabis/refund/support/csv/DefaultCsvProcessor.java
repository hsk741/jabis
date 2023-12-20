package com.jabis.refund.support.csv;

import java.io.InputStream;

public class DefaultCsvProcessor implements CsvProcessor {

    private final CsvImporter csvImporter;

    public DefaultCsvProcessor(CsvImporter csvImporter) {
        this.csvImporter = csvImporter;
    }

    @Override
    public void processCsv(InputStream inputStream, ImportedCsvProcessor importedCsvProcessor) {
        importedCsvProcessor.processImportedCsv(csvImporter.importCsv(inputStream));
    }
}
