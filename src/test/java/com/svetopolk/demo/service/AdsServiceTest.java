package com.svetopolk.demo.service;

import com.svetopolk.demo.dto.Status;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AdsServiceTest {

    @Autowired
    private AdsService adsService;

    public static MockWebServer mockWebServer;

    @BeforeEach
    void before() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8090);
    }

    @AfterEach
    public void after() {
        try {
            mockWebServer.shutdown();
        } catch (IOException ignored) {
            //nothing
        }
    }

    @Test
    void yesResponse() {
        MockResponse mockedResponse = new MockResponse()
                .setBody("""
                        {"ads": "sure, why not!"}
                        """);
        mockWebServer.enqueue(mockedResponse);

        assertThat(adsService.getStatus("US"), is(Status.ENABLED));
    }

    @Test
    void negativeResponse() {
        MockResponse mockedResponse = new MockResponse()
                .setBody("""
                        {"ads": "you shall not pass!"}
                        """);
        mockWebServer.enqueue(mockedResponse);

        assertThat(adsService.getStatus("US"), is(Status.DISABLED));
    }

    @Test
    void unexpectedResponse() {
        MockResponse mockedResponse = new MockResponse()
                .setBody("""
                        {"ads": "you do not expect this"}
                        """);
        mockWebServer.enqueue(mockedResponse);

        assertThat(adsService.getStatus("US"), is(Status.UNDEFINED));
    }

    @Test
    @Timeout(10)
    void connectionTimeout() throws IOException {
        mockWebServer.shutdown();

        assertThat(adsService.getStatus("US"), is(Status.UNDEFINED));
    }

    @Test()
    @Timeout(10)
    void readTimeout() {
        MockResponse mockedResponse = new MockResponse()
                .setBodyDelay(2, TimeUnit.SECONDS)
                .setBody("""
                        {"ads": "sure, why not!"}
                        """);
        mockWebServer.enqueue(mockedResponse);

        assertThat(adsService.getStatus("US"), is(Status.UNDEFINED));
    }

    @Test
    void serverErrorResponse() {
        MockResponse mockedResponse = new MockResponse()
                .setResponseCode(500)
                .setBody("""
                        {"ads": "sure, why not!"}
                        """);
        mockWebServer.enqueue(mockedResponse);

        assertThat(adsService.getStatus("US"), is(Status.UNDEFINED));
    }

    @Test
    void badClientRequestErrorResponse() {
        MockResponse mockedResponse = new MockResponse()
                .setResponseCode(400)
                .setBody("""
                        {"ads": "sure, why not!"}
                        """);
        mockWebServer.enqueue(mockedResponse);

        assertThat(adsService.getStatus("US"), is(Status.UNDEFINED));
    }

    @Test
    void notJsonResponse() {
        MockResponse mockedResponse = new MockResponse()
                .setBody("it is not json");
        mockWebServer.enqueue(mockedResponse);

        assertThat(adsService.getStatus("US"), is(Status.UNDEFINED));
    }

}