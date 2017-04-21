package rocks.bastion.core;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.json.JsonResponseAssertions;
import rocks.bastion.support.embedded.Sushi;
import rocks.bastion.support.embedded.TestWithEmbeddedServer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseViewsTest extends TestWithEmbeddedServer {

    @Test
    public void stringResponseView() throws Exception {
        ModelResponse<? extends Sushi> response = Bastion.request(FileRequest.post("http://localhost:9876/sushi",
                                                                                   "classpath:/json/create_sushi_request.json"))
                                                         .bind(Sushi.class)
                                                         .withAssertions(JsonResponseAssertions.fromResource(201,
                                                                                                             "classpath:/json/create_sushi_response.json")
                                                                                               .ignoreValuesForProperties("/id"))
                                                         .call()
                                                         .getResponse();
        Optional<String> responseString = response.getView(String.class);
        assertThat(responseString).isNotEmpty();
    }

    @Test
    public void jsonSerializableResponseView() throws Exception {
        ModelResponse<? extends Sushi> response = Bastion.request(FileRequest.post("http://localhost:9876/sushi",
                                                                                   "classpath:/json/create_sushi_request.json"))
                                                         .bind(Sushi.class)
                                                         .call()
                                                         .getResponse();
        Optional<JsonSerializable.Base> node = response.getView(JsonSerializable.Base.class);
        assertThat(node).isNotEmpty();
    }

    @Test
    public void treeNodeResponseView() throws Exception {
        ModelResponse<? extends Sushi> response = Bastion.request(FileRequest.post("http://localhost:9876/sushi",
                                                                                   "classpath:/json/create_sushi_request.json"))
                                                         .bind(Sushi.class)
                                                         .call()
                                                         .getResponse();
        Optional<TreeNode> node = response.getView(TreeNode.class);
        assertThat(node).isNotEmpty();
    }

    @Test
    public void treeNodeResponseView_afterCall() throws Exception {
        Optional<TreeNode> responseNode = Bastion.request(FileRequest.post("http://localhost:9876/sushi",
                                                                           "classpath:/json/create_sushi_request.json"))
                                                 .bind(Sushi.class)
                                                 .call()
                                                 .getView(TreeNode.class);
        assertThat(responseNode).isNotNull();
    }

    @Test
    public void jsonBoundResponseView() throws Exception {
        ModelResponse<? extends Sushi> response = Bastion.request(FileRequest.post("http://localhost:9876/sushi",
                                                                                   "classpath:/json/create_sushi_request.json"))
                                                         .bind(Sushi.class)
                                                         .call()
                                                         .getResponse();
        Optional<Sushi> boundModel = response.getView(Sushi.class);
        assertThat(boundModel).isNotEmpty();
    }

    @Test
    public void inexistantResponseView() throws Exception {
        ModelResponse<? extends Sushi> response = Bastion.request(FileRequest.post("http://localhost:9876/sushi",
                                                                                   "classpath:/json/create_sushi_request.json"))
                                                         .bind(Sushi.class)
                                                         .call()
                                                         .getResponse();
        Optional<Integer> boundModel = response.getView(Integer.class);
        assertThat(boundModel).isEmpty();
    }
}
