package rocks.bastion.core.assertions;

import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;
import rocks.bastion.core.ModelResponse;
import rocks.bastion.core.json.InvalidJsonException;
import rocks.bastion.core.json.JsonSchemaAssertions;

public class JsonSchemaAssertionsTest {

    @Test
    public void execute_fromStringSchemaMismatch_assertionErrorShouldBeThrown() {
        try {
            final JsonSchemaAssertions assertions = JsonSchemaAssertions.fromString("{\n" +
                                                                                    "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                                                                                    "  \"type\": \"object\",\n" +
                                                                                    "  \"properties\": {\n" +
                                                                                    "    \"id\": {\n" +
                                                                                    "      \"type\": \"integer\"\n" +
                                                                                    "    }\n" +
                                                                                    "  },\n" +
                                                                                    "  \"required\": [\n" +
                                                                                    "    \"id\"\n" +
                                                                                    "  ]\n" +
                                                                                    "}\n");
            ModelResponse<String> response = TestModelResponse.prepare("{ \"number\":\"21\" }");
            assertions.execute(201, response, response.getModel());
        } catch (AssertionError assertionError) {
            Assert.assertEquals("Assertion Failed Message",
                                "Actual response body is not as specified. The following message(s) where produced during validation; object has missing required properties ([\"id\"]).",
                                assertionError.getMessage());
            return;
        }

        Assert.fail("An assertion error should have been thrown by the JSON Schema Assertions");
    }

    @Test
    public void execute_fromStringContentTypeMismatch_assertionErrorShouldBeThrown() {
        try {
            final JsonSchemaAssertions assertions = JsonSchemaAssertions.fromString("{\n" +
                                                                                    "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                                                                                    "  \"type\": \"object\",\n" +
                                                                                    "  \"properties\": {\n" +
                                                                                    "    \"id\": {\n" +
                                                                                    "      \"type\": \"integer\"\n" +
                                                                                    "    }\n" +
                                                                                    "  },\n" +
                                                                                    "  \"required\": [\n" +
                                                                                    "    \"id\"\n" +
                                                                                    "  ]\n" +
                                                                                    "}\n");
            ModelResponse<String> response = TestModelResponse.prepare("{ \"id\": 21 }", "text/plain");
            assertions.execute(201, response, response.getModel());
        } catch (AssertionError assertionError) {
            Assert.assertEquals("Assertion Failed Message",
                                "Content-type MIME type expected:<[application/jso]n> but was:<[text/plai]n>",
                                assertionError.getMessage());
            return;
        }

        Assert.fail("An assertion error should have been thrown by the JSON Schema Assertions");
    }

    @Test
    public void execute_fromStringSchemaMatches_shouldAssertSuccessfully() {
        final JsonSchemaAssertions assertions = JsonSchemaAssertions.fromString("{\n" +
                                                                                "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                                                                                "  \"type\": \"object\",\n" +
                                                                                "  \"properties\": {\n" +
                                                                                "    \"id\": {\n" +
                                                                                "      \"type\": \"integer\"\n" +
                                                                                "    }\n" +
                                                                                "  },\n" +
                                                                                "  \"required\": [\n" +
                                                                                "    \"id\"\n" +
                                                                                "  ]\n" +
                                                                                "}\n");
        ModelResponse<String> response = TestModelResponse.prepare("{ \"id\": 21 }");
        assertions.execute(201, response, response.getModel());
    }

    @Test
    public void execute_fromStringSchemaAndContentTypeMatches_shouldAssertSuccessfully() {
        final JsonSchemaAssertions assertions = JsonSchemaAssertions.fromString("{\n" +
                                                                                "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                                                                                "  \"type\": \"object\",\n" +
                                                                                "  \"properties\": {\n" +
                                                                                "    \"id\": {\n" +
                                                                                "      \"type\": \"integer\"\n" +
                                                                                "    }\n" +
                                                                                "  },\n" +
                                                                                "  \"required\": [\n" +
                                                                                "    \"id\"\n" +
                                                                                "  ]\n" +
                                                                                "}\n");
        assertions.overrideContentType(ContentType.TEXT_PLAIN);
        ModelResponse<String> response = TestModelResponse.prepare("{ \"id\": 22 }", "text/plain");
        assertions.execute(201, response, response.getModel());
    }

    @Test(expected = InvalidJsonException.class)
    public void execute_fromStringSchemaIsInvalid_shouldThrowException() {
        final JsonSchemaAssertions assertions = JsonSchemaAssertions.fromString("{\n" +
                                                                                "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                                                                                "  \"type\": \"object\",\n" +
                                                                                "  \"properties\": {\n" +
                                                                                "    \"id\": {\n" +
                                                                                "      \"type\": \"fake-type\"\n" +
                                                                                "    }\n" +
                                                                                "  },\n" +
                                                                                "  \"required\": [\n" +
                                                                                "    \"id\"\n" +
                                                                                "  ]\n" +
                                                                                "}\n");
        ModelResponse<String> response = TestModelResponse.prepare("{ \"id\":\"21\" }");
        assertions.execute(201, response, response.getModel());
    }

}