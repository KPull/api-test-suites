package rocks.bastion.core.builder;

import rocks.bastion.core.Assertions;

/**
 * Defines the operations which a user can perform on a Bastion builder before any {@link Assertions assertions} object is added to the test.
 * At this point, a user may perform one of the following:
 * <ul>
 * <li>{@link #withAssertions(Assertions)}: Specify what test assertions to apply to the response. We recommend supplying the
 * assertions as a lambda or using one of the available subclass implementations of {@link Assertions}.</li>
 * <li>{@link #call()}: Starts the Bastion test by executing the HTTP request.</li>
 * </ul>
 * <p>
 * After using the {@linkplain #call()} method, the user may obtain the response, for further use in the ongoing test, using
 * methods defined in the {@link PostExecutionBuilder} interface.
 */
public interface AssertionsBuilder<MODEL> extends ExecuteRequestBuilder<MODEL> {

    /**
     * Add an assertions object which determines whether the received response is as expected. We recommend
     * supplying the {@linkplain Assertions} object as a lambda or using one of the convenience implementations of {@linkplain Assertions}
     * (such as {@link rocks.bastion.core.json.JsonResponseAssertions}) that will take care of the most common verifications that
     * a user would want to perform on a request.
     * <br><br>
     * If this method is not called on the builder, then a {@link Assertions#noAssertions() no-operation assertions object} which
     * always pass are set by default.
     *
     * @param assertions A non-{@literal null} {@linkplain Assertions} object
     * @return A fluent-builder which will allow you to execute the HTTP request and retrieve the response information
     */
    ExecuteRequestBuilder<? extends MODEL> withAssertions(Assertions<? super MODEL> assertions);

}
