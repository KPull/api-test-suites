package rocks.bastion.core.printer;

import org.apache.http.entity.ContentType;
import org.junit.Test;
import rocks.bastion.core.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:mail@kylepullicino.com">Kyle</a>
 */
public class HttpRequestPrinterTest {

    @Test
    public void getAsString() throws Exception {
        HttpRequestPrinter printer = new HttpRequestPrinter(prepareRequest());
        assertThat(printer.getAsString()).isEqualTo("POST /oisaje?query1=value2 HTTP/1.1\r\n" +
                "Host: test.test\r\n" +
                "Header1: value1\r\n" +
                "Authorization: token\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n" +
                "\r\n" +
                "{\n" +
                "\t\"tkoast\":\"raskpor\",\n" +
                "\t\"skoipra\":\"smroiar\"\n" +
                "}");
    }

    private HttpRequest prepareRequest() {
        return new HttpRequest() {
            @Override
            public String name() {
                return "Name";
            }

            @Override
            public String url() {
                return "http://test.test/oisaje";
            }

            @Override
            public HttpMethod method() {
                return HttpMethod.POST;
            }

            @Override
            public Optional<ContentType> contentType() {
                return Optional.ofNullable(ContentType.APPLICATION_JSON);
            }

            @Override
            public Collection<ApiHeader> headers() {
                return Arrays.asList(new ApiHeader("Header1", "value1"), new ApiHeader("Authorization", "token"));
            }

            @Override
            public Collection<ApiQueryParam> queryParams() {
                return Arrays.asList(new ApiQueryParam("query1", "value2"));
            }

            @Override
            public Collection<RouteParam> routeParams() {
                return Collections.emptyList();
            }

            @Override
            public Object body() {
                return "{\n" +
                        "\t\"tkoast\":\"raskpor\",\n" +
                        "\t\"skoipra\":\"smroiar\"\n" +
                        "}";
            }
        };
    }

}