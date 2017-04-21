package rocks.bastion.core.assertions;

import com.google.common.collect.Maps;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import rocks.bastion.core.ModelResponse;
import rocks.bastion.core.json.InvalidJsonException;
import rocks.bastion.core.json.JsonResponseAssertions;
import rocks.bastion.support.embedded.Sushi;

import java.util.HashMap;

/**
 * @author <a href="mailto:mail@kylepullicino.com">Kyle</a>
 */
public class JsonResponseAssertionsTest {

    @Test
    public void fromModel_differentValue_assertShouldFail() {
        Assertions.assertThatThrownBy(() -> {
            Sushi expectedModel = Sushi.newSushi().id(999).name("Salmon Nigiri").type(Sushi.Type.NIGIRI).price(50L).build();
            ModelResponse<String> response = TestModelResponse.prepare("{\"id\":1,\"name\":\"Salmon Nigiri\",\"type\":\"NIGIRI\",\"price\":50}");
            JsonResponseAssertions assertions = JsonResponseAssertions.fromModel(200, expectedModel);
            assertions.execute(200, response, response.getModel());
        }).hasMessage("Actual response body is not as expected. The following JSON Patch (as per RFC-6902) tells you what operations you need to perform to transform the actual response body into the expected response body:\n" +
                " [{\"op\":\"replace\",\"path\":\"/id\",\"value\":999}]");
    }

    @Test
    public void fromModel_sameAsExpectedModel_shouldAssertSuccessfully() {
            Sushi expectedModel = Sushi.newSushi().id(1).name("Salmon Nigiri").type(Sushi.Type.NIGIRI).price(50L).build();
            ModelResponse<String> response = TestModelResponse.prepare("{\"id\":1,\"name\":\"Salmon Nigiri\",\"type\":\"NIGIRI\",\"price\":50}");
            JsonResponseAssertions assertions = JsonResponseAssertions.fromModel(200, expectedModel);
            assertions.execute(200, response, response.getModel());
    }

    @Test(expected = InvalidJsonException.class)
    public void fromString_invalidJson_shouldThrowException() throws Exception {
        JsonResponseAssertions.fromString(200, "{ \"key\":\"kyle\", \"surname\":\"pullicino\" ");
    }

    @Test
    public void execute_ignoredField_shouldAssertSuccessfully() throws Exception {
        JsonResponseAssertions assertions = JsonResponseAssertions.fromString(200, "{ \"key\":\"kyle\", \"surname\":\"pullicino\" }").ignoreValuesForProperties("/key");
        ModelResponse<String> response = TestModelResponse.prepare("{ \"key\":\"kyle1\", \"surname\":\"pullicino\" }");
        assertions.execute(200, response, response.getModel());
    }

    @Test
    public void execute_ignoredOrderForArrayField_shouldAssertSuccessfully() throws Exception {
        JsonResponseAssertions assertions = JsonResponseAssertions.fromString(200, "{ \"array\":[\"first\",\"second\",\"third\"] }").ignoreOrderForArrayProperties("/array");
        ModelResponse<String> response = TestModelResponse.prepare("{ \"array\":[\"third\",\"first\",\"second\"] }");
        assertions.execute(200, response, response.getModel());
    }

    @Test
    public void execute_notIgnoredOrderForArrayFieldDisorderedAssertion_shouldThrowErrorWithDiff() throws Exception {
        try {
            JsonResponseAssertions assertions = JsonResponseAssertions.fromString(200, "{ \"array\":[\"first\",\"second\",\"third\"] }");
            ModelResponse<String> response = TestModelResponse.prepare("{ \"array\":[\"third\",\"first\",\"second\"] }");
            assertions.execute(200, response, response.getModel());
        } catch (AssertionError assertionError) {
            Assert.assertEquals("Assertion Failed Message", assertionError.getMessage(), "Actual response body is not as expected. The following JSON Patch (as per RFC-6902) tells you what operations you need to perform to transform the actual response body into the expected response body:" +
                    "\n" +
                    " [{\"op\":\"move\",\"path\":\"/array/2\",\"from\":\"/array/0\"}]");
            return;
        }
        Assert.fail("An assertion error should have been thrown by the JSON Response Assertions");
    }

    @Test
    public void execute_ignoredOrderForArrayFieldMissingElement_shouldThrowErrorWithDiff() throws Exception {
        try {
            JsonResponseAssertions assertions = JsonResponseAssertions.fromString(200, "{ \"array\":[\"first\",\"second\",\"third\"] }").ignoreOrderForArrayProperties("/array");
            ModelResponse<String> response = TestModelResponse.prepare("{ \"array\":[\"third\",\"first\"] }");
            assertions.execute(200, response, response.getModel());
        } catch (AssertionError assertionError) {
            Assert.assertEquals("Assertion Failed Message", assertionError.getMessage(), "Actual response body is not as expected. The following JSON Patch (as per RFC-6902) tells you what operations you need to perform to transform the actual response body into the expected response body:" +
                    "\n" +
                    " [{\"op\":\"add\",\"path\":\"/array/1\",\"value\":\"second\"}]");
            return;
        }
        Assert.fail("An assertion error should have been thrown by the JSON Response Assertions");
    }

    @Test
    public void execute_ignoredOrderForArrayFieldExtraElement_shouldThrowErrorWithDiff() throws Exception {
        try {
            JsonResponseAssertions assertions = JsonResponseAssertions.fromString(200, "{ \"array\":[\"first\",\"second\",\"third\"] }").ignoreOrderForArrayProperties("/array");
            ModelResponse<String> response = TestModelResponse.prepare("{ \"array\":[\"third\",\"first\",\"second\",\"fourth\"] }");
            assertions.execute(200, response, response.getModel());
        } catch (AssertionError assertionError) {
            Assert.assertEquals("Assertion Failed Message", assertionError.getMessage(), "Actual response body is not as expected. The following JSON Patch (as per RFC-6902) tells you what operations you need to perform to transform the actual response body into the expected response body:" +
                    "\n" +
                    " [{\"op\":\"remove\",\"path\":\"/array/0\"},{\"op\":\"replace\",\"path\":\"/array/2\",\"value\":\"third\"}]");
            return;
        }
        Assert.fail("An assertion error should have been thrown by the JSON Response Assertions");
    }

    @Test
    public void execute_fromStringJsonMismatches_shouldThrowErrorWithDiff() throws Exception {
        try {
            JsonResponseAssertions assertions = JsonResponseAssertions.fromString(200, "{ \"key\":\"kyle\", \"surname\":\"pullicino\" }");
            ModelResponse<String> response = TestModelResponse.prepare("{ \"key\":\"kyle1\", \"surname\":\"pullicino\", \"array\":[1, 2] }");
            assertions.execute(200, response, response.getModel());
        } catch (AssertionError assertionError) {
            Assert.assertEquals("Assertion Failed Message", assertionError.getMessage(), "Actual response body is not as expected. The following JSON Patch (as per RFC-6902) tells you what operations you need to perform to transform the actual response body into the expected response body:" +
                    "\n" +
                    " [{\"op\":\"replace\",\"path\":\"/key\",\"value\":\"kyle\"},{\"op\":\"remove\",\"path\":\"/array\"}]");
            return;
        }
        Assert.fail("An assertion error should have been thrown by the JSON Response Assertions");
    }

    @Test
    public void execute_fromFileJsonMismatches_shouldThrowErrorWithDiff() throws Exception {
        try {
            JsonResponseAssertions assertions = JsonResponseAssertions.fromResource(200, "classpath:/rocks/bastion/core/assertions/test-body.json");
            ModelResponse<String> response = TestModelResponse.prepare("{\n" +
                    "  \"name\": \"john\",\n" +
                    "  \"timestamp1\": \"2016-10-15T20:00:25+0100\",\n" +
                    "  \"colours\": [\"blue\"],\n" +
                    "  \"favourites\": {\n" +
                    "    \"food\": \"apples\",\n" +
                    "    \"number\": 23,\n" +
                    "    \"country\": \"Spain\"" +
                    "  }\n" +
                    "}");
            assertions.execute(200, response, response.getModel());
        } catch (AssertionError assertionError) {
            Assert.assertEquals("Assertion Failed Message", assertionError.getMessage(), "Actual response body is not as expected. The following JSON Patch (as per RFC-6902) tells you what operations you need to perform to transform the actual response body into the expected response body:\n" +
                    " [{\"op\":\"move\",\"path\":\"/timestamp\",\"from\":\"/timestamp1\"},{\"op\":\"remove\",\"path\":\"/colours\"},{\"op\":\"remove\",\"path\":\"/favourites/country\"},{\"op\":\"add\",\"path\":\"/favourites/colours\",\"value\":[\"blue\",\"red\"]}]");
            return;
        }
        Assert.fail("An assertion error should have been thrown by the JSON Response Assertions");
    }

    @Test
    public void execute_fromTemplateJsonMismatches_shouldThrowErrorWithDiff() throws Exception {
        try {
            HashMap<String, String> variableAssignments = Maps.newHashMap();
            variableAssignments.put("name", "john");
            JsonResponseAssertions assertions = JsonResponseAssertions.fromTemplate(200, "classpath:/rocks/bastion/core/assertions/test-template-body.json", variableAssignments);
            ModelResponse<String> response = TestModelResponse.prepare("{\n" +
                    "  \"name\": \"john\",\n" +
                    "  \"timestamp1\": \"2016-10-15T20:00:25+0100\",\n" +
                    "  \"colours\": [\"blue\"],\n" +
                    "  \"favourites\": {\n" +
                    "    \"food\": \"apples\",\n" +
                    "    \"number\": 23,\n" +
                    "    \"country\": \"Spain\"" +
                    "  }\n" +
                    "}");
            assertions.execute(200, response, response.getModel());
        } catch (AssertionError assertionError) {
            Assert.assertEquals("Assertion Failed Message", assertionError.getMessage(), "Actual response body is not as expected. The following JSON Patch (as per RFC-6902) tells you what operations you need to perform to transform the actual response body into the expected response body:\n" +
                    " [{\"op\":\"move\",\"path\":\"/timestamp\",\"from\":\"/timestamp1\"},{\"op\":\"remove\",\"path\":\"/colours\"},{\"op\":\"remove\",\"path\":\"/favourites/country\"},{\"op\":\"add\",\"path\":\"/favourites/colours\",\"value\":[\"blue\",\"red\"]}]");
            return;
        }
        Assert.fail("An assertion error should have been thrown by the JSON Response Assertions");
    }

}