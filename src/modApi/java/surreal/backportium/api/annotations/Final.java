package surreal.backportium.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method that you can't override because it's final
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Final {
}
