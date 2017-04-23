package rocks.bastion.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import rocks.bastion.core.ApiHeader;
import rocks.bastion.core.ApiQueryParam;
import rocks.bastion.core.HttpMethod;
import rocks.bastion.core.HttpRequest;
import rocks.bastion.core.RouteParam;
import rocks.bastion.support.embedded.Sushi;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CreateSushiRequest implements HttpRequest {

    @Override
    public String name() {
        return "Create Sushi Request";
    }

    @Override
    public String url() {
        return "http://localhost:9876/sushi";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public Optional<ContentType> contentType() {
        return Optional.of(ContentType.APPLICATION_JSON);
    }

    @Override
    public Collection<ApiHeader> headers() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ApiQueryParam> queryParams() {
        return Collections.emptyList();
    }

    @Override
    public Collection<RouteParam> routeParams() {
        return Collections.emptyList();
    }

    @Override
    public Object body() {
        try {
            return new ObjectMapper().writeValueAsString(Sushi.newSushi().name("happiness").build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
