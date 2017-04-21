package rocks.bastion.core.assertions;

import rocks.bastion.core.ApiHeader;
import rocks.bastion.core.ModelResponse;
import rocks.bastion.core.RawResponse;
import rocks.bastion.core.view.Bindings;

import java.io.ByteArrayInputStream;
import java.util.Collections;

class TestModelResponse {

    /**
     * Prepare a model response object with a status code of HTTP 200 and the given JSON body for testing purposes.
     * The content type of the response will be set to "application/json".
     *
     * @param jsonContent
     * @return A model response object.
     */
    static ModelResponse<String> prepare(String jsonContent) {
        return prepare(jsonContent, "application/json");
    }

    /**
     * Prepare a model response object with a status code of HTTP 200, as well as the given JSON body and content type,
     * for testing purposes.
     *
     * @param jsonContent
     * @param contentType
     * @return A model response object.
     */
    static ModelResponse<String> prepare(String jsonContent, String contentType) {
        return new ModelResponse<>(new RawResponse(200,
                                                   "OK",
                                                   Collections.singletonList(new ApiHeader("Content-type", contentType)), new ByteArrayInputStream(jsonContent.getBytes())),
                                   jsonContent, Bindings.single(String.class, jsonContent));
    }

}
