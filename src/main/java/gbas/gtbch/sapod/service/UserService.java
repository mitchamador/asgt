package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.users.User;

import java.util.List;

public interface UserService {
    /**
     *
     * @param login
     * @return
     */
    User findUserByLogin(String login);

    /**
     *
     * @param id
     * @return
     */
    User findUserById(Integer id);

    /**
     *
     * @return
     */
    List<User> findAll();

    /**
     *
     * @param u
     */
    void updateLoggedInDate(User u);

    /**
     *
     * @param id
     */
    void deleteUserById(Integer id);

    /**
     *
     * @param r
     * @return
     */
    User saveUser(User r);
}