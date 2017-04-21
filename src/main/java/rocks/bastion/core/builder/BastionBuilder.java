package rocks.bastion.core.builder;

import rocks.bastion.Bastion;
import rocks.bastion.core.Assertions;
import rocks.bastion.core.HttpRequest;

/**
 * The first interface for the Bastion fluent-builder allowing the user to {@link #bind(Class) bind} a model type, apply {@link #withAssertions(Assertions) assertions}
 * and {@link #call() initiate} the test. This interface serves to group together all the options for Bastion users
 * immediately after invoking the Bastion builder using {@link Bastion#request(String, HttpRequest)}. Using this builder, a user can:
 * <ul>
 * <li>{@link #bind(Class)}: Specify which class type to use when constructing a model of the received response. Bastion will
 * interpret and decode the received response object into an instance of whatever type is supplied to this method.</li>
 * <li>{@link #withAssertions(Assertions)}: Specify what test assertions to apply to the response. We recommend supplying the
 * assertions as a lambda or using one of the available subclass implementations of {@link Assertions}.</li>
 * <li>{@link #call()}: Starts the Bastion test by executing the HTTP request.</li>
 * </ul>
 * After using the {@linkplain #call()} method, the user may obtain the response, for further use in the ongoing test, using
 * methods defined in the {@link PostExecutionBuilder} interface.
 * <br>
 * Note that the user must perform the steps detailed above, in
 * the order they are listed: the user may skip any of the steps but they cannot perform steps out of order (for example, if you
 * supply an assertion lambda using {@linkplain #withAssertions(Assertions)} then you cannot use the {@linkplain #bind(Class)} method afterwards.
 *
 * @param <MODEL> The model type which was bound with the {@link #bind(Class)} method.
 */
public interface BastionBuilder<MODEL> extends BindBuilder, AssertionsBuilder<MODEL> {
}
