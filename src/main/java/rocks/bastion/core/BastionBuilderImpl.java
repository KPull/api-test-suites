package rocks.bastion.core;

import com.google.common.base.Strings;
import rocks.bastion.core.builder.*;
import rocks.bastion.core.configuration.Configuration;
import rocks.bastion.core.event.*;
import rocks.bastion.core.view.*;

import java.util.*;

import static java.lang.String.format;

/**
 * The standard implementation of the {@link BastionBuilder}. Will keep state of the various attributes that a user supplies to Bastion
 * when specifying a request. Bastion users will not typically have to deal with this class directly but will instead interact with
 * Bastion through the various builder interfaces and the front facing {@link rocks.bastion.Bastion Bastion facade}.
 *
 * @param <MODEL> The response model type currently bound to the builder
 */
public class BastionBuilderImpl<MODEL> implements BastionBuilder<MODEL>, ResponseDecodersRegistrar, BastionEventPublisher, PostExecutionBuilder<MODEL> {

    private State currentState;
    private String message;
    private Collection<BastionListener> bastionListenerCollection;
    private List<ResponseDecoder> modelConverters;
    private HttpRequest request;
    private Class<MODEL> modelType;
    private boolean suppressAssertions;
    private Assertions<? super MODEL> assertions;
    private MODEL model;
    private Bindings bindings;
    private ModelResponse<MODEL> modelResponse;
    private Configuration configuration;

    BastionBuilderImpl(String message, HttpRequest request) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(request);
        bastionListenerCollection = new LinkedList<>();
        modelConverters = new LinkedList<>();
        this.message = message;
        this.request = request;
        modelType = null;
        model = null;
        bindings = new Bindings();
        suppressAssertions = false;
        assertions = Assertions.noAssertions();
        currentState = State.INITIALISED;
    }

    public void addBastionListener(BastionListener newListener) {
        bastionListenerCollection.add(newListener);
    }

    /**
     * Sets whether assertions should be suppressed for this Bastion request. When assertions
     * are suppressed, Bastion will not execute whatever assertions were passed in to the {@link #withAssertions(Assertions)}
     * method.
     *
     * @param suppressAssertions {@literal true} to suppress assertions; {@literal false}, otherwise.
     */
    public void setSuppressAssertions(boolean suppressAssertions) {
        this.suppressAssertions = suppressAssertions;
    }

    @Override
    public void registerListener(BastionListener listener) {
        bastionListenerCollection.add(listener);
    }

    @Override
    public void notifyListenersCallStarted(BastionStartedEvent event) {
        Objects.requireNonNull(event);
        bastionListenerCollection.forEach(listener -> listener.callStarted(event));
    }

    @Override
    public void notifyListenersCallFailed(BastionFailureEvent event) {
        Objects.requireNonNull(event);
        bastionListenerCollection.forEach(listener -> listener.callFailed(event));
    }

    @Override
    public void notifyListenersCallError(BastionErrorEvent event) {
        Objects.requireNonNull(event);
        bastionListenerCollection.forEach(listener -> listener.callError(event));
    }

    @Override
    public void notifyListenersCallFinished(BastionFinishedEvent event) {
        Objects.requireNonNull(event);
        bastionListenerCollection.forEach(listener -> listener.callFinished(event));
    }

    @Override
    public PostExecutionBuilder<? extends MODEL> call() {
        transitionToState(State.ASSERTIONS, State.EXECUTED);
        modelResponse = null;
        Response response = null;
        try {
            notifyListenersCallStarted(new BastionStartedEvent(request));
            response = new RequestExecutor(request, getConfiguration()).execute();
            bindings = decodeBindings(response);
            model = extractModel();
            modelResponse = new ModelResponse<>(response, model, bindings);
            executeAssertions(modelResponse);
            return this;
        } catch (AssertionError error) {
            notifyListenersCallFailed(new BastionFailureEvent(request, response, error));
            return this;
        } catch (Throwable throwable) {
            notifyListenersCallError(new BastionErrorEvent(request, response, throwable));
            return this;
        } finally {
            notifyListenersCallFinished(new BastionFinishedEvent(request, response));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> AssertionsBuilder<? extends T> bind(Class<T> modelType) {
        Objects.requireNonNull(modelType);
        transitionToState(State.INITIALISED, State.BOUND);
        BastionBuilderImpl<T> castedBuilder = (BastionBuilderImpl<T>) this;
        castedBuilder.modelType = modelType;
        return castedBuilder;
    }

    @Override
    public ExecuteRequestBuilder<? extends MODEL> withAssertions(Assertions<? super MODEL> assertions) {
        Objects.requireNonNull(assertions);
        transitionToState(State.BOUND, State.ASSERTIONS);
        this.assertions = assertions;
        return this;
    }

    @Override
    public MODEL getModel() {
        return model;
    }

    @Override
    public ModelResponse<? extends MODEL> getResponse() {
        return modelResponse;
    }

    @Override
    public <T> Optional<T> getView(Class<T> viewType) {
        return bindings.getViewForType(viewType);
    }

    @Override
    public void registerModelConverter(ResponseDecoder decoder) {
        Objects.requireNonNull(decoder);
        modelConverters.add(decoder);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    private String getDescriptiveText() {
        if (Strings.isNullOrEmpty(message)) {
            return request.name();
        } else {
            return request.name() + " - " + message;
        }
    }

    private void executeAssertions(ModelResponse<MODEL> modelResponse) {
        if (!suppressAssertions) {
            assertions.execute(modelResponse.getStatusCode(), modelResponse, modelResponse.getModel());
        }
    }

    private Bindings decodeBindings(Response response) {
        ViewBinder binder = new ViewBinder(response, modelConverters);
        return binder.bind(new DecodingHints(modelType));
    }

    private MODEL extractModel() {
        if (modelType == null) {
            // By default, if no bind was supplied, use the String representation of the response as the model
            return (MODEL) bindings.getViewForType(String.class).orElse(null);
        }
        return bindings.getViewForType(modelType).orElseThrow(() ->
            new AssertionError(format("Could not parse response into model object of type %s", modelType.getName()))
        );
    }

    private boolean isModelInstanceOfRequiredType(Object decodedResponseModel) {
        return (modelType == null) || ((decodedResponseModel != null) && modelType.isAssignableFrom(decodedResponseModel.getClass()));
    }

    private void transitionToState(State from, State to) {
        synchronized (this) {
            if (currentState == to) {
                throw new IllegalStateException(format("%s has been called twice in a row", to.getMethodName()));
            }
            if (currentState.ordinal() > from.ordinal()) {
                throw new IllegalStateException(format("%s must be called before %s", to.getMethodName(), currentState.getMethodName()));
            }
            currentState = to;
        }
    }

    private enum State {
        INITIALISED("request()"),
        BOUND("bind()"),
        ASSERTIONS("withAssertions()"),
        EXECUTED("call()");

        private final String methodName;

        State(String methodName) {
            Objects.requireNonNull(methodName);
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }
    }
}
