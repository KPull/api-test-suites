package rocks.bastion.core.assertions;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;
import rocks.bastion.core.ModelResponse;
import rocks.bastion.core.json.InvalidJsonException;
import rocks.bastion.core.json.JsonResponseAssertions;

import java.util.HashMap;

/**
 * @author <a href="mailto:mail@kylepullicino.com">Kyle</a>
 */
public class JsonResponseAssertionsTest {

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