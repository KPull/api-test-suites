package rocks.bastion.core.builder;

/**
 * Specifies the operations available on a Bastion test builder before it has been executed.
 * At this point, a user can only perform the following operation:
 * <ul>
 * <li>{@link #call()}: Starts the Bastion test by executing the HTTP request.</li>
 * </ul>
 * After using the {@linkplain #call()} method, the user may obtain the response, for further use in the ongoing test, using
 * methods defined in the {@link PostExecutionBuilder} interface.
 */
public interface ExecuteRequestBuilder<MODEL> {

    /**
     * Instructs Bastion to perform the HTTP request, decode the response into a model and perform any assertions.
     * <br><br>
     * If the call was successful, you can obtain the response received by Bastion by chaining further methods to this
     * method. Notably, the {@link PostExecutionBuilder#getModel()} method will return the decoded model and
     * {@link PostExecutionBuilder#getResponse()} will return the complete response object (including the model) which represents the
     * HTTP information.
     *
     * @return A Bastion fluent-builder which allows you to retrieve the HTTP response and the decoded model
     */
    PostExecutionBuilder<? extends MODEL> call();

}
