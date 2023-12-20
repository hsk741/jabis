package com.jabis.mock;

import lombok.Getter;
import org.mockserver.integration.ClientAndServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ScrapingMockServer {

    private ClientAndServer mockExternalScrapingServer;

    @Getter
    private final int port = 9999;

    public void start() {
        mockExternalScrapingServer = ClientAndServer.startClientAndServer(port);
    }

    public void stop() {

        if (mockExternalScrapingServer != null && mockExternalScrapingServer.isRunning())
            mockExternalScrapingServer.stop();
    }

    public void setUpMockApiRequest(String requestPath, String httpMethodName, String requestBody) throws IOException {

        final ClassPathResource classPathResource = new ClassPathResource("scrap.json");
        final Path path = Paths.get(classPathResource.getURI());
        final String responseBody = Files.readString(path);

        mockExternalScrapingServer.when(
                request()
                        .withPath(requestPath)
                        .withMethod(httpMethodName)
                        .withBody(requestBody)
        ).respond(
                response(responseBody)
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
        );
    }

    public void verify(String requestPath, String httpMethodName, String requestBody) {

        mockExternalScrapingServer.verify(
                request()
                        .withPath(requestPath)
                        .withMethod(httpMethodName)
                        .withBody(requestBody)
        );
    }
}
