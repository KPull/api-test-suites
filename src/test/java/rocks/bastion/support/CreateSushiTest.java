package rocks.bastion.support;

import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.json.JsonRequest;
import rocks.bastion.core.json.JsonResponseAssertions;
import rocks.bastion.support.embedded.Sushi;
import rocks.bastion.support.embedded.TestWithEmbeddedServer;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateSushiTest extends TestWithEmbeddedServer {

    @Test
    public void testCreateSushi_Success() {
        Bastion.request("SUCCESS", new CreateSushiRequest())
                .bind(Sushi.class)
                .withAssertions((statusCode, response, model) -> {
                    assertThat(response.getContentType().isPresent()).isTrue();
                    assertThat(response.getContentType().get().getMimeType()).isEqualToIgnoringCase("application/json");
                    assertThat(statusCode).isEqualTo(201);
                    assertThat(model.getName()).isEqualTo("happiness");
                })
                .call();

        Bastion.request("SUCCESS (Again)", new CreateSushiRequest())
                .bind(Sushi.class)
                .withAssertions((statusCode, response, model) -> {
                    assertThat(response.getContentType().isPresent()).isTrue();
                    assertThat(response.getContentType().get().getMimeType()).isEqualToIgnoringCase("application/json");
                    assertThat(statusCode).isEqualTo(201);
                    assertThat(model.getName()).isEqualTo("happiness");
                })
                .call();
    }

    @Test
    public void secondTestCreateSushi_Success() {
        // docs:json-request-example
        Bastion.request("First Request", JsonRequest.postFromString("http://localhost:9876/sushi", "{ " +
                "\"name\":\"sashimi\", " +
                "\"price\":\"5.60\", " +
                "\"type\":\"SASHIMI\" " +
                "}"
        )).withAssertions(JsonResponseAssertions.fromString(201, "{ " +
                        "\"id\":5, " +
                        "\"name\":\"sashimi\", " +
                        "\"price\":5.60, " +
                        "\"type\":\"SASHIMI\" " +
                        "}"
                ).ignoreValuesForProperties("/id")
        ).call();
        // docs:json-request-example
    }
}
