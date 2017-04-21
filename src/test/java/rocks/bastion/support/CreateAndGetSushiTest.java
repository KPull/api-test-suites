package rocks.bastion.support;

import org.apache.http.entity.ContentType;
import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.Assertions;
import rocks.bastion.core.GeneralRequest;
import rocks.bastion.core.json.JsonRequest;
import rocks.bastion.core.json.JsonResponseAssertions;
import rocks.bastion.support.embedded.Sushi;
import rocks.bastion.support.embedded.TestWithEmbeddedServer;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAndGetSushiTest extends TestWithEmbeddedServer {

    @Test
    public void createAndGetSameSushi_Success() {
        // docs:bound-data
        Sushi createdSushi = Bastion.request("Create Sushi", JsonRequest.postFromString("http://localhost:9876/sushi", "{ " +
                "\"name\":\"sashimi\", " +
                "\"price\":\"5.60\", " +
                "\"type\":\"SASHIMI\" " +
                "}"
        )).bind(Sushi.class).withAssertions(JsonResponseAssertions.fromString(201, "{ " +
                        "\"id\":5, " +
                        "\"name\":\"sashimi\", " +
                        "\"price\":5.60, " +
                        "\"type\":\"SASHIMI\" " +
                        "}"
                ).ignoreValuesForProperties("id")
        ).call().getModel();
        // docs:bound-data

        Bastion.request("Get Sushi", GeneralRequest.get("http://localhost:9876/sushi/" + createdSushi.getId()))
                .bind(Sushi.class).withAssertions((Assertions<Sushi>) (statusCode, response, model) -> {
            assertThat(statusCode).describedAs("Status Code").isEqualTo(200);
            assertThat(response.getContentType().isPresent()).describedAs("Content Type Header exists").isTrue();
            assertThat(response.getContentType().get().getMimeType()).describedAs("Content Type Header").isEqualTo(ContentType.APPLICATION_JSON.getMimeType());
            assertThat(model).describedAs("Returned Sushi").isEqualTo(createdSushi);
        }).call();
    }
}
