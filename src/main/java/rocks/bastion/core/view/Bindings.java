package rocks.bastion.core.view;

import org.apache.commons.lang3.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a map of view bindings. Given a view type, will return the associated view with that binding.
 */
public final class Bindings {

    public static <T> Bindings single(Class<? super T> viewType, T view) {
        Bindings bindings = new Bindings();
        bindings.addBinding(viewType, view);
        return bindings;
    }

    /**
     * Creates a Bindings object containing the entire class hierarchy of the given view type. All extended classes and interfaces
     * implemented by the given type will be bound to the specified view.
     *
     * @param viewType The class representing the type of view to bind
     * @param view The view object to bind
     * @param <T> The type of view to bind
     * @return A bindings object
     */
    @SuppressWarnings("unchecked")
    public static <T> Bindings hierarchy(Class<? super T> viewType, T view) {
        Bindings bindings = new Bindings();
        for (Class<?> superType : ClassUtils.hierarchy(viewType, ClassUtils.Interfaces.INCLUDE)) {
            bindings.addBinding((Class<? super T>) superType, view);
        }
        return bindings;
    }

    private final Map<Class<?>, Object> bindings;

    public Bindings() {
        bindings = new HashMap<>();
    }

    public <T> void addBinding(Class<? super T> viewType, T view) {
        Objects.requireNonNull(viewType);
        Objects.requireNonNull(view);
        bindings.put(viewType, view);
    }

    public void addAllBindings(Bindings bindings) {
        Objects.requireNonNull(bindings);
        this.bindings.putAll(bindings.bindings);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getViewForType(Class<T> viewType) {
        return Optional.ofNullable((T) bindings.get(viewType));
    }
}
