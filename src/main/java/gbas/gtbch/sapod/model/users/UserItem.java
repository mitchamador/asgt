package gbas.gtbch.sapod.model.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"roles", "authorities"})
public class UserItem {
    private int id;

    private String login;

    private String fio;

    private String position;

    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private Date loggedInDate;

    private Set<Role> roles = new HashSet<>();

    private Collection<? extends GrantedAuthority> authorities;

    public UserItem(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.fio = user.getFio();
        this.position = user.getPosition();
        this.loggedInDate = user.getLoggedInDate();
        this.roles = user.getRoles();
        this.authorities = user.getAuthorities();
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getFio() {
        return fio;
    }

    public String getPosition() {
        return position;
    }

    public Date getLoggedInDate() {
        return loggedInDate;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Наименование ролей пользователя
     * @return
     */
    public String getAuthoritiesString() {
        if (authorities != null) {
            return authorities.stream()
                    .map(o -> UserRole.getRoleName(o.getAuthority()))
                    .sorted().collect(Collectors.joining(";"));
        }
        return "";
    }

    @Override
    public String toString() {
        return "UserItem {" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", fio='" + fio + '\'' +
                ", position='" + position + '\'' +
                ", loggedInDate=" + loggedInDate +
                '}';
    }
}
