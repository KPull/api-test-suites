package rocks.bastion.core;

import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.builder.BastionBuilder;
import rocks.bastion.support.embedded.Sushi;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BastionBuilderImplTest {

    @Test
    public void enforceMethodOrder_bindBeforeAssertions() {
        BastionBuilder<Object> builder = Bastion.request(GeneralRequest.get("http://test.test"));
        builder.withAssertions((statusCode, response, model) -> System.out.println("Hi there"));
        assertThatThrownBy(() -> builder.bind(Sushi.class)).isInstanceOf(IllegalStateException.class)
                                                           .hasMessage("bind() must be called before withAssertions()");
    }

    @Test
    public void enforceMethodOrder_bindTwice() {
        BastionBuilder<Object> builder = Bastion.request(GeneralRequest.get("http://test.test"));
        builder.bind(Sushi.class);
        assertThatThrownBy(() -> builder.bind(Sushi.class)).isInstanceOf(IllegalStateException.class)
                                                           .hasMessage("bind() has been called twice in a row");
    }

    @Test
    public void enforceMethodOrder_withAssertionsTwice() {
        BastionBuilder<Object> builder = Bastion.request(GeneralRequest.get("http://test.test"));
        builder.withAssertions((statusCode, response, model) -> System.out.println("Hi there"));
        assertThatThrownBy(() -> builder.withAssertions((statusCode, response, model) -> System.out.println("Hi there")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("withAssertions() has been called twice in a row");
    }
}