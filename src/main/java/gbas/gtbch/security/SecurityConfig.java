package gbas.gtbch.security;

import gbas.gtbch.sapod.service.UserService;
import gbas.gtbch.security.jwt.JWTToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@DependsOn("waitSapodDataSource")
@Order(2)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userService;
    private final ApiAccess apiAccess;
    private final JWTToken jwtToken;

    public SecurityConfig(UserDetailsService userService, ApiAccess apiAccess, JWTToken jwtToken) {
        this.userService = userService;
        this.apiAccess = apiAccess;
        this.jwtToken = jwtToken;
    }

    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        PasswordEncoder defaultEncoder = new SapodPasswordEncoder();
        Map<String, PasswordEncoder> encoderMap = new HashMap<>();
        encoderMap.put("sapod", defaultEncoder);
        encoderMap.put("bcrypt", new BCryptPasswordEncoder());

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("sapod", encoderMap);
        passwordEncoder.setDefaultPasswordEncoderForMatches(defaultEncoder);

        return passwordEncoder;
    }

    @Bean
    public UserDetailsService inMemoryUserDetailsService() {
        return new CustomInMemoryUserDetailsService(
                new UserDetailsToken("guest", "{bcrypt}$2a$10$advrZHrffq9ocKbjrYvISeYAiAbd/n08AHVSag0ghjqWPUM4x6AAK", new String[] {"NONE"}),
                new UserDetailsToken("su", "{sapod}4Q@'^\\<AE=+/*(R<J2B:0K)MUP$!", new String[] {"ADMIN", "USER"})
        );
    }

    @Bean
    public DaoAuthenticationProvider inMemoryDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(inMemoryUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(delegatingPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(inMemoryDaoAuthenticationProvider())
                .userDetailsService(userService).passwordEncoder(delegatingPasswordEncoder());
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
                            .successHandler(sessionHandler())
                    .and()
                        .logout()
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessUrl("/login?logout")
                            .logoutSuccessHandler(sessionHandler())
                            .permitAll()
                    .and()
                        .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                            httpSecurityExceptionHandlingConfigurer
                                    .accessDeniedHandler(sessionHandler());
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
                            //.expiredUrl("/login?expired")
                            .sessionRegistry(sessionRegistry())
                            .maxSessionsPreventsLogin(false)
                            .expiredSessionStrategy(sessionInformationExpiredStrategy())
            ;
    }

    @Bean
    public SessionHandler sessionHandler() {
        return new SessionHandler((UserService) userService, jwtToken, apiAccess, sessionRegistry());
    }

    private final static String EXPIRED_URL = "/login?expired";

    @Bean
    public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
        return new CustomSimpleRedirectSessionInformationExpiredStrategy(apiAccess, EXPIRED_URL);
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

    @Bean
    @Override
    @Qualifier("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }
}


