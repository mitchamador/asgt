package gbas.gtbch.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * api access authorization
 */
@Component
public class ApiAccess {

    private final static String ACCESS_COOKIES = "cookies";
    private final static String ACCESS_JWT = "jwt";

    @Value("${app.api.security}")
    private String apiSecurity;

    /**
     * check api access
     * @return true, if api access secured by cookies
     */
    public boolean byCookies() {
        return ACCESS_COOKIES.equals(apiSecurity);
    }

    /**
     * check api access
     * @return true, if api access secured by jwt
     */
    public boolean byJwt() {
        return ACCESS_JWT.equals(apiSecurity);
    }

    /**
     * check api access
     * @return true, if api access is unsecured
     */
    public boolean unsecure() {
        return !ACCESS_COOKIES.equals(apiSecurity) && !ACCESS_JWT.equals(apiSecurity);
    }

    private RequestMatcher[] matchers = null;

    /**
     * get list of unauthorized api matchers
     * @return
     */
    public RequestMatcher[] getUnauthorizedApiMatchers() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        if (matchers == null) {
            requestMatchers.add(new AntPathRequestMatcher("/api/session/login"));
            requestMatchers.add(new AntPathRequestMatcher("/api/calc**"));
            requestMatchers.add(new AntPathRequestMatcher("/api/websapod/**"));
            matchers = requestMatchers.toArray(new RequestMatcher[0]);
        }
        return matchers;
    }
}
