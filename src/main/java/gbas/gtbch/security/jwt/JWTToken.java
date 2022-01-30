package gbas.gtbch.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import gbas.gtbch.sapod.model.users.User;
import gbas.gtbch.security.UserDetailsToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JWTToken {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static long EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(120);

    private final static String TOKEN_PREFIX = "Bearer ";

    private final static String HEADER_STRING = "Authorization";

    private final static String CLAIM_AUTHORITIES = "authorities";

    private JWTToken() {
    }

    @Bean
    public JWTToken jwtToken() {
        return new JWTToken();
    }

    /**
     * create token for {@link User}
     * @param user
     * @return token
     */
    public String createToken(UserDetailsToken user) {
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withClaim(CLAIM_AUTHORITIES, new ArrayList<>(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())))
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
    public DecodedJWT getDecodedJWT(String token) throws JWTVerificationException {
        return JWT.require(Algorithm.HMAC512(getSecret().getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""));
    }

    /**
     * get expiration date from token
     * @param token
     * @return
     */
    public Date getExpirationDate(String token) {
        try {
            return JWT.decode(token).getExpiresAt();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * get user with authorities from token
     * @param token
     * @return
     * @throws JWTVerificationException
     */
    public UserDetailsToken getUser(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = getDecodedJWT(token);

        if (decodedJWT != null) {
            UserDetailsToken user = new UserDetailsToken(decodedJWT.getSubject(), null);
            List<String> roles = decodedJWT.getClaim(CLAIM_AUTHORITIES).asList(String.class);
            if (roles != null) {
                user.setRoles(roles.toArray(new String[0]));
            }
            return user;
        }

        return null;
    }

    private ConcurrentSkipListSet<String> blacklistedTokens = new ConcurrentSkipListSet<>();

    /**
     * blacklist token for logged out user
     * @param token
     */
    public void blacklist(String token) {
        // add token to blacklist
        if (token != null) {
            token = token.replaceAll(TOKEN_PREFIX, "");
            if (!blacklistedTokens.contains(token)) {
                logger.debug("blacklist token: {}", token);
                blacklistedTokens.add(token);
            }
        }
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
        if (token != null && blacklistedTokens.contains(token = token.replaceAll(TOKEN_PREFIX, ""))) {
            logger.debug("token is blacklisted: {}", token);
            return true;
        } else {
            return false;
        }
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
