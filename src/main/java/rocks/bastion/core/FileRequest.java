package rocks.bastion.core;

import org.apache.http.entity.ContentType;
import rocks.bastion.core.resource.ResourceLoader;
import rocks.bastion.core.resource.ResourceNotFoundException;
import rocks.bastion.core.resource.UnreadableResourceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * An HTTP request which takes any arbitrary file/resource, using the data within as its content body. The {@linkplain FileRequest} will not perform
 * any conversions or validation on any user-supplied body content. Use the static factory methods, such as {@link #post(String, String)}
 * or {@link #delete(String, String)} to initialise a new {@linkplain FileRequest}.
 * <p>
 * By default, this request will contain no headers (except for the content-type) and no query parameters. Use the {@link #addHeader(String, String)}
 * and {@link #addQueryParam(String, String)}} to add them. Also, initially, Bastion will attempt to guess the MIME type to send as part of the
 * "Content-type" header by looking at the given file. If no MIME type could be chosen, the request will have the "text/plain" content-type MIME
 * (which is automatically added to the HTTP headers by Bastion): you can change this content-type by calling the {@link #setContentType(ContentType)}
 * method.
 */
public class FileRequest implements HttpRequest {

    private static final Logger LOG = Logger.getLogger(FileRequest.class.getCanonicalName());

    /**
     * Construct an HTTP request, using the POST method, to be sent on the specified URL. The request's content will be loaded
     * from the specified resource URL. Bastion will attempt to guess the MIME type to send by looking at the given file.
     * <p>
     * The resource source is specified as a resource URL as described in {@link ResourceLoader}. Valid resource URLs include (but
     * are not limited to):
     * </p>
     * <ul>
     * <li>{@code classpath:/rocks/bastion/json/Sushi.json}</li>
     * <li>{@code file:/home/user/Sushi.json}</li>
     * </ul>
     * <p>
     * For more information about which resource URLs are accepted see the documentation for {@link ResourceLoader}.
     * </p>
     *
     * @param url      A non-{@literal null} URL to send the request on
     * @param resource A non-{@literal null} resource URL to load the data from, for this request
     * @return An HTTP request using the POST method
     * @throws UnreadableResourceException Thrown if the specified resource exists but cannot be read (because it is a directory, for example)
     * @throws ResourceNotFoundException   Thrown if the specified resource does not exist
     */
    public static FileRequest post(String url, String resource) throws UnreadableResourceException, ResourceNotFoundException {
        return new FileRequest(HttpMethod.POST, url, resource);
    }

    /**
     * Construct an HTTP request, using the DELETE method, to be sent on the specified URL. The request's content will be loaded
     * from the specified resource URL. Bastion will attempt to guess the MIME type to send by looking at the given file.
     * <p>
     * The resource source is specified as a resource URL as described in {@link ResourceLoader}. Valid resource URLs include (but
     * are not limited to):
     * </p>
     * <ul>
     * <li>{@code classpath:/rocks/bastion/json/Sushi.json}</li>
     * <li>{@code file:/home/user/Sushi.json}</li>
     * </ul>
     * <p>
     * For more information about which resource URLs are accepted see the documentation for {@link ResourceLoader}.
     * </p>
     *
     * @param url      A non-{@literal null} URL to send the request on
     * @param resource A non-{@literal null} resource URL to load the data from, for this request
     * @return An HTTP request using the DELETE method
     * @throws UnreadableResourceException Thrown if the specified resource exists but cannot be read (because it is a directory, for example)
     * @throws ResourceNotFoundException   Thrown if the specified resource does not exist
     */
    public static FileRequest delete(String url, String resource) throws UnreadableResourceException, ResourceNotFoundException {
        return new FileRequest(HttpMethod.DELETE, url, resource);
    }

    /**
     * Construct an HTTP request, using the PUT method, to be sent on the specified URL. The request's content will be loaded
     * from the specified resource URL. Bastion will attempt to guess the MIME type to send by looking at the given file.
     * <p>
     * The resource source is specified as a resource URL as described in {@link ResourceLoader}. Valid resource URLs include (but
     * are not limited to):
     * </p>
     * <ul>
     * <li>{@code classpath:/rocks/bastion/json/Sushi.json}</li>
     * <li>{@code file:/home/user/Sushi.json}</li>
     * </ul>
     * <p>
     * For more information about which resource URLs are accepted see the documentation for {@link ResourceLoader}.
     * </p>
     *
     * @param url      A non-{@literal null} URL to send the request on
     * @param resource A non-{@literal null} resource URL to load the data from, for this request
     * @return An HTTP request using the PUT method
     * @throws UnreadableResourceException Thrown if the specified resource exists but cannot be read (because it is a directory, for example)
     * @throws ResourceNotFoundException   Thrown if the specified resource does not exist
     */
    public static FileRequest put(String url, String resource) throws UnreadableResourceException, ResourceNotFoundException {
        return new FileRequest(HttpMethod.PUT, url, resource);
    }

    /**
     * Construct an HTTP request, using the PATCH method, to be sent on the specified URL. The request's content will be loaded
     * from the specified resource URL. Bastion will attempt to guess the MIME type to send by looking at the given file.
     * <p>
     * The resource source is specified as a resource URL as described in {@link ResourceLoader}. Valid resource URLs include (but
     * are not limited to):
     * </p>
     * <ul>
     * <li>{@code classpath:/rocks/bastion/json/Sushi.json}</li>
     * <li>{@code file:/home/user/Sushi.json}</li>
     * </ul>
     * <p>
     * For more information about which resource URLs are accepted see the documentation for {@link ResourceLoader}.
     * </p>
     *
     * @param url      A non-{@literal null} URL to send the request on
     * @param resource A non-{@literal null} resource URL to load the data from, for this request
     * @return An HTTP request using the PATCH method
     * @throws UnreadableResourceException Thrown if the specified resource exists but cannot be read (because it is a directory, for example)
     * @throws ResourceNotFoundException   Thrown if the specified resource does not exist
     */
    public static FileRequest patch(String url, String resource) throws UnreadableResourceException, ResourceNotFoundException {
        return new FileRequest(HttpMethod.PATCH, url, resource);
    }

    /**
     * Construct an HTTP request, using the specified method, to be sent on the specified URL. The request's content will be loaded
     * from the specified resource URL. Bastion will attempt to guess the MIME type to send by looking at the given file.
     * <p>
     * The resource source is specified as a resource URL as described in {@link ResourceLoader}. Valid resource URLs include (but
     * are not limited to):
     * </p>
     * <ul>
     * <li>{@code classpath:/rocks/bastion/json/Sushi.json}</li>
     * <li>{@code file:/home/user/Sushi.json}</li>
     * </ul>
     * <p>
     * For more information about which resource URLs are accepted see the documentation for {@link ResourceLoader}.
     * </p>
     *
     * @param method   A non-{@literal null} HTTP method to use for this request
     * @param url      A non-{@literal null} URL to send the request on
     * @param resource A non-{@literal null} resource URL to load the data from, for this request
     * @return An HTTP request
     * @throws UnreadableResourceException Thrown if the specified resource exists but cannot be read (because it is a directory, for example)
     * @throws ResourceNotFoundException   Thrown if the specified resource does not exist
     */
    public static FileRequest withMethod(HttpMethod method, String url, String resource) throws UnreadableResourceException, ResourceNotFoundException {
        return new FileRequest(method, url, resource);
    }

    private final GeneralRequest generalRequest;

    protected FileRequest(HttpMethod method, String url, String resource) throws UnreadableResourceException, ResourceNotFoundException {
        generalRequest = GeneralRequest.withMethod(method, url, new ResourceLoader(resource).load());
        guessResourceMimeType(resource);
    }

    /**
     * Set the content-type that will be used for this request.
     *
     * @param contentType A non-{@literal null} content-type to use for this request
     * @return This request (for method chaining)
     */
    public FileRequest setContentType(ContentType contentType) {
        generalRequest.setContentType(contentType);
        return this;
    }

    /**
     * Add a new HTTP header that will be sent with this request.
     *
     * @param name  A non-{@literal null} name for the new header
     * @param value A non-{@literal null} value for the new header
     * @return This request (for method chaining)
     */
    public FileRequest addHeader(String name, String value) {
        generalRequest.addHeader(name, value);
        return this;
    }

    /**
     * Add a new HTTP query parameter that will be sent with this request.
     *
     * @param name  A non-{@literal null} name for the new query parameter
     * @param value A non-{@literal null} value for the new query parameter
     * @return This request (for method chaining)
     */
    public FileRequest addQueryParam(String name, String value) {
        generalRequest.addQueryParam(name, value);
        return this;
    }

    /**
     * Add a new HTTP route parameter that will be sent with this request. Put a placeholder for the route parameter in
     * the request URL by surrounding a parameter's name using braces (eg. {@code http://sushi.test/{id}/ingredients}).
     * The URL in the previous example contains one route param which can be replaced with a numerical value using
     * {@code addRouteParam("id", "53")}, for example.
     *
     * @param name  A non-{@literal null} name for the new route parameter
     * @param value A non-{@literal null} value for the new route parameter
     * @return This request (for method chaining)
     */
    public FileRequest addRouteParam(String name, String value) {
        generalRequest.addRouteParam(name, value);
        return this;
    }

    /**
     * Sets the timeout, in milliseconds, that will cause the test to fail if no response response is received within the specified timeout.
     *
     * A value of {@literal 0} indicates no timeout; the test will wait indefinitely for a response.
     *
     * @return a number (in milliseconds) representing this requests's timeout
     */
    public FileRequest setTimeout(long timeout) {
        generalRequest.setTimeout(timeout);
        return this;
    }

    @Override
    public String name() {
        return generalRequest.name();
    }

    @Override
    public String url() {
        return generalRequest.url();
    }

    @Override
    public HttpMethod method() {
        return generalRequest.method();
    }

    @Override
    public Optional<ContentType> contentType() {
        return generalRequest.contentType();
    }

    @Override
    public Collection<ApiHeader> headers() {
        return generalRequest.headers();
    }

    @Override
    public Collection<ApiQueryParam> queryParams() {
        return generalRequest.queryParams();
    }

    @Override
    public Collection<RouteParam> routeParams() {
        return generalRequest.routeParams();
    }

    @Override
    public Object body() {
        return generalRequest.body();
    }

    @Override
    public long timeout() {
        return generalRequest.timeout();
    }

    private void guessResourceMimeType(String resource) {
        try {
            String mimeType = Files.probeContentType(Paths.get(resource));
            if (mimeType != null) {
                generalRequest.setContentType(ContentType.create(mimeType));
            } else {
                LOG.warning(format("Could not determine %s MIME type. Creating request with text/plain MIME type. Use setContentType() to change MIME type.", resource));
            }
        } catch (IOException e) {
            LOG.warning(format("Could not determine %s MIME type. Creating request with text/plain MIME type. Use setContentType() to change MIME type.", resource));
        }
    }
}
