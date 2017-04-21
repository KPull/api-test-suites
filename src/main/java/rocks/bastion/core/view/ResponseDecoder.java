package rocks.bastion.core.view;

import rocks.bastion.core.Response;

/**
 * Interprets and decodes an HTTP response into a model object. Bastion will ask {@linkplain ResponseDecoder}s to decode the
 * HTTP response if possible. A typical implementation will first look at the response's {@code Content-type} header and attempt
 * to generate some sort of object. If the decoder is unable to decode the HTTP response into a Java object
 * (because it cannot interpret the given {@code Content-type}, for example), then then it should return an {@link Bindings
 * empty Bindings object}.
 * <br><br>
 * Once registered with Bastion, the {@linkplain ResponseDecoder}s will form a strategy for turning an arbitrary HTTP response
 * to a number of usable Java object (so-called views) available in Bastion tests and assertions.
 */
public interface ResponseDecoder {

    /**
     * Attempts to decode the given HTTP response to a number of Java objects (ie. views). If this decoder is unable to decode the
     * HTTP response, then it should return an {@link Bindings empty Bindings object}. Otherwise, it should return
     * {@link Bindings bindings for all possible views of the given response}.
     * <p>
     * A user might have supplied a <i>target</i> object type it wants the decoded information bound to. In this case,
     * the target model type to bind to will be given in the {@code hints} parameter, accessible using the
     * {@link DecodingHints#getModelType()} method.
     *
     * @param response The HTTP response for the converter to decode
     * @param hints    Additional hints which can be used for decoding the provided response
     * @return A non-{@literal null} {@link Bindings} object
     */
    Bindings decode(Response response, DecodingHints hints);
}
