package gbas.gtbch.jobs.annotations;

import gbas.gtbch.jobs.ServerJob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation for running {@link ServerJob} at application's startup
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
