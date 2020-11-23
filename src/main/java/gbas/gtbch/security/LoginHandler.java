package gbas.gtbch.security;

import gbas.gtbch.sapod.model.User;
import gbas.gtbch.sapod.service.UserService;
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
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class LoginHandler implements AuthenticationSuccessHandler, AccessDeniedHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            logger.info("{} logged in", user.toString());

            user.setLoggedInDate(new Date());
            logger.debug("set logged in date to {} for user {}", UtilDate8.getStringFullDate(user.getLoggedInDate()), user.getUsername());
            userService.updateLoggedInDate(user);
        }

        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");
    }

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            logger.info(auth.getName()
                    + " was trying to access protected resource: "
                    + httpServletRequest.getRequestURI());
        }

        httpServletResponse.sendRedirect("/access-denied");
    }
}
