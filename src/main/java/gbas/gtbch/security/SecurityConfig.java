package gbas.gtbch.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Configuration
@DependsOn("waitSapodDataSource")
@Order(2)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     *
     */
    private final UserDetailsService userService;

    /**
     *
     */
    private final SessionHandler sessionHandler;

    @Autowired
    private ApiAccess apiAccess;

    public SecurityConfig(UserDetailsService userService, SessionHandler sessionHandler) {
        this.userService = userService;
        this.sessionHandler = sessionHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("guest").password("{noop}").roles("GUEST");
        auth.userDetailsService(userService).passwordEncoder(new SapodPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(expressionInterceptUrlRegistry -> {
                        expressionInterceptUrlRegistry
                                .antMatchers(
                                        "/",
                                        "/js/**",
                                        "/css/**",
                                        "/img/**",
                                        "/webjars/**",
                                        "/favicon.ico",
                                        "/login**",
                                        "/error/**")
                                .permitAll();

                        if (apiAccess.byCookies()) {
                            expressionInterceptUrlRegistry
                                    .requestMatchers(apiAccess.getUnauthorizedApiMatchers()).permitAll()
                                    .antMatchers("/api/**").authenticated();
                        } else if (apiAccess.unsecure()) {
                            expressionInterceptUrlRegistry
                                    .antMatchers("/api/**").permitAll();
                        } else if (apiAccess.byJwt()) {
                            expressionInterceptUrlRegistry
                                    .antMatchers("/api/**").denyAll();
                        }

                        expressionInterceptUrlRegistry
                                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER");


                        expressionInterceptUrlRegistry.anyRequest().denyAll();
                    })
                        .formLogin()
                            .loginPage("/login")
                            .permitAll()
                            .successHandler(sessionHandler)
                    .and()
                        .logout()
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessUrl("/login?logout")
                            .logoutSuccessHandler(sessionHandler)
                            .permitAll()
                    .and()
                        .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                            httpSecurityExceptionHandlingConfigurer
                                    .accessDeniedHandler(sessionHandler);
                            if (apiAccess.byCookies()) {
                                httpSecurityExceptionHandlingConfigurer
                                        .defaultAuthenticationEntryPointFor((httpServletRequest, httpServletResponse, e) -> httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"),
                                                //HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                                new AntPathRequestMatcher("/api/**"));
                            }

                        })
                        // remember me
                        .rememberMe().tokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(72))
                    .and()
                        .csrf().disable()
                        .sessionManagement()
                            .maximumSessions(1)
                            .expiredUrl("/login?expired")
                            .sessionRegistry(sessionRegistry())
                            .maxSessionsPreventsLogin(false)
            ;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * note, spring security session doesn't expires without defining this bean
     * @return
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    }

    @Bean
    @Override
    @Qualifier("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}


