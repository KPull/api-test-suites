package rocks.bastion.core

import org.junit.Ignore
import org.junit.Test
import rocks.bastion.Bastion
import rocks.bastion.core.json.JsonResponseAssertions

@Ignore
class GoogleBooksApiTest {

    @Test
    public void searchHarryPotter() {
        Bastion.request("Query 'Harry Potter'", GeneralRequest.get("https://www.googleapis.com/books/v1/volumes/{id}")
                .addRouteParam("id", "wrOQLV6xB-wC"))
                .withAssertions(JsonResponseAssertions.fromResource(200, "classpath:/json/harry_potter_results.json")
                .ignoreValuesForProperties("/etag", "/volumeInfo/imageLinks/thumbnail", "/volumeInfo/imageLinks/smallThumbnail", "/volumeInfo/ratingsCount"))
                .call()
    }

}
