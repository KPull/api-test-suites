package rocks.bastion.support;

import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.support.embedded.TestWithEmbeddedServer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see <a href="https://github.com/KPull/Bastion/issues/37">Issue #37</a>
 */
public class DefaultBindingTest extends TestWithEmbeddedServer {

    @Test
    public void testDefaultBinding() {
        Bastion.request("Create Sushi Request", new CreateSushiRequest())
                .withAssertions((statusCode, response, model) -> {
                    assertThat(statusCode).isEqualTo(201);
                }).call();
    }

}
