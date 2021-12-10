package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.users.User;
import gbas.gtbch.sapod.repository.UserRepository;
import gbas.gtbch.security.SapodPasswordEncoder;
import gbas.tvk.nsi.cash.Func;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * сервис для получения объектов {@link User} из БД
 * имплементирует {@link UserDetailsService} для расширения Spring Security полями объекта {@link User}
 *
 */
@Service("userService")
@Transactional(transactionManager = "sapodTransactionManager")
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    /**
     * поиск в БД пользователя {@link User} по логину
     * @param login
     * @return
     */
    @Override
    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    /**
     * поиск в БД пользователя {@link User} по id
     * @param id
     * @return
     */
    @Override
    public User findUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * чтение всех пользователей из БД
     * @return
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     *
     * @param u
     */
    @Override
    public void updateLoggedInDate(User u) {
        userRepository.updateLoggedInDate(u.getLoggedInDate(), u.getId());
    }

    /**
     * поиск имени по username (для расширения Spring Security)
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = findUserByLogin(username);
        if (u == null) {
            logger.info("User [ {} ] not found", username);
            throw new UsernameNotFoundException("User not found");
        }
        return u;
    }


    /**
     * delete user by id
     * @param id
     */
    @Override
    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    /**
     * save user
     * @param u
     * @return
     */
    @Override
    public User saveUser(User u) {
        // check login duplicate
        User checkLoginUser = userRepository.findByLogin(u.getLogin());
        if (checkLoginUser != null && checkLoginUser.getId() != u.getId()) {
            return null;
        }
        if (u.getId() != 0) {
            // delete all roles
            userRepository.deleteUserRoles(u.getId());
        }
        if (Func.isEmpty(u.getPassword()) && u.getId() != 0) {
            // reload user's password
            userRepository.findById(u.getId()).ifPresent(oldUser -> u.setPassword(oldUser.getPassword()));
        } else {
            // encode password (assume password is unencoded)
            u.setPassword(new SapodPasswordEncoder().encode(u.getPassword()));
        }
        return userRepository.save(u);
    }
}
