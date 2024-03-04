package uk.co.asepstrath.bank;

import io.jooby.StatusCode;
import io.jooby.test.JoobyTest;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
public class IntegrationTest {

    private final OkHttpClient client = new OkHttpClient();


    public void testHandleLoginFormSubmissionWithValidUsername(int serverPort) throws IOException {
        // Create a form body with a valid username
        RequestBody formBody = new FormBody.Builder()
                .add("username", "john_doe")
                .build();

        // Create a request with form data
        Request request = new Request.Builder()
                .url("http://localhost:" + serverPort + "/homepage")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Expecting a redirect response
            assertEquals(StatusCode.FOUND.value(), response.code());
            assertEquals("/homepage", response.header("Location"));
        }
    }

    @Test
    public void testHandleLoginFormSubmissionWithEmptyUsername(int serverPort) throws IOException {
        // Create a form body with an empty username
        RequestBody formBody = new FormBody.Builder()
                .add("username", "")
                .build();

        // Create a request with form data
        Request request = new Request.Builder()
                .url("http://localhost:" + serverPort + "/homepage")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Expecting a 400 Bad Request response
            assertEquals(StatusCode.BAD_REQUEST.value(), response.code());

            // Ensure the error message is correct
            assertEquals("Error: Username cannot be empty and must be in database", response.body().string().trim());
        }
    }
}