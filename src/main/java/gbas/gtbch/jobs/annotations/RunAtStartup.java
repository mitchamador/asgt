package gbas.gtbch.jobs.annotations;

import gbas.gtbch.jobs.AbstractServerJob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation for running {@link AbstractServerJob} at application's startup
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RunAtStartup {
    /**
     * forced run
     * @return
     */
    boolean force() default false;
}
