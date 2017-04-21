package rocks.bastion.support;

import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.GeneralRequest;
import rocks.bastion.core.json.JsonRequest;
import rocks.bastion.core.json.JsonResponseAssertions;
import rocks.bastion.core.json.JsonSchemaAssertions;
import rocks.bastion.support.embedded.Sushi;
import rocks.bastion.support.embedded.TestWithEmbeddedServer;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAndGetSushiFromFileTest extends TestWithEmbeddedServer {

    private static final String BASE_URL = "http://localhost:9876/sushi";

    @Test
    public void createAndGetSameSushi_Success() throws URISyntaxException {
        Sushi createdSushi = Bastion.request("Create Sushi", JsonRequest.postFromResource(BASE_URL, "classpath:/json/create_sushi_request.json"))
                .bind(Sushi.class)
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json").ignoreValuesForProperties("/id"))
                .call()
                .getModel();

        Sushi gottenSushi = Bastion.request("Get Sushi", GeneralRequest.get(BASE_URL + "/" + createdSushi.getId()))
                .bind(Sushi.class)
                .withAssertions(JsonResponseAssertions.fromResource(200, "classpath:/json/create_sushi_response.json").ignoreValuesForProperties("/id"))
                .call()
                .getModel();

        assertThat(gottenSushi.getId()).isEqualTo(createdSushi.getId());
    }

    @Test
    public void creatSushi_noBind_Success() throws URISyntaxException {
        // docs:load-from-file
        Bastion.request("Create Sushi", JsonRequest.postFromResource(BASE_URL, "classpath:/json/create_sushi_request.json"))
                .withAssertions(JsonResponseAssertions.fromResource(201, "classpath:/json/create_sushi_response.json").ignoreValuesForProperties("/id"))
                .call();
        // docs:load-from-file
    }

    @Test
    public void createSushiRequest_ValidateJsonSchemaOfResponse_Success() {
        Bastion.request("Valid Response JSON Schema", new CreateSushiRequest())
               .bind(Sushi.class)
               .withAssertions(JsonSchemaAssertions.fromResource("classpath:/json/create_sushi_response_schema.json"))
               .call();
    }

}