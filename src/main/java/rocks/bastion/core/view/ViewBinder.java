package rocks.bastion.core.view;

import rocks.bastion.core.Response;

import java.util.List;
import java.util.Objects;

/**
 * Responsible for extracting as many different views as possible of the given response. A view binder is first constructed using a
 * {@link rocks.bastion.core.Response response object} and a list of {@link ResponseDecoder response decoders}. The binder will execute
 * the decoders, in order, and build a map of types to views. If any response decoder returns a mapping which already exists, the new
 * mapping overwrites the earlier mapping.
 */
public final class ViewBinder {

    private final Response response;
    private final List<ResponseDecoder> decoders;

    public ViewBinder(Response response, List<ResponseDecoder> decoders) {
        this.response = Objects.requireNonNull(response);
        this.decoders = Objects.requireNonNull(decoders);
    }

    public Bindings bind(DecodingHints hints) {
        Bindings bindings = new Bindings();
        for (ResponseDecoder decoder : decoders) {
            bindings.addAllBindings(decoder.decode(response, hints));
        }
        return bindings;
    }

}
