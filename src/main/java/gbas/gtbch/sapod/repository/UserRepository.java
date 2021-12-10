package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     *
     * @param login
     * @return
     */
    User findByLogin(String login);

    /**
     *
     * @param loggedInDate
     * @param id
     */
    @Modifying
    @Query("update User u set u.loggedInDate = :date where u.id = :id")
    void updateLoggedInDate(@Param("date") Date loggedInDate, @Param("id") int id);

    /**
     *
     * @param id
     */
    @Modifying
    @Query("delete from Role where user.id = :id")
    void deleteUserRoles(@Param("id") int id);
}