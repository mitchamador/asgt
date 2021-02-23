package gbas.gtbch.sapod.model;

import java.util.ArrayList;
import java.util.List;

public enum UserRole {
    ROLE_ADMIN("Администратор"),
    ROLE_USER("Пользователь"),
    ROLE_NONE("Не определена"),
    ;

    public String getRoleName() {
        return roleName;
    }

    private String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public static String getRoleName(String roleMnemo) {
        try {
            return valueOf(roleMnemo).getRoleName();
        } catch (IllegalArgumentException e) {
            return valueOf(ROLE_NONE.name()).getRoleName();
        }
    }

    public static List<Role> getAllRoles() {
        List<Role> userRoles = new ArrayList<>();
        for (UserRole userRole : values()) {
            userRoles.add(new Role(userRole.name(), userRole.getRoleName()));
        }
        return userRoles;
    }
}
