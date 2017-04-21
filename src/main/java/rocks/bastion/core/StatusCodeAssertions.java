package rocks.bastion.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performs assertions on a response by checking for the HTTP status code. The user supplies either one status code or
 * multiple status codes for which the response will pass the assertions.
 */
public final class StatusCodeAssertions implements Assertions<Object> {

    /**
     * Initialise a assertions object expecting the HTTP response status code to be equal to any one of the given status codes.
     *
     * @param expectedStatusCodes The non-empty array of status codes to match against
     * @return An assertions object for use with the {@link rocks.bastion.core.builder.AssertionsBuilder#withAssertions(Assertions)} method
     * @throws IllegalArgumentException Thrown if the supplied array is empty
     */
    public static StatusCodeAssertions expecting(int... expectedStatusCodes) {
        return expecting(Ints.asList(expectedStatusCodes));
    }

    /**
     * Initialise a assertions object expecting the HTTP response status code to be equal to any one of the given status codes.
     *
     * @param expectedStatusCodes The non-empty iterable of status codes to match against
     * @return An assertions object for use with the {@link rocks.bastion.core.builder.AssertionsBuilder#withAssertions(Assertions)} method
     * @throws IllegalArgumentException Thrown if the supplied array is empty
     */
    public static StatusCodeAssertions expecting(Iterable<Integer> expectedStatusCodes) {
        return new StatusCodeAssertions(ImmutableSet.copyOf(expectedStatusCodes));
    }

    private final Set<Integer> expectedStatusCodes;

    private StatusCodeAssertions(Set<Integer> expectedStatusCodes) {
        Objects.requireNonNull(expectedStatusCodes);
        if (expectedStatusCodes.isEmpty()) {
            throw new IllegalArgumentException("The given list of status codes cannot be empty");
        }
        this.expectedStatusCodes = expectedStatusCodes;
    }

    @Override
    public void execute(int statusCode, ModelResponse<?> response, Object model) throws AssertionError {
        assertThat(statusCode).describedAs("HTTP Response Status Code").matches(expectedStatusCodes::contains);
    }
}
