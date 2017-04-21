package rocks.bastion.documentation;

import org.junit.Ignore;
import org.junit.Test;
import rocks.bastion.Bastion;
import rocks.bastion.core.GeneralRequest;
import rocks.bastion.core.json.JsonResponseAssertions;

@Ignore
public class VatValidatorTest {
    @Test
    public void callValidateVatNumber_returnsAmazonVatData() {
        Bastion.request("Validate Amazon's VAT Number", GeneralRequest.get("http://vatlayer.com/php_helper_scripts/vat_api.php")
                .addQueryParam("secret_key", "577b6fb6551df7f3532ecbd45ea07ddd")
                .addQueryParam("vat_number", "LU26375245")
        ).withAssertions(JsonResponseAssertions.fromResource(200, "classpath:/json/amazon_vat.json")).call();
    }
}
