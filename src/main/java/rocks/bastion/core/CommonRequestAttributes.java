package rocks.bastion.core;

import org.apache.http.entity.ContentType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * Contains the common attributes which usually appear in all HTTP requests. This class is intended to be used as an object
 * inside implementations of {@link HttpRequest}. Using inheritance by composition, we avoid repeating the handling for
 * HTTP headers, query parameters and route parameters.
 * </p>
 * <p>
 * While not being an {@link HttpRequest}, this class provides methods which are similar to those found in that interface
 * so that classes which use the {@linkplain CommonRequestAttributes} can delegate the {@link HttpRequest} implementing
 * methods to this object.
 * </p>
 *
 * @author <a href="mailto:mail@kylepullicino.com">Kyle</a>
 */
public class CommonRequestAttributes {

    /**
     * Represents an empty HTTP content body.
     */
    public static final String EMPTY_BODY = "";

    private String name;
    private String url;
    private HttpMethod method;
    private ContentType contentType;
    private Collection<ApiHeader> headers;
    private Collection<ApiQueryParam> queryParams;
    private Collection<RouteParam> routeParams;
    private Object body;
    private long timeout;

    /**
     * Constructs a new instance of this object containing the following initial defaults:
     * <ul>
     * <li>HTTP method: Supplied as an argument this this constructor.</li>
     * <li>URL: Supplied as an argument this this constructor.</li>
     * <li>Name: Initialised to be the HTTP method concatenated with the URL.</li>
     * <li>Content-type: Initialised to the value of {@link ContentType#TEXT_PLAIN}.</li>
     * <li>Headers: Initialised to the empty collection of headers.</li>
     * <li>Query parameters: Initialised to the empty collection of query parameters.</li>
     * <li>Route parameters: Initialised to the empty collection of route parameters.</li>
     * <li>Timeout: Falls back to globally configured timeout</li>
     * </ul>
     *
     * @param method The HTTP method to use for a request. Cannot be {@literal null}.
     * @param url    The URL to use for a request. Cannot be {@literal null}.
     * @param body   The body to use for a request. Cannot be {@literal null}.
     */
    public CommonRequestAttributes(HttpMethod method, String url, Object body) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(url);

        this.method = method;
        this.url = url;
        name = method.getValue() + ' ' + url;
        contentType = ContentType.TEXT_PLAIN;
        headers = new LinkedList<>();
        queryParams = new LinkedList<>();
        routeParams = new LinkedList<>();
        timeout = HttpRequest.USE_GLOBAL_TIMEOUT;
        setBody(body);
    }

    /**
     * Change the HTTP method used by a request.
     *
     * @param method A non-{@literal null} HTTP method.
     */
    public void setMethod(HttpMethod method) {
        Objects.requireNonNull(method);
        this.method = method;
    }

    /**
     * Change the descriptive name used by a request.
     *
     * @param name A non-{@literal null} descriptive name.
     */
    public void setName(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    /**
     * Sets the URL to use for a request.
     *
     * @param url A non-{@literal null} URL to send with a request
     */
    public void setUrl(String url) {
        Objects.requireNonNull(url);
        this.url = url;
    }

    /**
     * Set the content-type that will be used for a request.
     *
     * @param contentType A content-type to use for a request. Use {@literal null} to send no content-type header.
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * Add a new HTTP header that will be sent with a request.
     *
     * @param name  A non-{@literal null} name for the new header
     * @param value A non-{@literal null} value for the new header
     */
    public void addHeader(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        headers.add(new ApiHeader(name, value));
    }

    /**
     * Add a new HTTP query parameter that will be sent with the request.
     *
     * @param name  A non-{@literal null} name for the new query parameter
     * @param value A non-{@literal null} value for the new query parameter
     */
    public void addQueryParam(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        queryParams.add(new ApiQueryParam(name, value));
    }

    /**
     * Add a new HTTP route parameter that will be sent with the request.
     *
     * @param name  A non-{@literal null} name for the new query parameter
     * @param value A non-{@literal null} value for the new query parameter
     */
    public void addRouteParam(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        routeParams.add(new RouteParam(name, value));
    }

    /**
     * Sets the body content that will be sent by a request. You cannot set the body to {@literal null}; instead,
     * if you don't want to send any body content, pass in the {@link #EMPTY_BODY} constant.
     *
     * @param body A non-{@literal null} string to send as the body content
     */
    public void setBody(Object body) {
        Objects.requireNonNull(body);
        this.body = body;
    }

    /**
     * See {@link HttpRequest#name()} for information about this method.
     *
     * @return The descriptive name to use for a request
     */
    public String name() {
        return name;
    }

    /**
     * See {@link HttpRequest#url()} for information about this method.
     *
     * @return The URL to use for a request
     */
    public String url() {
        return url;
    }

    /**
     * See {@link HttpRequest#method()} for information about this method.
     *
     * @return The HTTP method to use for a request
     */
    public HttpMethod method() {
        return method;
    }

    /**
     * See {@link HttpRequest#contentType()} for information about this method.
     *
     * @return The content-type header, inside an {@link Optional#ofNullable(Object) Optional container} to use for a request.
     * An {@link Optional#empty() empty optional} is returned if no content type was set.
     */
    public Optional<ContentType> contentType() {
        return Optional.ofNullable(contentType);
    }

    /**
     * See {@link HttpRequest#headers()} for information about this method.
     *
     * @return The collection of headers to use for a request
     */
    public Collection<ApiHeader> headers() {
        return headers;
    }

    /**
     * See {@link HttpRequest#queryParams()} for information about this method.
     *
     * @return The collection of query parameters to use for a request
     */
    public Collection<ApiQueryParam> queryParams() {
        return queryParams;
    }

    /**
     * See {@link HttpRequest#routeParams()} for information about this method.
     *
     * @return The collection of route parameters to use for a request
     */
    public Collection<RouteParam> routeParams() {
        return routeParams;
    }

    /**
     * See {@link HttpRequest#body()} for information about this method.
     *
     * @return The body content to send with a request
     */
    public Object body() {
        return body;
    }

    /**
     * See {@link HttpRequest#timeout()}.
     *
     * @return the timeout for all phases of a request
     */
    public long timeout() {
        return timeout;
    }


    /**
     * Set the timeout for all phases of a request.
     * See {@link HttpRequest#timeout()} for more details.
     *
     * @param timeout the timeout (in milliseconds).
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
