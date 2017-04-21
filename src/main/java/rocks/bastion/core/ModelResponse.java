package rocks.bastion.core;

import org.apache.http.entity.ContentType;
import rocks.bastion.core.view.Bindings;

import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an HTTP response which also has a response model object and a number of views. The views are bound from the
 * content/body of the HTTP response.
 *
 * @param <MODEL> The model object type which was bound for this HTTP response.
 */
public class ModelResponse<MODEL> implements Response {

    private final Response response;
    private final MODEL model;
    private final Bindings views;

    public ModelResponse(Response response, MODEL model, Bindings views) {
        this.response = Objects.requireNonNull(response);
        this.model = model;
        this.views = Objects.requireNonNull(views);
    }

    @Override
    public Optional<ContentType> getContentType() {
        return response.getContentType();
    }

    @Override
    public int getStatusCode() {
        return response.getStatusCode();
    }

    @Override
    public String getStatusText() {
        return response.getStatusText();
    }

    @Override
    public Collection<ApiHeader> getHeaders() {
        return response.getHeaders();
    }

    @Override
    public InputStream getBody() {
        return response.getBody();
    }

    /**
     * Returns the designated model of this response. The model is one of the response's views which was chosen using an earlier call to
     * {@link rocks.bastion.core.builder.BindBuilder#bind(Class)}.
     *
     * @return The bound model of this response, if any
     */
    public MODEL getModel() {
        return model;
    }

    /**
     * Returns a view of this response. The view returned is of the specified type. Views are Java objects which represent the data
     * returned in the HTTP response. For example, a JSON response would contain a JSON tree as one of the views.
     *
     * @param viewType The class type of view to return
     * @param <T> The type of the returned view
     * @return An optional container containing a view of the specified type, if any was bound
     */
    public <T> Optional<T> getView(Class<T> viewType) {
        return views.getViewForType(viewType);
    }
}
