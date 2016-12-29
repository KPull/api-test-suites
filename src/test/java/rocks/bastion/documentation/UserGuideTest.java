package rocks.bastion.documentation;

import org.apache.http.entity.ContentType;
import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.FormUrlEncodedRequest;
import rocks.bastion.core.GeneralRequest;
import rocks.bastion.core.StatusCodeAssertions;
import rocks.bastion.core.json.JsonRequest;
import rocks.bastion.core.json.JsonResponseAssertions;
import rocks.bastion.support.embedded.TestWithProxiedEmbeddedServer;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * These are all the code examples which appear in the User Guide. Some of the examples don't have an assertion because that's not the point.
 * We want to make sure that all the examples at least compile (especially the Request ones). For examples which do have assertions (eg.
 * the tests showing Assertions) then the assertions should pass as well. For examples which show failing assertions, we wrap the test, and expect
 * an AssertionError.
 */
public class UserGuideTest extends TestWithProxiedEmbeddedServer {

    @Test
    public void generalRequest_get() {
        Bastion.request(
                GeneralRequest.get("http://sushi-shop.test/sushi")
        ).call();
    }

    @Test
    public void generalRequest_getWithAttributes() {
        Bastion.request(
                GeneralRequest.get("http://sushi-shop.test/sushi/{id}")
                        .addRouteParam("id", "5")
                        .addQueryParam("amount", "6")
                        .addHeader("X-Caches", "disabled")
        ).call();
    }

    @Test
    public void generalRequest_post() {
        Bastion.request(GeneralRequest.post("http://sushi-shop.test/greeting", "<b>Hello, sushi lover!</b>")
                .setContentType(ContentType.TEXT_HTML)
        ).call();
    }

    @Test
    public void jsonRequest_postFromString() {
        Bastion.request(
                JsonRequest.postFromString("http://sushi-shop.test/sushi", "{ \"name\": \"Salmon Nigiri\", \"price\":5.85 }")
        ).call();
    }

    @Test
    public void jsonRequest_patchFromString() {
        Bastion.request(
                JsonRequest.patchFromString("http://sushi-shop.test/sushi/2",
                        "{ \"op\":\"replace\", \"path\":\"/name\", \"value\":\"Squid Nigiri\" }")
        ).call();
    }

    @Test
    public void jsonRequest_postFromResource() {
        Bastion.request(
                JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json")
        ).call();
    }

    @Test
    public void jsonRequest_postFromResource_overrideContentType() {
        Bastion.request(
                JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json")
                        .overrideContentType(ContentType.APPLICATION_OCTET_STREAM)
        ).call();
    }

    @Test
    public void jsonRequest_postFromTemplate() {
        Bastion.request(
                JsonRequest.postFromTemplate("http://sushi-shop.test/sushi", "classpath:/rocks/bastion/core/request/test-template-body.json",
                        Collections.singletonMap("food", "Squid Nigiri"))
        ).call();
    }

    @Test
    public void formUrlEncodedRequest_post() {
        Bastion.request(
                FormUrlEncodedRequest.post("http://sushi-shop.test/sushi")
                        .addDataParameter("name", "Squid Nigiri")
                        .addDataParameter("price", "5.85")
                        .addHeader("X-Manager", "John Doe")
        ).call();
    }

    @Test
    public void formUrlEncodedRequest_put_overrideContentType() {
        Bastion.request(
                FormUrlEncodedRequest.put("http://sushi-shop.test/booking")
                        .addDataParameter("name", "John Doe")
                        .addDataParameter("timestamp", "2017-02-10T19:00:00Z")
                        .overrideContentType(ContentType.APPLICATION_OCTET_STREAM)
        ).call();
    }

    @Test
    public void globals() {
        Bastion.globals()
                .addHeader("Authorization", "BASIC a3lsZTpwdWxsaWNpbm8=")
                .addQueryParam("diet", "vegetarian")
                .addRouteParam("version", "v2");

        Bastion.globals().clear();
    }

    @Test
    public void clearGlobals() {
        Bastion.globals().clear();
    }

    @Test
    public void statusCodeAssertions() {
        Bastion.request(GeneralRequest.post("http://sushi-shop.test/greeting", "<b>Hello, sushi lover!</b>"))
                .withAssertions(StatusCodeAssertions.expecting(200)).call();
    }

    @Test
    public void statusCodeAssertions_multipleArgs() {
        Bastion.request(GeneralRequest.post("http://sushi-shop.test/greeting", "<b>Hello, sushi lover!</b>"))
                .withAssertions(StatusCodeAssertions.expecting(new int[]{200, 201, 204})).call();
    }

    @Test
    public void jsonResponseAssertions() {
        Bastion.request(GeneralRequest.get("http://sushi-shop.test/reservation/1"))
                .withAssertions(JsonResponseAssertions.fromString(200, "{ \"name\":\"John Doe\", \"timestamp\":\"2016-02-10T21:00:00Z\" }"))
                .call();
    }

    @Test
    public void jsonResponseAssertions_wrongValue() {
        // Error output:
        /*
         * java.lang.AssertionError: Actual response body is not as expected.
         * The following JSON Patch (as per RFC-6902) tells you what operations you need to perform to transform the actual response body into the expected response body:
         *   [{"op":"replace","path":"/price","value":"EUR 5.60"}]
         */
        assertThatThrownBy(() -> {
            Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                    .withAssertions(JsonResponseAssertions.fromString(201, "{ " +
                            "\"id\":5, " +
                            "\"name\":\"sashimi\", " +
                            "\"price\":\"EUR 5.60\", " +
                            "\"type\":\"SASHIMI\" " +
                            "}"
                    ).ignoreValuesForProperties("id")).call();
        }).isInstanceOf(AssertionError.class).hasMessageContaining("[{\"op\":\"replace\",\"path\":\"/price\",\"value\":\"EUR 5.60\"}]");
    }

    @Test
    public void jsonResponseAssertions_ignoreField() {
        Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                .withAssertions(JsonResponseAssertions.fromString(201, "{ " +
                                "\"id\":5, " +
                                "\"name\":\"sashimi\", " +
                                "\"price\":5.60, " +
                                "\"type\":\"SASHIMI\" " +
                                "}"
                        ).ignoreValuesForProperties("id")
                ).call();
    }

    @Test
    public void jsonResponseAssertions_fromResource() {
        Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json")
                        .ignoreValuesForProperties("id")
                ).call();
    }

}