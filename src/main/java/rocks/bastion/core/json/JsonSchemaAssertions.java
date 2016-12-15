package rocks.bastion.core.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import rocks.bastion.core.Assertions;
import rocks.bastion.core.ModelResponse;
import rocks.bastion.core.resource.ResourceLoader;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Asserts that an API response conforms to a given JSON schema.
 */
public final class JsonSchemaAssertions implements Assertions<Object> {

    public static JsonSchemaAssertions fromString(String expectedSchemaJson) {
        return new JsonSchemaAssertions(expectedSchemaJson);
    }

    public static JsonSchemaAssertions fromResource(String expectedSchemaSource) {
        Objects.requireNonNull(expectedSchemaSource);
        return new JsonSchemaAssertions(new ResourceLoader(expectedSchemaSource).load());
    }

    private String expectedSchema;

    private JsonSchemaAssertions(String expectedSchema) {
        Objects.requireNonNull(expectedSchema);
        this.expectedSchema = expectedSchema;
    }

    @Override
    public void execute(int statusCode,
                        ModelResponse<?> response,
                        Object model) throws AssertionError {
        try {
            JsonNode jsonNodeOfResponse = convertResponseToJsonNode(response);
            assertResponseConformsToSchema(jsonNodeOfResponse);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while parsing JSON text", e);
        } catch (InvalidSchemaException e) {
            throw new InvalidJsonException("The given text is not a valid JSON schema", e, expectedSchema);
        } catch (ProcessingException e) {
            throw new RuntimeException("An unknown error occurred while processing the JSON schema and API response", e);
        }
    }

    private JsonNode convertResponseToJsonNode(ModelResponse<?> response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        return factory.createParser(response.getBody()).readValueAsTree();
    }

    private void assertResponseConformsToSchema(JsonNode response) throws ProcessingException, IOException {
        ProcessingReport validationReport = JsonSchemaFactory.byDefault()
                .getJsonSchema(getExpectedSchema()).validate(response);
        if (!validationReport.isSuccess()) {
            String messages = StreamSupport.stream(validationReport.spliterator(), false)
                    .map(ProcessingMessage::getMessage)
                    .collect(Collectors.joining(", "));
            throw new AssertionError(String.format("Actual response body is not as specified. The following message(s) where produced during validation; %s.", messages));
        }
    }

    private JsonNode getExpectedSchema() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(expectedSchema);
    }

}
