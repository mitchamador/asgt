package gbas.gtbch.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    /**
     * create token for {@link User}
     * @param user
     * @return
     */
    public String createToken(User user) {
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(getSecret().getBytes()));

        return TOKEN_PREFIX + token;
    }

    private String secret = null;

    private String getSecret() {
        if (secret == null) {
            secret = UUID.randomUUID().toString();
        }
        return secret;
    }

    /**
     * get {@link DecodedJWT} from token
     * @param token
     * @return
     * @throws JWTVerificationException
     */
    private DecodedJWT getDecodedJWT(String token) throws JWTVerificationException {
        return JWT.require(Algorithm.HMAC512(getSecret().getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""));
    }

    /**
     * get username from token
     * @param token
     * @return
     */
    public String getUsername(String token) throws JWTVerificationException {
        String decodedToken = null;

        DecodedJWT decodedJWT = getDecodedJWT(token);
        if (decodedJWT != null) {
            decodedToken = decodedJWT.getSubject();
        }

        return decodedToken;
    }

    private ConcurrentSkipListSet<String> blacklistedTokens = new ConcurrentSkipListSet<>();

    /**
     * blacklist token for logged out user and remove expired tokens from blacklist
     * @param token
     */
    public void blacklist(String token) {
        // add token to blacklist
        blacklistedTokens.add(token);
    }

    /**
     * check if token is in blacklist
     * @param token
     * @return
     */
    public boolean isBlacklisted(String token) {
        // remove expired tokens
        blacklistedTokens.removeIf(s -> {
            try {
                return getDecodedJWT(s) == null;
            } catch (JWTVerificationException e) {
                return true;
            }
        });
        // check if token is blacklisted
        return blacklistedTokens.contains(token);
    }

    /**
     * get header string
     * @return
     */
    public String getHeaderString() {
        return HEADER_STRING;
    }

    /**
     * get token prefix
     * @return
     */
    public String getTokenPrefix() {
        return TOKEN_PREFIX;
    }
}
