package rocks.bastion.documentation;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.entity.ContentType;
import org.junit.Ignore;
import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.*;
import rocks.bastion.core.json.JsonRequest;
import rocks.bastion.core.json.JsonResponseAssertions;
import rocks.bastion.core.json.JsonSchemaAssertions;
import rocks.bastion.support.embedded.Sushi;
import rocks.bastion.support.embedded.TestWithProxiedEmbeddedServer;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * These are all the code examples which appear in the User Guide. Some of the examples don't have an assertion because that's not the point.
 * We want to make sure that all the examples at least compile (especially the Request ones). For examples which do have assertions (eg.
 * the tests showing Assertions) then the assertions should pass as well. For examples which show failing assertions, we wrap the test, and expect
 * an AssertionError.
 */
@Ignore
public class UserGuideTest extends TestWithProxiedEmbeddedServer {

    // docs:quickstart
    @Test
    public void quickstart() {
        Bastion.request("Get the Restaurant's Name", GeneralRequest.get("http://localhost:9876/restaurant"))
                .withAssertions((statusCode, response, model) -> assertThat(model).isEqualTo("The Sushi Parlour"))
                .call();
    }
    // docs:quickstart

    @Test
    public void generalRequest_get() {
        // docs:general-request-get
        Bastion.request(
                GeneralRequest.get("http://sushi-shop.test/sushi")
        ).call();
        // docs:general-request-get
    }

    @Test
    public void generalRequest_getWithAttributes() {
        // docs:general-request-get-with-attributes
        Bastion.request(
                GeneralRequest.get("http://sushi-shop.test/sushi/{id}")
                        .addRouteParam("id", "5")
                        .addQueryParam("amount", "6")
                        .addHeader("X-Caches", "disabled")
        ).call();
        // docs:general-request-get-with-attributes
    }

    @Test
    public void generalRequest_post() {
        // docs:general-request-post
        Bastion.request(GeneralRequest.post("http://sushi-shop.test/greeting", "<b>Hello, sushi lover!</b>")
                .setContentType(ContentType.TEXT_HTML)
        ).call();
        // docs:general-request-post
    }

    @Test
    public void fileRequest_post() {
        // docs:file-request-post
        Bastion.request(FileRequest.post("http://sushi-shop.test/greeting", "classpath:fixture/greeting.html")
                            .setContentType(ContentType.TEXT_HTML)
        ).call();
        // docs:file-request-post
    }


    @Test
    public void jsonRequest_postFromString() {
        // docs:json-request-post-from-string
        Bastion.request(
                JsonRequest.postFromString("http://sushi-shop.test/sushi", "{ \"name\": \"Salmon Nigiri\", \"price\":5.85 }")
        ).call();
        // docs:json-request-post-from-string
    }

    @Test
    public void jsonRequest_patchFromString() {
        // docs:json-request-patch-from-string
        Bastion.request(
                JsonRequest.patchFromString("http://sushi-shop.test/sushi/2",
                        "{ \"op\":\"replace\", \"path\":\"/name\", \"value\":\"Squid Nigiri\" }")
        ).call();
        // docs:json-request-patch-from-string
    }

    @Test
    public void jsonRequest_postFromResource() {
        // docs:json-request-post-from-resource
        Bastion.request(
                JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json")
        ).call();
        // docs:json-request-post-from-resource
    }

    @Test
    public void jsonRequest_postFromResource_overrideContentType() {
        // docs:json-request-post-from-resource-override
        Bastion.request(
                JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json")
                        .overrideContentType(ContentType.APPLICATION_OCTET_STREAM)
        ).call();
        // docs:json-request-post-from-resource-override
    }

    @Test
    public void jsonRequest_postFromTemplate() {
        // docs:json-request-post-from-template
        Bastion.request(
                JsonRequest.postFromTemplate("http://sushi-shop.test/sushi", "classpath:/rocks/bastion/core/request/test-template-body.json",
                        Collections.singletonMap("food", "Squid Nigiri"))
        ).call();
        // docs:json-request-post-from-template
    }

    @Test
    public void jsonRequest_postFromModel() {
        // docs:json-request-post-from-model
        Sushi model = Sushi.newSushi().id(19).name("Salmon Nigiri").price(10L).type(Sushi.Type.NIGIRI).build();
        Bastion.request(JsonRequest.postFromModel("http://test.test", model)).call();
        // docs:json-request-post-from-model
    }

    @Test
    public void formUrlEncodedRequest_post() {
        // docs:form-url-encoded-request-post
        Bastion.request(
                FormUrlEncodedRequest.post("http://sushi-shop.test/sushi")
                        .addDataParameter("name", "Squid Nigiri")
                        .addDataParameter("price", "5.85")
        ).call();
        // docs:form-url-encoded-request-post
    }

    @Test
    public void formUrlEncodedRequest_put_overrideContentType() {
        // docs:form-url-encoded-request-put-override-content-type
        Bastion.request(
                FormUrlEncodedRequest.put("http://sushi-shop.test/booking")
                        .addDataParameter("name", "John Doe")
                        .addDataParameter("timestamp", "2017-02-10T19:00:00Z")
                        .addHeader("X-Manager", "Alice")
                        .overrideContentType(ContentType.APPLICATION_OCTET_STREAM)
        ).call();
        // docs:form-url-encoded-request-put-override-content-type
    }

    @Test
    public void globals() {
        // docs:globals
        Bastion.globals()
                .addHeader("Authorization", "BASIC a3lsZTpwdWxsaWNpbm8=")
                .addQueryParam("diet", "vegetarian")
                .addRouteParam("version", "v2");

        Bastion.globals().clear();
        // docs:globals
    }

    @Test
    public void clearGlobals() {
        // docs:clear-globals
        Bastion.globals().clear();
        // docs:clear-globals
    }

    @Test
    public void statusCodeAssertions() {
        // docs:status-code-assertions
        Bastion.request(GeneralRequest.post("http://sushi-shop.test/greeting", "<b>Hello, sushi lover!</b>"))
                .withAssertions(StatusCodeAssertions.expecting(200)).call();
        // docs:status-code-assertions
    }

    @Test
    public void statusCodeAssertions_multipleArgs() {
        // docs:status-code-assertions-multiple-args
        Bastion.request(GeneralRequest.post("http://sushi-shop.test/greeting", "<b>Hello, sushi lover!</b>"))
                .withAssertions(StatusCodeAssertions.expecting(200, 201, 204)).call();
        // docs:status-code-assertions-multiple-args
    }

    @Test
    public void jsonResponseAssertions() {
        // docs:json-response-assertions
        Bastion.request(GeneralRequest.get("http://sushi-shop.test/reservation/1"))
                .withAssertions(JsonResponseAssertions.fromString(200, "{ \"name\":\"John Doe\", \"timestamp\":\"2016-02-10T21:00:00Z\" }"))
                .call();
        // docs:json-response-assertions
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
            // docs:json-response-assertions-wrong-value
            Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                    .withAssertions(JsonResponseAssertions.fromString(201, "{ " +
                            "\"id\":5, " +
                            "\"name\":\"sashimi\", " +
                            "\"price\":\"EUR 5.60\", " +
                            "\"type\":\"SASHIMI\" " +
                            "}"
                    ).ignoreValuesForProperties("id")).call();
            // docs:json-response-assertions-wrong-value
        }).isInstanceOf(AssertionError.class).hasMessageContaining("[{\"op\":\"replace\",\"path\":\"/price\",\"value\":\"EUR 5.60\"}]");
    }

    @Test
    public void jsonResponseAssertions_ignoreField() {
        // docs:json-response-assertions-ignore-field
        Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                .withAssertions(JsonResponseAssertions.fromString(201, "{ " +
                                "\"id\":5, " +
                                "\"name\":\"sashimi\", " +
                                "\"price\":5.60, " +
                                "\"type\":\"SASHIMI\" " +
                                "}"
                        ).ignoreValuesForProperties("id")
                ).call();
        // docs:json-response-assertions-ignore-field
    }

    @Test
    public void jsonResponseAssertions_fromResource() {
        // docs:json-response-assertions-from-resource
        Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json")
                        .ignoreValuesForProperties("id")
                ).call();
        // docs:json-response-assertions-from-resource
    }

    @Test
    public void jsonSchemaAssertions_fromResource() {
        // docs:json-schema-assertions-from-resource
        Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                .withAssertions(JsonSchemaAssertions.fromResource("classpath:/json/create_sushi_response_schema.json")).call();
        // docs:json-schema-assertions-from-resource
    }

    @Test
    public void jsonSchemaAssertions_fromString() {
        // docs:json-schema-assertions-from-string
        Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                .withAssertions(JsonSchemaAssertions.fromString("{" +
                        "  \"$schema\": \"http://json-schema.org/draft-04/schema#\"," +
                        "  \"type\": \"object\"," +
                        "  \"properties\": {" +
                        "    \"id\": {\n" +
                        "      \"type\": \"integer\"" +
                        "    }" +
                        "  }," +
                        "  \"required\": [" +
                        "    \"id\"" +
                        "  ]" +
                        "}")).call();
        // docs:json-schema-assertions-from-string
    }

    @Test
    public void lambdaAssertions() {
        // docs:lambda-assertions
        Bastion.request(GeneralRequest.post("http://sushi-shop.test/greeting", "<b>Hello, sushi lover!</b>"))
                .withAssertions((statusCode, response, model) -> {
                    assertThat(statusCode).describedAs("Response Status Code").isEqualTo(200);
                    assertThat(response.getHeaders()).describedAs("Response Headers").contains(new ApiHeader("Author", "John Doe"));
                }).call();
        // docs:lambda-assertions
    }

    @Test
    public void bindModel() {
        // docs:bind-model
        Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                .bind(Sushi.class)
                .withAssertions((statusCode, response, model) -> {
                    assertThat(statusCode).isEqualTo(201);
                    assertThat(response.getContentType()).hasValueSatisfying(contentType ->
                            assertThat(contentType.getMimeType()).isEqualToIgnoringCase("application/json")
                    );
                    assertThat(model.getName()).isEqualTo("sashimi");
                }).call();
        // docs:bind-model
    }

    @Test
    public void getResponse() {
        // docs:get-response
        ModelResponse<? extends Sushi> response =
                Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                        .bind(Sushi.class)
                        .call()
                        .getResponse();

        System.out.println(response.getHeaders());
        // docs:get-response
    }

    @Test
    public void getModel() {
        // docs:get-model
        Sushi sushi =
                Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                        .bind(Sushi.class)
                        .call()
                        .getModel();

        System.out.println(sushi.getName());
        // docs:get-model
    }

    @Test
    public void getView() {
        // docs:get-view
        Optional<JsonNode> json =
                Bastion.request(JsonRequest.postFromResource("http://sushi-shop.test/sushi", "classpath:/json/create_sushi_request.json"))
                       .bind(Sushi.class)
                       .call()
                       .getView(JsonNode.class);
        // docs:get-view
    }

}
