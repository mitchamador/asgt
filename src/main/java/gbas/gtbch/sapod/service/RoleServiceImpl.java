package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.Role;
import gbas.gtbch.sapod.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role findRoleByMnemo(String mnemo) {
        return roleRepository.findByMnemo(mnemo);
    }
}
