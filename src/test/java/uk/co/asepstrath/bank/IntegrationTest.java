package uk.co.asepstrath.bank;

import io.jooby.StatusCode;
import io.jooby.test.JoobyTest;
<<<<<<< HEAD
import uk.co.asepstrath.bank.App;
import io.jooby.StatusCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
=======
import okhttp3.*;
>>>>>>> a51f68ec3c357f63ebfe1f5e319a7b6c02daca5d
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JoobyTest(App.class)
public class IntegrationTest {
<<<<<<< HEAD
    static OkHttpClient client = new OkHttpClient();

    @Test
    public void shouldSayHi(int serverPort) throws IOException {
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort+"/example")
                .build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals("Welcome to Jooby!", rsp.body().string());
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }
}
=======

    private final OkHttpClient client = new OkHttpClient();

    @Test
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
            assertEquals("Error: Username cannot be empty", response.body().string().trim());
        }
    }
}
>>>>>>> a51f68ec3c357f63ebfe1f5e319a7b6c02daca5d
