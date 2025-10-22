package surreal.backportium.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The method implementation is final, you can't override the method
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Final {
}
