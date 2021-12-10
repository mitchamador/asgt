package gbas.gtbch.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import gbas.gtbch.sapod.model.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

@Component
public class JWTToken {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SECRET = "SECRET_KEY";

    private static final long EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(120);

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String HEADER_STRING = "Authorization";

    private JWTToken() {
    }

    @Bean
    public JWTToken jwtToken() {
        return new JWTToken();
    }

    public String createToken(User user) {
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(getSecret().getBytes()));

        cleanupBlacklist();

        return TOKEN_PREFIX + token;
    }

    private String secret = null;

    private String getSecret() {
        if (secret == null) {
            secret = UUID.randomUUID().toString();
        }
        return secret;
    }

    public String getToken(String token) {
        String decodedToken = null;
        try {
            decodedToken = JWT.require(Algorithm.HMAC512(getSecret().getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();
        } catch (JWTVerificationException e) {
            logger.info("token verification failed: {}", token);
        } catch (IllegalArgumentException ignored) {
        }
        return decodedToken;
    }

    private ConcurrentSkipListSet<String> blacklistedTokens = new ConcurrentSkipListSet<>();

    private void cleanupBlacklist() {
        blacklistedTokens.removeIf(s -> getToken(s) == null);
    }

    public void blacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public String getHeaderString() {
        return HEADER_STRING;
    }

    public String getTokenPrefix() {
        return TOKEN_PREFIX;
    }
}
