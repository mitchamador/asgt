package gbas.gtbch.web;

import gbas.gtbch.sapod.model.users.User;
import gbas.gtbch.security.SessionHandler;
import gbas.gtbch.security.jwt.JWTToken;
import gbas.gtbch.web.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private final AuthenticationManager authenticationManager;
    private final SessionRegistry sessionRegistry;
    private final SessionHandler sessionHandler;
    private final JWTToken jwtToken;

    public SessionController(@Qualifier("authenticationManager") AuthenticationManager authenticationManager, SessionRegistry sessionRegistry, SessionHandler sessionHandler, JWTToken jwtToken) {
        this.authenticationManager = authenticationManager;
        this.sessionRegistry = sessionRegistry;
        this.sessionHandler = sessionHandler;
        this.jwtToken = jwtToken;
    }

    /**
     * api authentication
     *
     * @param session
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<Response> login(HttpSession session,
                                              @RequestParam(value = "username", required = false) String userName,
                                              @RequestParam(value = "password", required = false) String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userName,
                            password
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication != null) {
                // add session to sessionregistry
                sessionRegistry.removeSessionInformation(session.getId());
                sessionRegistry.registerNewSession(session.getId(), authentication.getPrincipal());

                sessionHandler.fillUserAuthInfo(authentication);

                String token = authentication.getPrincipal() instanceof User ? ((User) authentication.getPrincipal()).getToken() : "";

                return ResponseEntity.ok().header(jwtToken.getHeaderString(), token).body(new Response(String.format("User %s logged in", userName)));
            }

        } catch (BadCredentialsException e) {
            logger.debug("login failed for user {}", userName);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("Login failed"));
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="logout", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Response> logout(HttpServletRequest request, HttpServletResponse response) {
        // Get the Spring Authentication object of the current request.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // In case you are not filtering the users of this request url.
        if (authentication != null && authentication.getAuthorities().stream().noneMatch(r -> r.getAuthority().equals("ROLE_ANONYMOUS"))) {

            // perform logout
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            if (authentication.getPrincipal() instanceof User) {
                sessionHandler.logoutUser((User) authentication.getPrincipal());
            } else {
                logger.info(String.format("%s logged out", authentication.getPrincipal().toString()));
            }

            // remove user's sessions
            for (SessionInformation sessionInformation : sessionRegistry.getAllSessions(authentication.getPrincipal(), true)) {
                sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
            }

            return ResponseEntity.ok(new Response(String.format("User %s logged out", authentication.getPrincipal() instanceof User ? ((User) authentication.getPrincipal()).getUsername().trim() : authentication.getPrincipal().toString())));
        }
        return ResponseEntity.badRequest().body(new Response("Logout failed"));
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public ResponseEntity getUser(HttpServletResponse httpServletResponse) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User) {
            return ResponseEntity.ok((User) authentication.getPrincipal());
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
        return null;
    }

}