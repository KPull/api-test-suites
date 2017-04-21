package rocks.bastion.core.view;

import com.google.common.collect.Lists;
import org.junit.Test;
import rocks.bastion.core.RawResponse;
import rocks.bastion.core.Response;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewBinderTest {

    @Test
    public void bind_actualTypes() throws Exception {
        ViewBinder viewbinder = viewBinderForStrings();
        assertThat(viewbinder.bind(new DecodingHints(String.class)).getViewForType(String.class)).hasValue("Bound string");
        assertThat(viewbinder.bind(new DecodingHints(StringBuffer.class)).getViewForType(StringBuffer.class))
                .hasValueSatisfying(buffer -> buffer.toString().equals("Bound string buffer"));
    }

    @Test
    public void bind_unboundType_emptyOptional() throws Exception {
        ViewBinder viewbinder = viewBinderForStrings();
        assertThat(viewbinder.bind(new DecodingHints(String.class)).getViewForType(Integer.class)).isEmpty();
    }

    @Test
    public void bind_multipleViewsForType_overrideBinding() throws Exception {
        ViewBinder viewbinder = viewBinderForStrings();
        assertThat(viewbinder.bind(new DecodingHints(StringBuffer.class)).getViewForType(StringBuffer.class))
                .hasValueSatisfying(buffer -> buffer.toString().equals("Bound string buffer"));
    }

    private ViewBinder viewBinderForStrings() {
        return new ViewBinder(new RawResponse(200, "OK", Collections.emptyList(), new ByteArrayInputStream(new byte[0])),
                              Lists.newArrayList(new TestDecoder<>(String.class, "Bound string"),
                                                 new TestDecoder<>(StringBuffer.class,
                                                                   new StringBuffer("Bound string buffer"))));
    }

    private static final class TestDecoder<T> implements ResponseDecoder {

        private final Class<T> bindType;
        private final T viewObject;

        private TestDecoder(Class<T> bindType, T viewObject) {
            this.bindType = bindType;
            this.viewObject = viewObject;
        }

        @Override
        public Bindings decode(Response response, DecodingHints hints) {
            return Bindings.hierarchy(bindType, viewObject);
        }
    }
}