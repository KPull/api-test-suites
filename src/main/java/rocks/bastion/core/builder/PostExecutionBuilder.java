package rocks.bastion.core.builder;

import rocks.bastion.core.ModelResponse;

import java.util.Optional;

/**
 * Specifies the operations available on a Bastion fluent-builder after the test has been executed with {@link ExecuteRequestBuilder#call()} and
 * a response has been obtained. At this point, Bastion will have obtained a response and decoded the response body into
 * some sort of Java object known as the model.
 * <br><br>
 * If a type has been bound to this builder using {@link BindBuilder#bind(Class)}, then the model given by {@link #getModel()}
 * will be of the specified type. Otherwise, the type of model will be determined by Bastion itself depending on the content-type
 * of the response.
 *
 * @param <MODEL> The type of model bound to this builder
 */
public interface PostExecutionBuilder<MODEL> {

    /**
     * Returns the model object that was decoded from the received HTTP response. If a type has been bound to this builder
     * using {@link BindBuilder#bind(Class)}, then the model given by this method
     * will be of the specified type. Otherwise, the type of model will be determined by Bastion itself depending on the content-type
     * of the response.
     *
     * @return The decoded model object
     */
    MODEL getModel();

    /**
     * Returns the complete HTTP response as received. This response will contain the HTTP status code, the content-type
     * and any HTTP headers. The response will also contain the raw response content as well as the decoded model object
     * (which you can get directly using {@link #getModel()}).
     *
     * @return The full HTTP response object
     */
    ModelResponse<? extends MODEL> getResponse();

    /**
     * Gets a particular view of the response. A view is a Java object which represents the data received in the response. This allows
     * users to get a different type of object other than the one provided in the {@link BindBuilder#bind(Class) bind()} method. Note that
     * the model object returned by {@link #getModel()} would be one of the views of the response.
     *
     * @param viewType The class of the view object you want returned from the response
     * @param <T> The type of view returned by this method
     * @return An optional container with the view of the received response of the specified type, if any
     */
    <T> Optional<T> getView(Class<T> viewType);
}
