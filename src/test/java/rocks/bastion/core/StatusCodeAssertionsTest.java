package rocks.bastion.core;

import org.junit.Test;
import rocks.bastion.core.view.Bindings;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class StatusCodeAssertionsTest {

    @Test
    public void execute_singleCodePasses() throws Exception {
        testAssertion(400, 400);
    }

    @Test
    public void execute_singleCodeFails() throws Exception {
        assertThatThrownBy(() -> testAssertion(401, 400)).describedAs("Status Code Assertion").isInstanceOf(AssertionError.class);
    }

    @Test
    public void execute_manyCodesPasses() throws Exception {
        testAssertion(401, 400, 401);
    }

    @Test
    public void execute_manyCodesFails() throws Exception {
        assertThatThrownBy(() -> testAssertion(405, 400, 401)).describedAs("Status Code Assertion").isInstanceOf(AssertionError.class);
    }

    @Test
    public void init_emptyPrimitiveIntExpectation() throws Exception {
        assertThatThrownBy(() -> StatusCodeAssertions.expecting(new int[0])).describedAs("Empty constructor").isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void init_emptyIterableExpectation() throws Exception {
        assertThatThrownBy(() -> StatusCodeAssertions.expecting(Collections.emptyList())).describedAs("Empty constructor").isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void initAndExecute_literalIntegersPassed_assertionCreatedCorrectly() throws Exception {
        StatusCodeAssertions assertion = StatusCodeAssertions.expecting(401, 404);
        assertion.execute(401, getResponseWithStatusCode(401), "Model");
    }

    private void testAssertion(int actual, int... expected) {
        StatusCodeAssertions assertion = StatusCodeAssertions.expecting(expected);
        assertion.execute(actual, getResponseWithStatusCode(actual), "Model");
    }

    private ModelResponse<String> getResponseWithStatusCode(int statusCode) {
        String data = "Model";
        return new ModelResponse<>(new RawResponse(statusCode, "Status Code", Collections.emptyList(), new ByteArrayInputStream(new byte[0])),
                                   data,
                                   Bindings.single(String.class, data));
    }
}