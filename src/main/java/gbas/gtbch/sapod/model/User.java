package gbas.gtbch.sapod.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Cacheable(false)
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Column(name = "last_time")
    private Date loggedInDate;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<Role>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
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

    /**
     * Наименование ролей пользователя
     * @return
     */
    public String getAuthoritiesString() {
        return getAuthorities().stream().map(o -> {
            switch (o.getAuthority()) {
                case "ROLE_ADMIN":
                    return "Администратор";
                case "ROLE_USER":
                    return "Пользователь";
            }
            return "Не определена";
        }).sorted().collect(Collectors.joining(";"));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    public Date getLoggedInDate() {
        return loggedInDate;
    }

    public void setLoggedInDate(Date loggedInDate) {
        this.loggedInDate = loggedInDate;
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
        return "User [ username: " + getUsername().trim() + ", position: " + getPosition().trim() + ", fio: " + getFio().trim() + " ]";
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