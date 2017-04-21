package rocks.bastion;

import rocks.bastion.core.Assertions;
import rocks.bastion.core.BastionFactory;
import rocks.bastion.core.HttpRequest;
import rocks.bastion.core.builder.BastionBuilder;
import rocks.bastion.core.builder.ExecuteRequestBuilder;
import rocks.bastion.core.builder.PostExecutionBuilder;
import rocks.bastion.core.configuration.Configuration;
import rocks.bastion.core.configuration.GlobalRequestAttributes;
import rocks.bastion.core.resource.ResourceLoader;

import static java.util.Objects.requireNonNull;

/**
 * The main starting point for creating a Bastion test using the library.
 * <h1>Overview</h1>
 * <p>
 * Bastion is a library that eases the development of end-to-end tests for HTTP APIs. A typical user would write tests using
 * Bastion based off of some sort of API specification. This API specification can be anything like a WADL file, RAML file or a JSON schema.
 * A test engineer would prepare requests and responses based on these specifications to test the overall process of calling these APIs.
 * </p>
 * <p>
 * Bastion also contains tools that will help developers fix problems that occur as fast as possible. When a test fails, the entire
 * request and response content is logged. Also, when using the built-in assertions, helpful diff text is displayed related to the type
 * of HTTP response you're expecting.
 * </p>
 * <h1>Creating a Bastion Test</h1>
 * <p>
 * Bastion is run as part of your standard testing framework. For an
 * example of a Bastion test, see the <a href="http://bastion-dev.github.io/Bastion/reference/index.html">Bastion User Guide</a>.
 * </p>
 * <p>
 * Inside your test method or test case, make a call to the {@link Bastion#request(String, HttpRequest)} method. This will
 * return a <a href="https://en.wikipedia.org/wiki/Fluent_interface">fluent-builder style interface</a> that will allow you
 * to further specify the response model and assertions on your test.
 * </p>
 * <p>
 * The returned builder will allow you call any of the following methods:
 * </p>
 * <ul>
 * <li>{@link rocks.bastion.core.builder.BindBuilder#bind(Class)}: Specify which class type to use when constructing a model of the received response. Bastion will
 * interpret and decode the received response object into an instance of whatever type is supplied to this method.</li>
 * <li>{@link rocks.bastion.core.builder.AssertionsBuilder#withAssertions(Assertions)}: Specify what test assertions to apply to the response. We recommend supplying the
 * assertions as a lambda or using one of the available subclass implementations of {@link Assertions}.</li>
 * <li>{@link ExecuteRequestBuilder#call()}: Starts the Bastion test by executing the HTTP request.</li>
 * </ul>
 * <p>
 * You cannot call any of the methods above before any of the methods listed before it. Therefore, in your test, you should call
 * the methods above one after each other as listed above: you can skip any of the methods and Bastion will assign defaults.
 * For example, if you want to make an HTTP request and apply some assertions without binding a model, you would
 * call the {@link #request(String, HttpRequest)} method first, followed by the {@link rocks.bastion.core.builder.AssertionsBuilder#withAssertions(Assertions)}
 * method to configure your assertions, followed by the {@link ExecuteRequestBuilder#call()} method to send the request
 * and start the test.
 * </p>
 * <h1>Getting the Response</h1>
 * <p>
 * Once you execute the HTTP test using the {@link ExecuteRequestBuilder#call()} method, you can retrieve the HTTP response
 * and any views decoded by Bastion using the following methods:
 * </p>
 * <ul>
 * <li>{@link PostExecutionBuilder#getResponse()}: Get the HTTP response object that was returned following the request
 * sent with the Bastion test. This will contain the status code, and HTTP headers and the body content. It will also
 * contain the model object decoded by Bastion.</li>
 * <li>{@link PostExecutionBuilder#getModel()}: Get the model object decoded by Bastion from the HTTP response. You can specify the
 * type of model object you expect using {@link rocks.bastion.core.builder.BindBuilder#bind(Class)}.</li>
 * <li>{@link PostExecutionBuilder#getView(Class)}: Get alternative view objects (different than the model) of the response.</li>
 * </ul>
 * <p>
 * When Bastion receives a response, it will attempt to decode it into as many view objects as possible. A view is a Java object which
 * represents the data received inside the response body. For example, if the response is a JSON string, then one of the views would be a
 * {@link com.fasterxml.jackson.databind.JsonNode}.
 * </p>
 * <p>
 * The model object is a plain Java object that was created after Bastion has decoded the content body into views. If you specified
 * a model type with the {@link rocks.bastion.core.builder.BindBuilder#bind(Class)} method, then the model object will be
 * of the type you specified.
 * </p>
 * <p>Obtaining the response in these ways is useful especially if you want to perform another Bastion request after in the same
 * test. You can use the information retrieved in the model, views or response for the next Bastion request.</p>
 * <h1>Request Types</h1>
 * <p>
 * There are multiple types of requests that you can send using Bastion. To start building a Bastion test, construct and initialise
 * a request and pass it to the {@link #request(String, HttpRequest)} method. The types of {@link HttpRequest requests} you can
 * use are listed below (see each requests's JavaDocs for more information about how to use that request):
 * </p>
 * <ul>
 * <li>{@link rocks.bastion.core.GeneralRequest}: Allows you to specify a string content-body. Can also be used if you wouldn't
 * like to send any content-body (such as for a {@code GET} request).</li>
 * <li>{@link rocks.bastion.core.FileRequest}: Allows you to string content-body that is loaded from a file.</li>
 * <li>{@link rocks.bastion.core.FormUrlEncodedRequest}: Allows you to construct an HTTP request containing URL encoded form data in its
 * content-body.</li>
 * <li>{@link rocks.bastion.core.json.JsonRequest}: Allows you to specify a valid JSON string to send as part of your HTTP request
 * content-body.</li>
 * </ul>
 * <p>Also, feel free to provide your own implementation of an {@link HttpRequest} which will allow you customise completely
 * the request sent by Bastion. Implementing your own {@link HttpRequest request class} is extremely useful to encourage
 * reuse and maintainability (instead of repeating your own requests each time).</p>
 * <h1>Assertion Types</h1>
 * <p>
 * Assertions allow you to quickly check whether a test was successful or not. Note that a request does not necessarily have
 * to succeed for a test to pass. In fact, you are encouraged to test your error/failure paths extensively as well as your
 * success paths of your APIs to ensure good quality software. Bastion allows you to do this easily by supply an {@link Assertions}
 * object to the {@link rocks.bastion.core.builder.AssertionsBuilder#withAssertions(Assertions)}} method. At the moment, there
 * is only one built-in {@link Assertions assertion type} listed below (see the type's JavaDocs for more information on
 * how to use it):
 * </p>
 * <ul>
 * <li>{@link rocks.bastion.core.StatusCodeAssertions}: Expects a response from the remote server to have any of the specified status codes.</li>
 * <li>{@link rocks.bastion.core.json.JsonResponseAssertions}: Expects a JSON response from the remote server. It allows
 * you to specify a valid JSON string to assert the response against. The assertion is smart in that a JSON structural comparison
 * is performed (instead of a straight-up text equality comparison) making sure that the response contains all the expected
 * properties, with the correct values, regardless of whitespace and property order.</li>
 * <li>{@link rocks.bastion.core.json.JsonSchemaAssertions}: Expects a JSON response from the remote server to adhere to the given JSON schema.</li>
 * </ul>
 * <p>
 * Just like requests, you may define your own {@link Assertions assertion types}. You are encouraged to do so if you
 * are performing some sort of assertion frequently throughout your tests. This enables code reuse and improves the maintainability
 * of your tests.
 * </p>
 * <p>
 * If you do not want to implement your own assertion classes you can supply an assertions object directly into the
 * {@link rocks.bastion.core.builder.AssertionsBuilder#withAssertions(Assertions)} method of the Bastion test builder as
 * a Java 8 lambda.
 * </p>
 * <h1>Global configuration</h1>
 * <p>
 * You can specify global attributes which apply to all future Bastion requests using the {@link Bastion#globals()} method. This allows
 * you to add global {@link GlobalRequestAttributes#addQueryParam(String, String) query parameters},
 * {@link GlobalRequestAttributes#addHeader(String, String) headers} and
 * {@link GlobalRequestAttributes#addRouteParam(String, String) route parameters} to all requests you make using Bastion. You can also
 * {@link GlobalRequestAttributes#setGlobalRequestTimeout(long) configure the timeout} which applies to requests.
 * </p>
 * <h1>Groovy Tests</h1>
 * <p>
 * Certain features of Bastion such as the {@link rocks.bastion.core.json.JsonRequest} and the {@link rocks.bastion.core.json.JsonResponseAssertions}
 * classes work with strings. These string inputs will frequently turn out to be extremely long and, especially in Java,
 * this will cause your strings to appear unwieldy due to multiple breaklines and escape characters. Fortunately, Bastion
 * works with any of the JVM languages including Groovy.
 * </p>
 * <p>
 * Groovy is easy to set up for your project. After you have set up Groovy to run automated tests, simply add Bastion to
 * your project's dependencies (as explained above) and create your tests as normal. With Groovy, you can supply long strings
 * using alternate string delimiters including so-called multiline strings. These will improve the readability of your code
 * greatly when supplying strings to Bastion. See the <a href="http://bastion-dev.github.io/Bastion/reference/index.html">Bastion User Guide</a>
 * for an example of how a Bastion test would look like in Groovy.
 * </p>
 */
public final class Bastion {

    /**
     * <p>
     * Starts building a single Bastion test which will execute the specified HTTP request. The first parameter is a descriptive
     * string that appears in test reports and any UI running your tests. The method will return a fluent-builder object which
     * will let you specify the test further.
     * </p>
     * <p>
     * The request you specify in this method can be an instance of one of the in-built {@link HttpRequest request types}
     * provided with Bastion itself. You can also supply your own implementation of a request by subclassing {@link HttpRequest}.
     * </p>
     *
     * @param message A descriptive message for this Bastion test.
     * @param request The HTTP request that Bastion will execute for this test.
     * @return A fluent-builder object which will let you bind a model type, add assertions and execute the test.
     */
    public static BastionBuilder<Object> request(String message, HttpRequest request) {
        return BastionFactory.getDefaultBastionFactory().getBastion(message, request);
    }

    /**
     * <p>
     * Starts building a single Bastion test which will execute the specified HTTP request. The method will return a
     * fluent-builder object which will let you specify the test further.
     * </p>
     * <p>
     * The request you specify in this method can be an instance of one of the in-built {@link HttpRequest request types}
     * provided with Bastion itself. You can also supply your own implementation of a request by subclassing {@link HttpRequest}.
     * </p>
     *
     * @param request The HTTP request that Bastion will execute for this test.
     * @return A fluent-builder object which will let you bind a model type, add assertions and execute the test.
     */
    public static BastionBuilder<Object> request(HttpRequest request) {
        return BastionFactory.getDefaultBastionFactory().getBastion("", request);
    }

    /**
     * <p>
     * Loads Bastion's configuration from the provided resource location. The resource location should be a valid .yml file that
     * corresponds to the same schema as a {@link Configuration}.
     * </p>
     *
     * @see Configuration
     * @see ResourceLoader
     * @param resourceLocation The resource location for the Bastion configuration.
     * @return The loaded configuration.
     */
    public static Configuration loadConfiguration(String resourceLocation) {
        requireNonNull(resourceLocation, "The resource location cannot be null.");
        return BastionFactory.loadConfiguration(resourceLocation);
    }

    /**
     * <p>
     * Starts building or modifying the configuration of the {@link GlobalRequestAttributes} for Bastion.
     * </p>
     *
     * @return The configured global request attributes.
     */
    public static GlobalRequestAttributes globals() {
        return BastionFactory.getDefaultBastionFactory().getConfiguration().getGlobalRequestAttributes();
    }

    private Bastion() {
        // This class should not be instantiated.
    }
}
