package com.jabis.refund.configuration;

import com.jabis.refund.support.csv.CsvImporter;
import com.jabis.refund.support.csv.CsvProcessor;
import com.jabis.refund.support.csv.DefaultCsvProcessor;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.common.processor.core.AbstractListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvProcessorConfiguration {

    @Bean
    public AbstractListProcessor<ParsingContext> rowProcessor() {
        return new RowListProcessor();
    }

    @Bean
    public CsvParser csvParser(AbstractListProcessor<ParsingContext> rowProcessor) {

        CsvParserSettings parserSettings = new CsvParserSettings();

        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        return new CsvParser(parserSettings);
    }

    @Bean
    public CsvImporter csvImporter(AbstractListProcessor<ParsingContext> rowProcessor, CsvParser csvParser) {
        return new CsvImporter(rowProcessor, csvParser);
    }

    @Bean
    public CsvProcessor csvProcessor(CsvImporter csvImporter) {
        return new DefaultCsvProcessor(csvImporter);
    }
}
