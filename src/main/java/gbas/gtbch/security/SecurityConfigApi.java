package gbas.gtbch.security;

import gbas.gtbch.security.jwt.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.concurrent.TimeUnit;

@Configuration
@Order(1)
@DependsOn("waitSapodDataSource")
@ConditionalOnExpression("'${app.api.security:}'.equals('jwt')")
public class SecurityConfigApi extends WebSecurityConfigurerAdapter {

    /**
     * {@link ApiAccess}
     */
    private final ApiAccess apiAccess;

    /**
     * application context
     */
    private final ApplicationContext context;

    public SecurityConfigApi(ApiAccess apiAccess, ApplicationContext context) {
        this.apiAccess = apiAccess;
        this.context = context;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.antMatcher("/api/**")
                .authorizeRequests()
                    .requestMatchers(apiAccess.getUnauthorizedApiMatchers())
                            .permitAll()
                    .antMatchers("/api/**").authenticated()
                // remember me
                .and()
                    .rememberMe().tokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(72))
                .and()
                    .csrf().disable()
                .addFilter(context.getBean("jwtAuthorizationFilter", JWTAuthorizationFilter.class))
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }

    @Bean
    @Qualifier("apiAuthenticationManager")
    public AuthenticationManager apiAuthenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}


