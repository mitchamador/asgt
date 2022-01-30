package gbas.gtbch.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gbas.gtbch.sapod.model.users.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"enabled", "password", "authorities", "accountNonExpired", "credentialsNonExpired", "accountNonLocked", "token"})
public class UserDetailsToken implements UserDetails {

    private List<GrantedAuthority> authorities;

    public UserDetailsToken() {
    }

    public UserDetailsToken(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDetailsToken(String username, String password, String[] roles) {
        this.username = username;
        this.password = password;
        setRoles(roles);
    }

    private String username;

    private String password;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRoles(String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>(
                roles.length);
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority((role.startsWith("ROLE_") ? "" : "ROLE_") + role));
        }
        this.authorities = new ArrayList<>(authorities);
    }

    /**
     * Наименование ролей пользователя
     * @return
     */
    public String getAuthoritiesString() {
        return getAuthorities().stream()
                .map(o -> UserRole.getRoleName(o.getAuthority()))
                .sorted().collect(Collectors.joining(";"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User [ " +
                "username: " + getUsername().trim() +
                " ]";
    }
}
