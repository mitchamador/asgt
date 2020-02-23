package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.Role;

import java.util.List;

public interface RoleService {

    List<Role> getRoles();

    Role findRoleByMnemo(String mnemo);
}