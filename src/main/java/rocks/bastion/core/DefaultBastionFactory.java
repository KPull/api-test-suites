package rocks.bastion.core;

import rocks.bastion.core.configuration.Configuration;
import rocks.bastion.core.event.*;
import rocks.bastion.core.view.JsonResponseDecoder;
import rocks.bastion.core.view.ResponseDecoder;
import rocks.bastion.core.view.ResponseDecodersRegistrar;
import rocks.bastion.core.view.StringResponseDecoder;

/**
 * The default implementation of a {@link BastionFactory} which registers a basic set of {@link ResponseDecoder model converters}. Also registers
 * an event listener to Bastion which will cause exceptions and errors thrown during a Bastion request to propagate up the
 * frame stack.
 * <br><br>
 * The set of model converters which are registered by this factory are listed below: <ul>
 * <li>{@link StringResponseDecoder} - Binds the response to a {@link String} model by reading the content
 * inside the HTTP response.
 * <li>{@link JsonResponseDecoder} - Binds an HTTP response with content-type {@code application/json} to
 * a Java-based object using the
 * <a href="https://fasterxml.github.io/jackson-databind/javadoc/2.0.0/com/fasterxml/jackson/databind/ObjectMapper.html">Jackson Object Mappe</a>.</li>
 * </ul>
 */
public class DefaultBastionFactory extends BastionFactory implements BastionListener {

    public DefaultBastionFactory() {
        setConfiguration(new Configuration());
    }

    @Override
    public void callStarted(BastionStartedEvent event) {

    }

    @Override
    public void callFinished(BastionFinishedEvent event) {

    }

    @Override
    public void callFailed(BastionFailureEvent event) {
        new EventLogging(event).logResponseAndRequest();
        throw event.getAssertionError();
    }

    @Override
    public void callError(BastionErrorEvent event) {
        new EventLogging(event).logResponseAndRequest();
        Throwable throwable = event.getThrowable();
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        } else if (throwable instanceof Error) {
            throw (Error) throwable;
        } else {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    protected void prepareBastion(BastionBuilderImpl<?> bastion) {
        registerModelConverters(bastion);
        bastion.registerListener(this);
    }

    protected void registerModelConverters(ResponseDecodersRegistrar bastion) {
        bastion.registerModelConverter(new JsonResponseDecoder());
        bastion.registerModelConverter(new StringResponseDecoder());
    }

}
