package com.jabis.refund.support.csv;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.core.AbstractListProcessor;
import com.univocity.parsers.csv.CsvParser;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class CsvImporter {

    private final AbstractListProcessor<ParsingContext> rowProcessor;

    private final CsvParser parser;

    public CsvDefinitions importCsv(InputStream inputStream) {

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            parser.parse(bufferedReader);

            return new CsvDefinitions(rowProcessor.getHeaders(), rowProcessor.getRows());
        } catch (IOException e) {

            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
