package gbas.gtbch.jobs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ServerJob} annotation for running job at application's startup
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
