package gbas.gtbch.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     *
     */
    private final UserDetailsService userService;

    /**
     *
     */
    private final LoginHandler loginHandler;

    public SecurityConfig(UserDetailsService userService, LoginHandler loginHandler) {
        this.userService = userService;
        this.loginHandler = loginHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new SapodPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(
                            "/",
                            "/js/**",
                            "/css/**",
                            "/img/**",
                            "/webjars/**",
                            "/favicon.ico",
                            "/login")
                            .permitAll()
                    .antMatchers("/admin/**", "/api/**").hasAnyRole("ADMIN")
                    .antMatchers("/user/**", "/api/**").hasAnyRole("ADMIN", "USER")
                    .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .successHandler(loginHandler)
                .and()
                .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                .and()
                .exceptionHandling()
                    .accessDeniedHandler(loginHandler)
                // remember me
                .and()
                .rememberMe().tokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(72))
                .and()
                .csrf().disable()
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login?expired")
                .sessionRegistry(sessionRegistry())
                .maxSessionsPreventsLogin(false);
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

/*
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/webjars/**", "/css/**", "/js/**", "/img/**", "/api/**");
    }
*/
}