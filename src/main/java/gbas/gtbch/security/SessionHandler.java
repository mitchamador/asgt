package gbas.gtbch.security;

import gbas.gtbch.sapod.model.users.User;
import gbas.gtbch.sapod.service.UserService;
import gbas.gtbch.security.jwt.JWTToken;
import gbas.gtbch.util.UtilDate8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class SessionHandler implements AuthenticationSuccessHandler, LogoutSuccessHandler, AccessDeniedHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final UserService userService;
    private final JWTToken jwtToken;
    private final ApiAccess apiAccess;
    private final SessionRegistry sessionRegistry;

    public SessionHandler(UserService userService, JWTToken jwtToken, ApiAccess apiAccess, SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.jwtToken = jwtToken;
        this.apiAccess = apiAccess;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        authenticateUser(authentication.getPrincipal(), false);
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof UserDetailsToken) {
            logoutUser((UserDetailsToken) authentication.getPrincipal());
        }
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");
    }

    public void authenticateUser(Object principal, boolean expireSession) {
        cleanupSessions(principal, expireSession);

        if (principal instanceof UserDetailsToken) {
            UserDetailsToken userDetailsToken = (UserDetailsToken) principal;
            logger.info("{} logged in", userDetailsToken.toString());

            if (userDetailsToken instanceof User) {
                User user = (User) userDetailsToken;
                user.setLoggedInDate(new Date());
                logger.debug("set logged in date to {} for user {}", UtilDate8.getStringFullDate(user.getLoggedInDate()), userDetailsToken.getUsername());
                userService.updateLoggedInDate(user);
            }

            userDetailsToken.setToken(jwtToken.createToken(userDetailsToken));
        }
    }

    /**
     * expire sessions and blacklist tokens for principal
     * @param principal
     * @param expireSession
     */
    public void cleanupSessions(Object principal, boolean expireSession) {
        if (!apiAccess.unsecure()) {
            // expire old sessions after successful authentication
            sessionRegistry.getAllSessions(principal, true).forEach(sessionInformation -> {
                if (expireSession && !sessionInformation.isExpired()) {
                    sessionInformation.expireNow();
                }
                if (sessionInformation.getPrincipal() instanceof UserDetailsToken) {
                    jwtToken.blacklist(((UserDetailsToken) sessionInformation.getPrincipal()).getToken());
                }
                // remove expired sessions after 30 min
                if (sessionInformation.isExpired() && Duration.between(sessionInformation.getLastRequest().toInstant(), Instant.now()).toMinutes() > 30) {
                    sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                }
            });
        }

    }

    public void logoutUser(UserDetailsToken user) {
        cleanupSessions(user, false);
        logger.info("{} logged out", user.toString());
    }

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            logger.info("{} was trying to access protected resource: {}", auth.getName() != null ? auth.getName().trim() : "unknown user", httpServletRequest.getRequestURI());
        }

        httpServletResponse.sendRedirect("/error/access-denied");
    }
}
