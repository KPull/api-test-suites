package rocks.bastion.core;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import rocks.bastion.Bastion;
import rocks.bastion.core.json.JsonRequest;
import rocks.bastion.support.embedded.TestWithEmbeddedServer;

import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for the timeout functionality.
 *
 * @author benjie.gatt
 */
public class TimeoutTest extends TestWithEmbeddedServer {

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
    };

    @After
    public void teardown() {
        Bastion.globals().clear();
    }

    @Test(timeout = 3000L)
    public void callSlowAPI_jsonRequestWithConfiguredTimeout_requestTimesOutAfterConfiguredTimeAndTestFails() {
        JsonRequest request = JsonRequest.fromString(HttpMethod.GET, "http://localhost:9876/chikuzen-ni", "");
        request.setTimeout(1500L);
        performRequestAndAssertTimeout(request, request.timeout());
    }

    @Test(timeout = 3000L)
    public void callSlowAPI_generalRequestWithConfiguredTimeout_requestTimesOutAfterConfiguredTimeAndTestFails() {
        GeneralRequest request = GeneralRequest.get("http://localhost:9876/chikuzen-ni");
        request.setTimeout(1600L);
        performRequestAndAssertTimeout(request, request.timeout());
    }

    @Test(timeout = 3000L)
    public void callSlowAPI_formUrlEncodedRequestWithConfiguredTimeout_requestTimesOutAfterConfiguredTimeAndTestFails() {
        FormUrlEncodedRequest request = FormUrlEncodedRequest.withMethod(HttpMethod.GET, "http://localhost:9876/chikuzen-ni");
        request.setTimeout(1700L);
        performRequestAndAssertTimeout(request, request.timeout());
    }

    @Test(timeout = 3000L)
    public void callSlowAPI_jsonRequestWithNoConfiguredTimeout_requestTimesOutAfterGlobalTimeoutAndTestFails() {
        JsonRequest request = JsonRequest.fromString(HttpMethod.GET, "http://localhost:9876/chikuzen-ni", "");
        Bastion.globals().timeout(1500L);
        performRequestAndAssertTimeout(request, Bastion.globals().getGlobalRequestTimeout());
    }

    @Test(timeout = 3000L)
    public void callSlowAPI_generalRequestWithNoConfiguredTimeout_requestTimesOutAfterGlobalTimeoutAndTestFails() {
        GeneralRequest request = GeneralRequest.get("http://localhost:9876/chikuzen-ni");
        Bastion.globals().timeout(1600L);
        performRequestAndAssertTimeout(request, Bastion.globals().getGlobalRequestTimeout());
    }

    @Test(timeout = 3000L)
    public void callSlowAPI_formUrlEncodedRequestWithNoConfiguredTimeout_requestTimesOutAfterGlobalTimeoutAndTestFails() {
        FormUrlEncodedRequest request = FormUrlEncodedRequest.withMethod(HttpMethod.GET, "http://localhost:9876/chikuzen-ni");
        Bastion.globals().timeout(1700L);
        performRequestAndAssertTimeout(request, Bastion.globals().getGlobalRequestTimeout());
    }

    private void performRequestAndAssertTimeout(HttpRequest request, long expectedTimeout) {
        assertThatThrownBy(() -> Bastion.request("Create Sushi", request).call())
                .isInstanceOf(AssertionError.class)
                .hasMessage(format("Failed to receive response before timeout of [%d] ms", expectedTimeout));
        assertThat(stopwatch.runtime(TimeUnit.MILLISECONDS)).as("Test runtime").isGreaterThanOrEqualTo(1000L);
    }
}
