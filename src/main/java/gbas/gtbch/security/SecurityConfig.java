package gbas.gtbch.security;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;


@EnableWebSecurity
@DependsOn("checkSapodDataSource")
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
        auth.inMemoryAuthentication().withUser("guest").password("{noop}").roles("GUEST");
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
                            "/api/websapod/**",
                            "/login**")
                            .permitAll()
                    .antMatchers("/admin/**", "/api/**").hasAnyRole("ADMIN")
                    .antMatchers("/user/**", "/api/**").hasAnyRole("ADMIN", "USER")
                    //.antMatchers("/api/websapod/**", "/guest/**").hasAnyRole("GUEST")
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


    @Override
    public void configure(WebSecurity web) throws Exception {
//        web
//                .ignoring()
//                .antMatchers("/webjars/**", "/css/**", "/js/**", "/img/**", "/api/**", "/login");
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}