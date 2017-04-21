package rocks.bastion.core.json;

import static java.lang.String.format;

/**
 * Indicates that an object could not be serialized due to an unforeseen error.
 */
public final class JsonSerializationException extends RuntimeException {

    private final Object object;

    public JsonSerializationException(Object object){
        super(format("The provided object could not be serialized: %s", object));
        this.object = object;
    }

    public JsonSerializationException(String message, Object object) {
        super(message);
        this.object = object;
    }

    public JsonSerializationException(String message, Throwable cause, Object object) {
        super(message, cause);
        this.object = object;
    }

    public JsonSerializationException(Throwable cause, Object object) {
        super(format("The provided object could not be serialized: %s", object), cause);
        this.object = object;
    }

    /**
     * Gets the object which could not be serialized into valid JSON.
     *
     * @return Invalid object
     */
    public Object getObject() {
        return object;
    }

}
