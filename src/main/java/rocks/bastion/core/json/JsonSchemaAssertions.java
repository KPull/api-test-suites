package rocks.bastion.core.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.apache.http.entity.ContentType;
import rocks.bastion.core.Assertions;
import rocks.bastion.core.ModelResponse;
import rocks.bastion.core.Response;
import rocks.bastion.core.resource.ResourceLoader;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

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
    private ContentType contentType;

    private JsonSchemaAssertions(String expectedSchema) {
        Objects.requireNonNull(expectedSchema);
        this.expectedSchema = expectedSchema;
        contentType = ContentType.APPLICATION_JSON;
    }

    @Override
    public void execute(int statusCode,
                        ModelResponse<?> response,
                        Object model) throws AssertionError {
        try {
            assertContentTypeHeader(response);
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

    /**
     * The assertions object will initially check that the content-type header returned by the actual response is
     * "application/json". This can be overriden to check for a different content-type header using this method. Despite
     * this, this assertions object will still try to interpret the body as if it were JSON text.
     *
     * @param contentType The expected content-type header
     * @return This object (for method chaining)
     */
    public JsonSchemaAssertions overrideContentType(ContentType contentType) {
        Objects.requireNonNull(contentType);
        this.contentType = contentType;
        return this;
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
            throw new AssertionError(format(
                    "Actual response body is not as specified. The following message(s) where produced during validation; %s.",
                    messages));
        }
    }

    private JsonNode getExpectedSchema() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(expectedSchema);
    }

    private void assertContentTypeHeader(Response response) {
        if (!response.getContentType().isPresent()) {
            throw new AssertionError("Response content-type should not be missing.");
        }
        if (!response.getContentType().get().getMimeType().equals(contentType.getMimeType())) {
            throw new AssertionError(format("Response content-type should be \"%s\" but got \"%s\" instead", contentType.getMimeType(),
                                             response.getContentType().get().getMimeType()));
        }
    }
}
