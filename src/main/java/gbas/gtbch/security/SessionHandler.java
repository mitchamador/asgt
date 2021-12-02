package gbas.gtbch.security;

import gbas.gtbch.sapod.model.User;
import gbas.gtbch.sapod.service.UserService;
import gbas.gtbch.security.jwt.JWTToken;
import gbas.gtbch.util.UtilDate8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class SessionHandler implements AuthenticationSuccessHandler, LogoutSuccessHandler, AccessDeniedHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final UserService userService;

    private final JWTToken jwtToken;

    public SessionHandler(UserService userService, JWTToken jwtToken) {
        this.userService = userService;
        this.jwtToken = jwtToken;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        fillUserAuthInfo(authentication);
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof User) {
            logoutUser((User) authentication.getPrincipal());
        }
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");
    }

    public void fillUserAuthInfo(Authentication auth) {
        if (auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            logger.info("{} logged in", user.toString());

            user.setLoggedInDate(new Date());
            logger.debug("set logged in date to {} for user {}", UtilDate8.getStringFullDate(user.getLoggedInDate()), user.getUsername());
            userService.updateLoggedInDate(user);

            user.setToken(jwtToken.createToken(user));
        }
    }

    public void logoutUser(User user) {
        logger.info("{} logged out", user.toString());

        jwtToken.blacklist(user.getToken());
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
