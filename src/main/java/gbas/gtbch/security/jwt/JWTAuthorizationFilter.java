package gbas.gtbch.security.jwt;

import gbas.gtbch.sapod.model.users.User;
import gbas.gtbch.sapod.service.UserService;
import gbas.gtbch.security.ApiAccess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
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
            String header = request.getHeader(jwtToken.getHeaderString());

            if (header == null || !header.startsWith(jwtToken.getTokenPrefix())) {
                return;
            }

            if ((authentication = getAuthentication(request)) == null) {
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // update sessions
            for (SessionInformation sessionInformation : sessionRegistry.getAllSessions(authentication.getPrincipal(), false)) {
                sessionRegistry.refreshLastRequest(sessionInformation.getSessionId());
            }
        } finally {

            if (authentication != null || new OrRequestMatcher(apiAccess.getUnauthorizedApiMatchers()).matches(request)) {
                chain.doFilter(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            }
        }
    }

    // Reads the JWT from the Authorization header, and then uses JWT to validate the token
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;

        String _token = request.getHeader(jwtToken.getHeaderString());

        if (_token != null) {
            // parse the token.
            String token = jwtToken.getToken(_token);

            if (token != null) {
                if (!jwtToken.isBlacklisted(_token)) {
                    User user = userService.findUserByLogin(token);
                    if (user != null) {
                        user.setToken(_token);
                        usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    }
                }
            }
        }

        return usernamePasswordAuthenticationToken;
    }
}