package rocks.bastion.core;

import rocks.bastion.Bastion;
import rocks.bastion.core.builder.BastionBuilder;
import rocks.bastion.core.configuration.BastionConfigurationLoader;
import rocks.bastion.core.configuration.Configuration;

import static java.util.Objects.requireNonNull;

/**
 * Creates and configures an instance of the {@link BastionBuilderImpl} fluent builder. A single factory can be designated as the
 * <i>Default</i> factory which is used by the {@link Bastion#request(String, HttpRequest)} method. Subclasses will generally
 * implement the {@link BastionFactory#prepareBastion(BastionBuilderImpl)} to further configure the instance with any additional
 * features necessary.
 */
public abstract class BastionFactory {

    private static BastionFactory defaultBastionFactory = null;

    private boolean suppressAssertions = false;
    private Configuration configuration;

    /**
     * Gets the {@link BastionFactory} which is designated as the "Default" factory. This factory is the one used
     * when a user calls the {@link Bastion#request(String, HttpRequest)} method.
     *
     * @return A non-null factory instance which is considered to be the "Default."
     */
    public static synchronized BastionFactory getDefaultBastionFactory() {
        if (defaultBastionFactory == null) {
            setDefaultBastionFactory(new DefaultBastionFactory());
        }
        return defaultBastionFactory;
    }

    /**
     * Designate a new factory instance as the "Default". This changes which factory is used when users call the
     * {@link Bastion#request(String, HttpRequest)} method allowing external systems to modify Bastion functionality. For example,
     * a test library could potentially register a new factory which will register event listeners for when Bastion calls
     * start/end to display them in a UI.
     *
     * @param defaultBastionFactory The factory instance to designate as "Default". Cannot be {@literal null}.
     */
    public static void setDefaultBastionFactory(BastionFactory defaultBastionFactory) {
        requireNonNull(defaultBastionFactory, "The default Bastion factory cannot be null");
        BastionFactory.defaultBastionFactory = defaultBastionFactory;
    }

    public static Configuration loadConfiguration(String resourceLocation) {
        Configuration config = new BastionConfigurationLoader(resourceLocation).load();
        BastionFactory bastionFactory = getDefaultBastionFactory();
        bastionFactory.setConfiguration(config);
        return bastionFactory.getConfiguration();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        requireNonNull(configuration, "Configuration should not be null.");
        this.configuration = configuration;
    }

    /**
     * Construct and initialise a new instance of the {@link BastionBuilderImpl} builder. By default, the returned builder
     * will bind the response to a plain {@linkplain Object} model. Also, the returned builder will use the specified
     * {@code message} (for informational purposes) and {@code request}.
     *
     * @param message A non-{@literal null} String which describes the request/test that Bastion will be performing.
     *                This message will typically be used and displayed on a UI or test reports for informational
     *                purposes.
     * @param request A non-{@literal null} instance of a {@linkplain HttpRequest} which will be performed by Bastion.
     * @return A fully configured instance of the {@link BastionBuilderImpl} fluent builder which can be used directly by
     * the user to construct Bastion tests.
     */
    public BastionBuilder<Object> getBastion(String message, HttpRequest request) {
        BastionBuilderImpl<Object> bastion = new BastionBuilderImpl<>(message, request);
        bastion.setSuppressAssertions(suppressAssertions);
        bastion.setConfiguration(getConfiguration());
        prepareBastion(bastion);
        return bastion;
    }

    /**
     * Configures whether {@link BastionBuilderImpl} objects returned by this factory should be configured to suppress assertions or
     * not. When set to suppress assertions, Bastion will execute the HTTP request as normal but will skip executing any assertions
     * provided to the {@link BastionBuilderImpl#withAssertions(Assertions)} method.
     *
     * @param suppressAssertions {@literal true} to suppress assertions; {@literal false}, otherwise.
     */
    public void suppressAssertions(boolean suppressAssertions) {
        this.suppressAssertions = suppressAssertions;
    }

    /**
     * Configures the specified instance of the {@link BastionBuilderImpl} builder. Factory subclasses must override this method to
     * configure the builder for use with external systems/libraries. An implementation will typically register event listeners
     * or model converters to be used by Bastion.
     *
     * @param bastion The builder instance to configure.
     */
    protected abstract void prepareBastion(BastionBuilderImpl<?> bastion);
}
