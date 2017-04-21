package rocks.bastion.core;

import org.apache.http.entity.ContentType;

import java.util.Collection;
import java.util.Optional;

/**
 * Defines an HTTP request for Bastion to send as part of a test.
 */
public interface HttpRequest {

    /**
     * Constant to be returned by {@link #timeout()} if the globally configured timeout should be used when performing this request.
     */
    long USE_GLOBAL_TIMEOUT = -1;

    /**
     * Returns a descriptive name for the contents of this request object.
     *
     * @return Description of this request
     */
    String name();

    /**
     * Returns the URL which this HTTP request will be sent to.
     *
     * @return A non-null remote URL for this request
     */
    String url();

    /**
     * Returns the HTTP method to use for sending this request. Can be one of the constants from {@link HttpMethod}
     * or a custom HTTP method which is non-standard.
     *
     * @return A non-null HTTP method for this request
     */
    HttpMethod method();

    /**
     * The content type header to use for this request. This includes the MIME type of the content body, an optional character
     * set of the request and other parameters, if necessary (such as quality parameter).
     *
     * @return An {@link Optional#of(Object) optional content type} to use for this request. Return an {@link Optional#empty()
     * empty Optional} if you would like to leave the content-type header undefined for this request.
     */
    Optional<ContentType> contentType();

    /**
     * A collection of HTTP headers that will be included with this request. Note that Bastion will automatically include the
     * "Content-type" header using the value returned by {@link #contentType()} if, and only if, the collection returned by this
     * method does not contain a header for "Content-type." This means that this method should not return a "Content-type" header
     * as part of the collection unless you want to override the content-type returned by the {@link #contentType()} method.
     *
     * @return A non-null collection of HTTP header to include with the request
     */
    Collection<ApiHeader> headers();

    /**
     * A collection of HTTP query parameters that will be used for performing this request. Query parameters are typically
     * appended to the end of an URL following a question mark symbol.
     *
     * @return A non-null collection of query parameters for this request
     */
    Collection<ApiQueryParam> queryParams();

    /**
     * <p>
     * A collection of route parameter assignments that will be replaced in the URL. For example, the following URL:
     * </p>
     * <p>
     * {@code http://sushi.test/{id}/ingredients}
     * </p>
     * <p>
     * The URL above contains one route parameter called "id" which can be assigned a numerical value which will be replaced
     * when the actual HTTP takes place.
     * </p>
     *
     * @return A non-null collection of route parameters for this request.
     */
    Collection<RouteParam> routeParams();

    /**
     * An object that will serve as a content body for this request. Can be {@literal null} if this request will not send
     * a body. Bastion may use the content-type of this request, returned by the {@link #contentType()} method, as well as
     * the runtime type of the object returned by this method to determine how best to serialize the returned object into
     * the HTTP request's body.
     *
     * @return An object that will serve as the content body for this request. May be {@literal null}.
     */
    Object body();

    /**
     * A timeout (in milliseconds) that will cause tests to cutoff if:
     * <ul>
     *  <li>the connection takes too long to be established</li>
     *  <li>the response takes too long to arrive.</li>
     * </ul>
     * Note that these are 2 separate timeouts; the test might take (in the worst case) <b>twice</b> the value of the timeout, if the phases mentioned above take long enough.
     * Tests exceeding these timeouts will throw an {@link AssertionError} and be marked as failed.
     * A value of {@literal 0} indicates no timeout - the test will wait indefinitely for a response.
     *
     * By default, this returns the {@link HttpRequest#USE_GLOBAL_TIMEOUT} constant, which indicates that the globally configured timeout should be used.
     *
     * @return a number (in milliseconds) representing the longest a test should wait for each phase of a request
     */
    default long timeout() {
        return USE_GLOBAL_TIMEOUT;
    }

}
