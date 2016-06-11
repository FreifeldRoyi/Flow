package flow.plumber.injections;

import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * @author royif
 * @since 07/05/16
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Qualifier
public @interface NamedFlowElement
{
	String elementName() default "";

	boolean indexed() default true;
}
