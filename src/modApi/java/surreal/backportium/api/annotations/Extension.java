package surreal.backportium.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the annotated class, most likely interface, is implemented by the given class. This means objects which are types of that class can be cast to this interface.
 * You can implement this in your own class to easily override the methods.
 **/
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Extension {
    /**
     * Classes this interface extends.
     **/
    Class<?>[] value();
}
