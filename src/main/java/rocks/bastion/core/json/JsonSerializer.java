package rocks.bastion.core.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serializes an {@link Object} into its String representation.
 */
final class JsonSerializer {

    private final Object source;
    private final ObjectMapper objectMapper;

    public JsonSerializer(Object source) {
        this.source = source;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Serializes the source object into a JSON {@link String} representation.
     *
     * @return The JSON {@link String} representation of the source object.
     * @throws JsonSerializationException if an unexpected exception is thrown during serialization
     */
    public String serialize() throws JsonSerializationException {
        try {
            return objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException(e);
        }
    }
}
