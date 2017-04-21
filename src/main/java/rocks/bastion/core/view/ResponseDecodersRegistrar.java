package rocks.bastion.core.view;

/**
 * Responsible for registering {@link ResponseDecoder}s with Bastion instances.
 *
 * @author <a href="mailto:mail@kylepullicino.com">Kyle</a>
 */
public interface ResponseDecodersRegistrar {

    /**
     * Registers the specified {@linkplain ResponseDecoder} for use with Bastion.
     *
     * @param decoder A non-{@literal null} response decoder object
     */
    void registerModelConverter(ResponseDecoder decoder);

}
