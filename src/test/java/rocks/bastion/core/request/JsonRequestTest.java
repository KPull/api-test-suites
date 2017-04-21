package rocks.bastion.core.request;

import org.apache.http.entity.ContentType;
import org.junit.Test;
import rocks.bastion.core.ApiHeader;
import rocks.bastion.core.ApiQueryParam;
import rocks.bastion.core.HttpMethod;
import rocks.bastion.core.json.InvalidJsonException;
import rocks.bastion.core.json.JsonRequest;
import rocks.bastion.core.resource.ResourceNotFoundException;
import rocks.bastion.support.embedded.Sushi;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonRequestTest {

    @Test(expected = InvalidJsonException.class)
    public void fromString_invalidJson_shouldThrowException() throws Exception {
        JsonRequest request = JsonRequest.fromString(HttpMethod.POST, "http://test.test", "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" }");
    }

    @Test
    public void fromString_validJson_shouldReturnARequest() throws Exception {
        JsonRequest request = JsonRequest.fromString(HttpMethod.POST, "http://test.test", "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
        assertJsonRequestAttributes(request, "POST http://test.test", HttpMethod.POST, "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
    }

    @Test
    public void postFromString_validJson_shouldReturnARequest() throws Exception {
        JsonRequest request = JsonRequest.postFromString("http://test.test", "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
        assertJsonRequestAttributes(request, "POST http://test.test", HttpMethod.POST, "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
    }

    @Test
    public void putFromString_validJson_shouldReturnARequest() throws Exception {
        JsonRequest request = JsonRequest.putFromString("http://test.test", "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
        assertJsonRequestAttributes(request, "PUT http://test.test", HttpMethod.PUT, "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
    }

    @Test
    public void fromFile_validJson_shouldReturnARequest() throws Exception {
        JsonRequest request = JsonRequest.fromResource(HttpMethod.POST, "http://test.test", getValidJsonFile());
        assertJsonRequestAttributes(request, "POST http://test.test", HttpMethod.POST, "{\n" +
                "  \"name\": \"john\",\n" +
                "  \"timestamp\": \"2016-10-15T20:00:25+0100\",\n" +
                "  \"favourites\": {\n" +
                "    \"food\": \"apples\",\n" +
                "    \"colours\": [\"blue\", \"red\"],\n" +
                "    \"number\": 23\n" +
                "  }\n" +
                "}");
    }

    @Test
    public void postFromFile_validJson_shouldReturnARequest() throws Exception {
        JsonRequest request = JsonRequest.postFromResource("http://test.test", getValidJsonFile());
        assertJsonRequestAttributes(request, "POST http://test.test", HttpMethod.POST, "{\n" +
                "  \"name\": \"john\",\n" +
                "  \"timestamp\": \"2016-10-15T20:00:25+0100\",\n" +
                "  \"favourites\": {\n" +
                "    \"food\": \"apples\",\n" +
                "    \"colours\": [\"blue\", \"red\"],\n" +
                "    \"number\": 23\n" +
                "  }\n" +
                "}");
    }

    @Test
    public void fromTemplate_validJson_shouldReturnARequest() throws Exception {
        JsonRequest request = JsonRequest.fromTemplate(HttpMethod.POST, "http://test.test", getValidJsonTemplateFile(), Collections.singletonMap("food", "apples"));
        assertJsonRequestAttributes(request, "POST http://test.test", HttpMethod.POST, "{\n" +
                "  \"name\": \"john\",\n" +
                "  \"timestamp\": \"2016-10-15T20:00:25+0100\",\n" +
                "  \"favourites\": {\n" +
                "    \"food\": \"apples\",\n" +
                "    \"colours\": [\"blue\", \"red\"],\n" +
                "    \"number\": 23\n" +
                "  }\n" +
                "}");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void putFromFile_inexistantFile_shouldThrowAnException() throws Exception {
        JsonRequest request = JsonRequest.putFromResource("http://test.test", "inexistant.json");
    }

    @Test
    public void fromModel_validModel_shouldReturnARequest() throws Exception {
        Sushi model = Sushi.newSushi().id(19).name("Salmon Nigiri").price(10L).type(Sushi.Type.NIGIRI).build();
        JsonRequest jsonRequest = JsonRequest.fromModel(HttpMethod.POST, "http://test.test", model);
        assertJsonRequestAttributes(jsonRequest, "POST http://test.test", HttpMethod.POST,
                "{\"id\":19,\"name\":\"Salmon Nigiri\",\"price\":10,\"type\":\"NIGIRI\"}");
    }

    @Test
    public void fromModel_parametrisedTypeModel_shouldReturnARequest() throws Exception {
        List<Sushi> model = new LinkedList<>();
        model.add(Sushi.newSushi().id(19).name("Salmon Nigiri").price(10L).type(Sushi.Type.NIGIRI).build());
        model.add(Sushi.newSushi().id(29).name("Tuna Nigiri").price(20L).type(Sushi.Type.NIGIRI).build());
        JsonRequest jsonRequest = JsonRequest.fromModel(HttpMethod.POST, "http://test.test", model);
        assertJsonRequestAttributes(jsonRequest, "POST http://test.test", HttpMethod.POST,
                "[{\"id\":19,\"name\":\"Salmon Nigiri\",\"price\":10,\"type\":\"NIGIRI\"},{\"id\":29,\"name\":\"Tuna Nigiri\",\"price\":20,\"type\":\"NIGIRI\"}]");
    }

    @Test
    public void postFromModel_validModel_shouldReturnARequest() throws Exception {
        Sushi model = Sushi.newSushi().id(19).name("Salmon Nigiri").price(10L).type(Sushi.Type.NIGIRI).build();
        JsonRequest jsonRequest = JsonRequest.postFromModel("http://test.test", model);
        assertJsonRequestAttributes(jsonRequest, "POST http://test.test", HttpMethod.POST,
                "{\"id\":19,\"name\":\"Salmon Nigiri\",\"price\":10,\"type\":\"NIGIRI\"}");
    }

    @Test
    public void putFromModel_validModel_shouldReturnARequest() throws Exception {
        Sushi model = Sushi.newSushi().id(19).name("Salmon Nigiri").price(10L).type(Sushi.Type.NIGIRI).build();
        JsonRequest jsonRequest = JsonRequest.putFromModel("http://test.test", model);
        assertJsonRequestAttributes(jsonRequest, "PUT http://test.test", HttpMethod.PUT,
                "{\"id\":19,\"name\":\"Salmon Nigiri\",\"price\":10,\"type\":\"NIGIRI\"}");
    }

    @Test
    public void patchFromModel_validModel_shouldReturnARequest() throws Exception {
        Sushi model = Sushi.newSushi().id(19).name("Salmon Nigiri").price(10L).type(Sushi.Type.NIGIRI).build();
        JsonRequest jsonRequest = JsonRequest.patchFromModel("http://test.test", model);
        assertJsonRequestAttributes(jsonRequest, "PATCH http://test.test", HttpMethod.PATCH,
                "{\"id\":19,\"name\":\"Salmon Nigiri\",\"price\":10,\"type\":\"NIGIRI\"}");
    }


    @Test
    public void deleteFromModel_validModel_shouldReturnARequest() throws Exception {
        Sushi model = Sushi.newSushi().id(19).name("Salmon Nigiri").price(10L).type(Sushi.Type.NIGIRI).build();
        JsonRequest jsonRequest = JsonRequest.deleteFromModel("http://test.test", model);
        assertJsonRequestAttributes(jsonRequest, "DELETE http://test.test", HttpMethod.DELETE,
                "{\"id\":19,\"name\":\"Salmon Nigiri\",\"price\":10,\"type\":\"NIGIRI\"}");
    }

    @Test
    public void overrideContentType() throws Exception {
        JsonRequest request = JsonRequest.fromString(HttpMethod.POST, "http://test.test", "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
        request.overrideContentType(ContentType.TEXT_PLAIN);
        assertThat(request.contentType()).isEqualTo(Optional.ofNullable(ContentType.TEXT_PLAIN));
    }

    @Test
    public void addHeader() throws Exception {
        JsonRequest request = JsonRequest.fromString(HttpMethod.POST, "http://test.test", "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
        request.addHeader("header1", "value1");
        request.addHeader("header2", "value2");
        assertThat(request.headers()).hasSize(2);
        assertThat(request.headers()).containsExactly(new ApiHeader("header1", "value1"), new ApiHeader("header2", "value2"));
    }

    @Test
    public void addQueryParam() throws Exception {
        JsonRequest request = JsonRequest.fromString(HttpMethod.POST, "http://test.test", "{ \"key\":\"value\", \"array\":[ \"1\", \"test\" ] }");
        request.addQueryParam("query1", "value1");
        request.addQueryParam("query2", "value2");
        assertThat(request.queryParams()).hasSize(2);
        assertThat(request.queryParams()).containsExactly(new ApiQueryParam("query1", "value1"), new ApiQueryParam("query2", "value2"));
    }

    private void assertJsonRequestAttributes(JsonRequest request, String expectedName, HttpMethod expectedMethod, String expectedBody) {
        assertThat(request.name()).describedAs("Request Name").isEqualTo(expectedName);
        assertThat(request.url()).describedAs("Request URL").isEqualTo("http://test.test");
        assertThat(request.method()).describedAs("Request Method").isEqualTo(expectedMethod);
        assertThat(request.contentType().isPresent()).describedAs("Request Content-type is present").isTrue();
        assertThat(request.contentType().get()).describedAs("Request Content-type").isEqualTo(ContentType.APPLICATION_JSON);
        assertThat(request.headers()).describedAs("Request Headers").isEmpty();
        assertThat(request.queryParams()).describedAs("Request Query Parameters").isEmpty();
        assertThat(request.body()).describedAs("Request Body").isEqualTo(expectedBody);
    }

    private String getValidJsonFile() throws URISyntaxException {
        return "classpath:/rocks/bastion/core/request/test-body.json";
    }

    private String getValidJsonTemplateFile() throws URISyntaxException {
        return "classpath:/rocks/bastion/core/request/test-template-body.json";
    }

}