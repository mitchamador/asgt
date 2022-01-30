package gbas.gtbch.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * [very] simple implementation of {@code UserDetailsService} which is backed by an
 * in-memory map.
 */
public class CustomInMemoryUserDetailsService implements UserDetailsService {

    private Map<String, UserDetails> users = new HashMap<>();

    public CustomInMemoryUserDetailsService(UserDetails... userDetails) {
        for (UserDetails userDetail : userDetails) {
            users.put(userDetail.getUsername().toLowerCase(), userDetail);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username.toLowerCase());

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return user;
    }
}
