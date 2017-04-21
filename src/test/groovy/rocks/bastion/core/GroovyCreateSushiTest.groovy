package rocks.bastion.core

import org.junit.Test
import rocks.bastion.Bastion
import rocks.bastion.core.json.JsonRequest
import rocks.bastion.core.json.JsonResponseAssertions
import rocks.bastion.support.embedded.TestWithEmbeddedServer

class GroovyCreateSushiTest extends TestWithEmbeddedServer {

    @Test
    public void secondTestCreateSushi_Success() {
        // docs:groovy-example
        Bastion.request("First Request", JsonRequest.postFromString("http://localhost:9876/sushi",
                '''{
                "name":"sashimi",
                "price":"5.60",
                "type":"SASHIMI"
            }'''
        )).withAssertions(JsonResponseAssertions.fromString(201,
                '''{
                   "id":5,
                   "name":"sashimi",
                   "price":5.60,
                   "type":"SASHIMI"
                }'''
        ).ignoreValuesForProperties("/id")
        ).call()
        // docs:groovy-example
    }
}