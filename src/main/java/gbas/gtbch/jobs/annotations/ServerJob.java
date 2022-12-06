package gbas.gtbch.jobs.annotations;

import gbas.gtbch.jobs.AbstractServerJob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link AbstractServerJob} annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerJob {
    String alias() default "";
    String name() default "";
}
