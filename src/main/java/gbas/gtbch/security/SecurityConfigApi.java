package gbas.gtbch.security;

import gbas.gtbch.sapod.service.UserService;
import gbas.gtbch.security.jwt.JWTAuthorizationFilter;
import gbas.gtbch.security.jwt.JWTToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;

import javax.servlet.Filter;
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

    private final JWTToken jwtToken;

    private final SessionRegistry sessionRegistry;

    private final UserService userService;

    public SecurityConfigApi(ApiAccess apiAccess, JWTToken jwtToken, SessionRegistry sessionRegistry, UserService userService) {
        this.apiAccess = apiAccess;
        this.jwtToken = jwtToken;
        this.sessionRegistry = sessionRegistry;
        this.userService = userService;
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
                .addFilter(jwtAuthorizationFilter())
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }

    private Filter jwtAuthorizationFilter() throws Exception {
        return new JWTAuthorizationFilter(jwtToken, userService, sessionRegistry, apiAccess, authenticationManager());
    }

}


