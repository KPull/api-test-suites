package rocks.bastion.core.builder;

import rocks.bastion.core.Assertions;

/**
 * Defines the operations that can be performed on a Bastion builder before it has been bound to a model type. Binding the
 * builder to a model type allows Bastion to interpret the received response to a Java object of the specified type. Once
 * a builder is bound, later calls on the builder will provide a constructed instance of the model object which was bound.
 * This is useful, for example, if you would like to perform assertions on a Java object rather than a raw HTTP response
 * or a JSON string.
 * <br><br>
 * Since at this point we don't know what type of model the builder was bound to, then there is only one operation defined
 * in this interface, namely:
 * <ul>
 * <li>{@link #bind(Class)}: Specify which class type to use when constructing a model of the received response. Bastion will
 * interpret and decode the received response object into an instance of whatever type is supplied to this method.</li>
 * </ul>
 * Once bound, you will have access to an {@link AssertionsBuilder} which contains the model type you specified in the {@link #bind(Class)}
 * method. This will allow you to continue preparing your test by method chaining.
 */
public interface BindBuilder {

    /**
     * Binds the specified class type to this Bastion builder. Although not necessarily required by Bastion, you should
     * pass in a constructable type (a non-abstract class which can be instantiated) because the various decoders used by
     * Bastion, such as the Jackson JSON parser, only work if it can construct an instance of the model.
     * <br><br>
     * The bound type will be used further on when the user specifies {@link AssertionsBuilder#withAssertions(Assertions)
     * assertions} or outright retrieves the {@link PostExecutionBuilder#getModel() decoded response model}
     * as the bound model (of the correct type) will be provided by Bastion.
     *
     * @param modelType A non-{@literal null} class type which will be used when constructing the response model
     * @param <MODEL>   The type of model to bind this builder to
     * @return A builder bound to the specified model type. Use this to continue chaining method calls and specify your Bastion test even further
     */
    <MODEL> AssertionsBuilder<? extends MODEL> bind(Class<MODEL> modelType);

}
