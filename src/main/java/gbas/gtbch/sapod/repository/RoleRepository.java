package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByMnemo(String mnemo);
}
