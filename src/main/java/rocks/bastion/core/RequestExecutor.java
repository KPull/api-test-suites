package rocks.bastion.core;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import rocks.bastion.core.configuration.Configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Responsible for executing a Bastion remote request built using the {@link BastionBuilderImpl} builder and prepare a response object.
 */
public class RequestExecutor {

    private Configuration configuration;
    private HttpRequest bastionHttpRequest;
    private HttpUriRequest executableHttpRequest;
    private Collection<ApiHeader> headers;
    private String resolvedUrl;
    private URIBuilder uriBuilder;

    public RequestExecutor(HttpRequest bastionHttpRequest, Configuration configuration) {
        try {
            Objects.requireNonNull(bastionHttpRequest);
            this.bastionHttpRequest = bastionHttpRequest;
            this.configuration = configuration;
            uriBuilder = new URIBuilder();
            applyQueryParameters();
            applyRouteParameters();
            executableHttpRequest = prepareHttpRequest();
            applyHeaders();
            applyBody();
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Could not compute URI", exception);
        }
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
            CloseableHttpResponse httpResponse = performRequest();
            return convertToRawResponse(httpResponse);
        } catch (IOException exception) {
            if (exception.getCause() instanceof SocketTimeoutException) {
                throw new AssertionError(String.format("Failed to receive response before timeout of [%s] ms",
                                                       resolveTimeoutOrFallbackToGlobal(bastionHttpRequest, configuration)));
            }
            throw new IllegalStateException("Failed executing request", exception);
        }
    }

    private HttpUriRequest prepareHttpRequest() throws URISyntaxException {

        long timeout = resolveTimeoutOrFallbackToGlobal(bastionHttpRequest, configuration);
        HttpRequestBase request;
        resolvedUrl = uriBuilder.build().toString();
        switch (bastionHttpRequest.method().getValue()) {
            case "GET":
                request = new HttpGet(resolvedUrl);
                break;
            case "POST":
                request = new HttpPost(resolvedUrl);
                break;
            case "PATCH":
                request = new HttpPost(resolvedUrl);
                break;
            case "DELETE":
                request = new HttpDelete(resolvedUrl);
                break;
            case "PUT":
                request = new HttpPut(resolvedUrl);
                break;
            case "OPTIONS":
                request = new HttpOptions(resolvedUrl);
                break;
            case "HEAD":
                request = new HttpHead(resolvedUrl);
                break;
            default:
                throw new UnsupportedOperationException(String.format("We cannot perform a request of type %s.",
                                                                      bastionHttpRequest.method().getValue()));
        }
        request.setConfig(RequestConfig.custom()
                                       .setConnectTimeout((int) timeout)
                                       .setSocketTimeout((int) timeout)
                                       .setConnectionRequestTimeout((int) timeout)
                                       .build());
        return request;
    }

    private void applyHeaders() {
        headers = new LinkedList<>(configuration.getGlobalRequestAttributes().getGlobalHeaders());
        headers.addAll(bastionHttpRequest.headers());
        if (headers.stream().noneMatch(header -> header.getName().equalsIgnoreCase("content-type")) && bastionHttpRequest.contentType()
                                                                                                                         .isPresent()) {
            headers.add(new ApiHeader("Content-Type", bastionHttpRequest.contentType().get().toString()));
        }
        headers.forEach(header -> executableHttpRequest.addHeader(header.getName(), header.getValue()));
    }

    private void applyQueryParameters() {
        List<ApiQueryParam> apiQueryParams = new ArrayList<>(configuration.getGlobalRequestAttributes().getGlobalQueryParams());
        apiQueryParams.addAll(bastionHttpRequest.queryParams());
        apiQueryParams.forEach(queryParam -> uriBuilder.addParameter(queryParam.getName(), queryParam.getValue()));
    }

    private void applyRouteParameters() {
        List<RouteParam> routeParams = new ArrayList<>(configuration.getGlobalRequestAttributes().getGlobalRouteParams());
        routeParams.addAll(bastionHttpRequest.routeParams());
        String urlWithPlaceholders = bastionHttpRequest.url();
        for (RouteParam routeParam : routeParams) {
            urlWithPlaceholders = urlWithPlaceholders.replaceAll("\\Q{" + routeParam.getName() + "}\\E", routeParam.getValue());
        }
        uriBuilder.setPath(urlWithPlaceholders);
    }

    private void applyBody() {
        if (executableHttpRequest instanceof HttpEntityEnclosingRequest) {
            ((HttpEntityEnclosingRequest) executableHttpRequest).setEntity(new StringEntity(
                    bastionHttpRequest.body().toString(),
                    bastionHttpRequest.contentType().orElse(null)
            ));
        }
    }

    private CloseableHttpResponse performRequest() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            return httpClient.execute(executableHttpRequest);
        }
    }

    private Response convertToRawResponse(CloseableHttpResponse httpResponse) throws IOException {
        return new RawResponse(httpResponse.getStatusLine().getStatusCode(),
                               httpResponse.getStatusLine().getReasonPhrase(),
                               Arrays.stream(httpResponse.getAllHeaders())
                                     .map(header -> new ApiHeader(header.getName(), header.getValue()))
                                     .collect(Collectors.toList()),
                               httpResponse.getEntity().getContent());
    }

    private static long resolveTimeoutOrFallbackToGlobal(HttpRequest request, Configuration configuration) {
        if (request.timeout() == HttpRequest.USE_GLOBAL_TIMEOUT) {
            return configuration.getGlobalRequestAttributes().getGlobalRequestTimeout();
        } else {
            return request.timeout();
        }
    }
}
