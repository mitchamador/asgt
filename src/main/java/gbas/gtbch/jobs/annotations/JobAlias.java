package gbas.gtbch.jobs.annotations;

import gbas.gtbch.jobs.ServerJob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ServerJob} alias
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JobAlias {
    String value() default "";
}
