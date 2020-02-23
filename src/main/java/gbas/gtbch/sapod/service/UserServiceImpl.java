package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.User;
import gbas.gtbch.sapod.repository.UserRepository;
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
@Transactional("sapodTransactionManager")
public class UserServiceImpl implements UserService, UserDetailsService {

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
        return findUserByLogin(username);
    }

}
