package rocks.bastion.core.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import rocks.bastion.core.Response;

import java.io.IOException;

/**
 * A {@link ResponseDecoder} which will interpret an HTTP response containing JSON content body. This implementation uses
 * the Jackson library's {@link ObjectMapper} to perform the decoding operation.
 * <p>
 * The decoder creates the following views: first, it
 * parses the HTTP response's JSON content into a {@link JsonNode} which is abstract syntax tree representing the given JSON
 * (known as a JSON tree). Then, if the user has supplied a target model type, it attempts to bind the JSON tree into an instance
 * of that type (and the entire hierarchy of the given type).
 * </p>
 */
public class JsonResponseDecoder implements ResponseDecoder {

    private static ObjectMapper jsonObjectMapper = null;

    @Override
    public Bindings decode(Response response, DecodingHints hints) {
        ContentType responseContentType = response.getContentType().orElse(ContentType.DEFAULT_TEXT);
        if (!supportsContentType(responseContentType)) {
            return new Bindings();
        }
        try {
            Bindings bindings = new Bindings();
            JsonNode decodedJsonTree;
            decodedJsonTree = getObjectMapper().readTree(response.getBody());
            bindings.addAllBindings(Bindings.hierarchy(JsonNode.class, decodedJsonTree));
            bindings.addAllBindings(decodeTreeUsingHints(decodedJsonTree, hints));
            return bindings;
        } catch (JsonProcessingException ignored) {
            return new Bindings();
        } catch (IOException exception) {
            throw new IllegalStateException("An unexpected error occurred while reading JSON data", exception);
        }
    }

    private static synchronized ObjectMapper getObjectMapper() {
        if (jsonObjectMapper == null) {
            jsonObjectMapper = new ObjectMapper();
        }
        return jsonObjectMapper;
    }

    @SuppressWarnings("unchecked")
    private Bindings decodeTreeUsingHints(TreeNode decodedJsonTree, DecodingHints hints) {
        return hints.getModelType().map(modelType -> {
            try {
                return Bindings.hierarchy((Class<? super Object>) modelType, getObjectMapper().treeToValue(decodedJsonTree, modelType));
            } catch (JsonProcessingException ignored) {
                return null;
            }
        }).orElse(new Bindings());
    }

    private boolean supportsContentType(ContentType responseContentType) {
        return responseContentType.getMimeType().equals(ContentType.APPLICATION_JSON.getMimeType());
    }
}
