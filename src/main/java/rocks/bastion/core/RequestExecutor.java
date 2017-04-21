package rocks.bastion.core;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import rocks.bastion.core.configuration.Configuration;

/**
 * Responsible for executing a Bastion remote request built using the {@link BastionBuilderImpl} builder and prepare a response object.
 */
public class RequestExecutor {

    private Configuration configuration;
    private HttpRequest bastionHttpRequest;
    private com.mashape.unirest.request.HttpRequest executableHttpRequest;
    private Collection<ApiHeader> headers;
    private String resolvedUrl;

    public RequestExecutor(HttpRequest bastionHttpRequest, Configuration configuration) {
        Objects.requireNonNull(bastionHttpRequest);
        this.bastionHttpRequest = bastionHttpRequest;
        this.configuration = configuration;
        executableHttpRequest = prepareHttpRequest();
        applyHeaders();
        applyQueryParameters();
        applyRouteParameters();
        applyBody();
    }

    public String getMethod() {
        return bastionHttpRequest.method().getValue();
    }

    public String getResolvedUrl() {
        return resolvedUrl;
    }

    public Collection<ApiHeader> getHeaders() {
        return Collections.unmodifiableCollection(headers);
    }

    /**
     * Executes the given HTTP request and retrieves the response.
     *
     * @return The HTTP response retrieved from the remote server.
     */
    public Response execute() {
        try {
            HttpResponse<InputStream> httpResponse = performRequest();
            return convertToRawResponse(httpResponse);
        } catch (UnirestException exception) {
            if (exception.getCause() instanceof SocketTimeoutException) {
                throw new AssertionError(String.format("Failed to receive response before timeout of [%s] ms", resolveTimeoutOrFallbackToGlobal(bastionHttpRequest, configuration)));
            }
            throw new IllegalStateException("Failed executing request", exception);
        }
    }

    private com.mashape.unirest.request.HttpRequest prepareHttpRequest() {

        long timeout = resolveTimeoutOrFallbackToGlobal(bastionHttpRequest, configuration);
        Unirest.setTimeouts(timeout, timeout);
        com.mashape.unirest.request.HttpRequest request;
        switch (bastionHttpRequest.method().getValue()) {
            case "GET":
                request = Unirest.get(bastionHttpRequest.url());
                break;
            case "POST":
                request = Unirest.post(bastionHttpRequest.url());
                break;
            case "PATCH":
                request = Unirest.patch(bastionHttpRequest.url());
                break;
            case "DELETE":
                request = Unirest.delete(bastionHttpRequest.url());
                break;
            case "PUT":
                request = Unirest.put(bastionHttpRequest.url());
                break;
            case "OPTIONS":
                request = Unirest.options(bastionHttpRequest.url());
                break;
            case "HEAD":
                request = Unirest.head(bastionHttpRequest.url());
                break;
            default:
                throw new UnsupportedOperationException(String.format("We cannot perform a request of type %s.", bastionHttpRequest.method().getValue()));
        }
        return request;
    }

    private static long resolveTimeoutOrFallbackToGlobal(HttpRequest request, Configuration configuration) {
        if (request.timeout() == HttpRequest.USE_GLOBAL_TIMEOUT) {
            return configuration.getGlobalRequestAttributes().getGlobalRequestTimeout();
        } else {
            return request.timeout();
        }
    }

    private void applyHeaders() {
        headers = new LinkedList<>(configuration.getGlobalRequestAttributes().getGlobalHeaders());
        headers.addAll(bastionHttpRequest.headers());
        if (headers.stream().noneMatch(header -> header.getName().equalsIgnoreCase("content-type")) && bastionHttpRequest.contentType().isPresent()) {
            headers.add(new ApiHeader("Content-Type", bastionHttpRequest.contentType().get().toString()));
        }
        headers.forEach(header -> executableHttpRequest.header(header.getName(), header.getValue()));
    }

    private void applyQueryParameters() {
        List<ApiQueryParam> apiQueryParams = new ArrayList<>(configuration.getGlobalRequestAttributes().getGlobalQueryParams());
        apiQueryParams.addAll(bastionHttpRequest.queryParams());
        apiQueryParams.forEach(queryParam -> executableHttpRequest.queryString(queryParam.getName(), queryParam.getValue()));
        resolvedUrl = executableHttpRequest.getUrl();
    }

    private void applyRouteParameters() {
        List<RouteParam> routeParams = new ArrayList<>(configuration.getGlobalRequestAttributes().getGlobalRouteParams());
        routeParams.addAll(bastionHttpRequest.routeParams());
        routeParams.forEach(routeParam -> executableHttpRequest.routeParam(routeParam.getName(), routeParam.getValue()));
        resolvedUrl = executableHttpRequest.getUrl();
    }

    private void applyBody() {
        if (executableHttpRequest instanceof HttpRequestWithBody) {
            ((HttpRequestWithBody) executableHttpRequest).body(bastionHttpRequest.body().toString());
        }
    }

    private HttpResponse<InputStream> performRequest() throws UnirestException {
        return executableHttpRequest.asBinary();
    }

    private Response convertToRawResponse(HttpResponse<InputStream> httpResponse) {
        return new RawResponse(httpResponse.getStatus(),
                httpResponse.getStatusText(),
                httpResponse.getHeaders().entrySet().stream().flatMap(header ->
                        header.getValue().stream().map(headerValue ->
                                new ApiHeader(header.getKey(), headerValue))).collect(Collectors.toList()),
                httpResponse.getBody());
    }
}
