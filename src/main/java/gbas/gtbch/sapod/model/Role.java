package gbas.gtbch.sapod.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users_role")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private int id;

    @Column(name = "mnemo")
    private String mnemo;

    @Transient
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_user", nullable=false)
    @JsonBackReference
    private User user;

    @Transient
    private boolean isChecked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMnemo() {
        return mnemo == null ? "ROLE_NONE" : mnemo;
    }

    public void setMnemo(String mnemo) {
        this.mnemo = mnemo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return UserRole.getRoleName(getMnemo());
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Role(String mnemo, String name) {
        this.mnemo = mnemo;
        this.name = name;
    }

    public Role() {
    }
}
