package gbas.gtbch.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import gbas.gtbch.sapod.model.users.User;
import gbas.gtbch.sapod.service.UserService;
import gbas.gtbch.security.ApiAccess;
import gbas.gtbch.security.UserDetailsToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt authorization filter
 */
@ConditionalOnExpression("'${app.api.security:}'.equals('jwt')")
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserService userService;

    private final JWTToken jwtToken;

    private final SessionRegistry sessionRegistry;

    private final ApiAccess apiAccess;

    public JWTAuthorizationFilter(JWTToken jwtToken, UserService userService, SessionRegistry sessionRegistry, ApiAccess apiAccess, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.jwtToken = jwtToken;
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
        this.apiAccess = apiAccess;
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() {
        return this;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        Authentication authentication = null;

        try {
            // check auth by header
            if ((authentication = getAuthentication(request.getHeader(jwtToken.getHeaderString()))) == null) {
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // update session's last request
            sessionRegistry.refreshLastRequest(request.getSession().getId());
            //sessionRegistry.getAllSessions(authentication.getPrincipal(), false).forEach(sessionInformation -> sessionRegistry.refreshLastRequest(sessionInformation.getSessionId()));
        } finally {

            if (authentication != null || new OrRequestMatcher(apiAccess.getUnauthorizedApiMatchers()).matches(request)) {
                chain.doFilter(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            }
        }
    }

    // Reads the JWT from the Authorization header, and then uses JWT to validate the token
    private UsernamePasswordAuthenticationToken getAuthentication(String _token) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;

        if (_token != null) {
            if (!jwtToken.isBlacklisted(_token)) {
                // parse the token.
                try {
                    UserDetailsToken user = jwtToken.getUser(_token);

                    if (user != null && user.getUsername() != null) {
                        User _user = userService.findUserByLogin(user.getUsername());
                        if (_user != null) {
                            user = _user;
                        }
                        user.setToken(_token);
                        usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    } else {
                        logger.info("user from token is null: {}", _token);
                    }
                } catch (TokenExpiredException e) {
                    logger.info("token expired: {}", _token);
                } catch (JWTVerificationException e) {
                    logger.info("token verification failed: {}", _token);
                }
            }
        }

        return usernamePasswordAuthenticationToken;
    }
}