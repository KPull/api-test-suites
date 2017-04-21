package rocks.bastion.core;

import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.json.JsonResponseAssertions;
import rocks.bastion.support.embedded.Sushi;
import rocks.bastion.support.embedded.TestWithEmbeddedServer;

import static org.assertj.core.api.Assertions.assertThat;

public class FileRequestTest extends TestWithEmbeddedServer {

    @Test
    public void post() throws Exception {
        Bastion.request("Create Sushi", FileRequest.post("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json"))
                .bind(Sushi.class)
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json").ignoreValuesForProperties("/id"))
                .call();
    }

    @Test
    public void put() throws Exception {
        Bastion.request("Create Sushi", FileRequest.put("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json"))
                .bind(Sushi.class)
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json").ignoreValuesForProperties("/id"))
                .call();
    }

    @Test
    public void delete() throws Exception {
        Bastion.request("Create Sushi", FileRequest.delete("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json"))
                .bind(Sushi.class)
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json").ignoreValuesForProperties("/id"))
                .call();
    }

    @Test
    public void patch() throws Exception {
        Bastion.request("Create Sushi", FileRequest.patch("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json"))
                .bind(Sushi.class)
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json").ignoreValuesForProperties("/id"))
                .call();
    }

    @Test
    public void withMethod() throws Exception {
        Bastion.request("Create Sushi", FileRequest.withMethod(HttpMethod.POST, "http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json"))
                .bind(Sushi.class)
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json").ignoreValuesForProperties("/id"))
                .call();
    }

    @Test
    public void headers() throws Exception {
        FileRequest request = FileRequest.post("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json")
                .addHeader("name1", "value1")
                .addHeader("name2", "value2");
        assertThat(request.headers()).describedAs("Request headers").containsExactlyInAnyOrder(
                new ApiHeader("name1", "value1"),
                new ApiHeader("name2", "value2")
        );
    }

    @Test
    public void queryParams() throws Exception {
        FileRequest request = FileRequest.post("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json")
                .addQueryParam("name1", "value1")
                .addQueryParam("name2", "value2");
        assertThat(request.queryParams()).describedAs("Request query params").containsExactlyInAnyOrder(
                new ApiQueryParam("name1", "value1"),
                new ApiQueryParam("name2", "value2")
        );
    }

    @Test
    public void routeParams() throws Exception {
        FileRequest request = FileRequest.post("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json")
                .addRouteParam("name1", "value1")
                .addRouteParam("name2", "value2");
        assertThat(request.routeParams()).describedAs("Request route params").containsExactlyInAnyOrder(
                new RouteParam("name1", "value1"),
                new RouteParam("name2", "value2")
        );
    }

    @Test
    public void url() throws Exception {
        FileRequest request = FileRequest.post("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json");
        assertThat(request.url()).describedAs("Request URL").isEqualTo("http://localhost:9876/sushi");
    }

    @Test
    public void body() throws Exception {
        FileRequest request = FileRequest.post("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json");
        assertThat(request.body()).describedAs("Request Body").isEqualTo("{\n" +
                "  \"name\": \"sashimi\",\n" +
                "  \"price\": \"5.60\",\n" +
                "  \"type\": \"SASHIMI\"\n" +
                "}");
    }

    @Test
    public void timeout() throws Exception {
        FileRequest request = FileRequest.post("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json")
                .setTimeout(5000L);
        assertThat(request.timeout()).describedAs("Request timeout").isEqualTo(5000L);
    }

    @Test
    public void contentType_unknownFileType_contentTypeShouldBePlainText() throws Exception {
        FileRequest request = FileRequest.post("http://localhost:9876/sushi", "classpath:/rocks/bastion/core/request/unknown.unk");
        assertThat(request.contentType().get().getMimeType()).describedAs("Request content-type").isEqualTo("text/plain");
    }

    @Test
    public void contentType_jsonFileType_contentTypeShouldBeJson() throws Exception {
        FileRequest request = FileRequest.post("http://localhost:9876/sushi", "classpath:/json/create_sushi_request.json");
        assertThat(request.contentType().get().getMimeType()).describedAs("Request content-type").isEqualTo("application/json");
    }
}