package gbas.gtbch.sapod.model.users;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import gbas.gtbch.security.UserDetailsToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Cacheable(false)
@JsonIgnoreProperties(value = {"enabled", "username", "authorities", "accountNonExpired", "credentialsNonExpired", "accountNonLocked", "token"})
public class User extends UserDetailsToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "login")
    private String login;

    @Column(name = "passwd")
    private String password;

    @Column(name = "fio")
    private String fio;

    @Column(name = "position")
    private String position;

    @Column(name = "last_time", updatable = false)
    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private Date loggedInDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Role> roles = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login != null ? login.trim() : null;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> authorities = new HashSet<>();

        Set<Role> roles = getRoles();

        if (roles != null) {
            for (Role r : roles) {
                authorities.add((GrantedAuthority) () -> (r.getMnemo().equals("SERVER_ADMIN") ? "ROLE_ADMIN" : (r.getMnemo().equals("USER") ? "ROLE_USER" : r.getMnemo())));
            }
        }

        return authorities;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return getLogin();
    }

    public Date getLoggedInDate() {
        return loggedInDate;
    }

    public void setLoggedInDate(Date loggedInDate) {
        this.loggedInDate = loggedInDate;
    }

    @Override
    public String toString() {
        return "User [ " +
                "username: " + getUsername().trim() +
                (getPosition().trim().isEmpty() ? "" : (", position: " + getPosition().trim())) +
                (getFio().trim().isEmpty() ? "" : (", fio: " + getFio().trim())) +
                " ]";
    }

    @Override
    public boolean equals(Object otherUser) {
        if (otherUser == null) return false;
        else if (!(otherUser instanceof UserDetails)) return false;
        else return (otherUser.hashCode() == hashCode());
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }

}