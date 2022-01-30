package gbas.gtbch.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class for handling /api expired session (send 401 error instead of redirect to expired session's url)
 *
 * @see org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy
 */
public final class CustomSimpleRedirectSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
    private final Log logger = LogFactory.getLog(getClass());
    private final String destinationUrl;
    private final RedirectStrategy redirectStrategy;
    private final ApiAccess apiAccess;

    public CustomSimpleRedirectSessionInformationExpiredStrategy(ApiAccess apiAccess, String invalidSessionUrl) {
        this(apiAccess, invalidSessionUrl, new DefaultRedirectStrategy());
    }

    public CustomSimpleRedirectSessionInformationExpiredStrategy(ApiAccess apiAccess, String invalidSessionUrl, RedirectStrategy redirectStrategy) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl),
                "url must start with '/' or with 'http(s)'");
        this.destinationUrl=invalidSessionUrl;
        this.apiAccess=apiAccess;
        this.redirectStrategy=redirectStrategy;
    }

    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        if (!apiAccess.unsecure() && event.getRequest().getRequestURI().startsWith("/api")) {
            event.getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired");
        } else {
            logger.debug("Redirecting to '" + destinationUrl + "'");
            redirectStrategy.sendRedirect(event.getRequest(), event.getResponse(), destinationUrl);
        }
    }

}