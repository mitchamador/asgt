package gbas.gtbch.web;

import gbas.gtbch.sapod.model.User;
import gbas.gtbch.sapod.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UsersController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final UserService userService;

    /**
     *
     */
    private final SessionRegistry sessionRegistry;

    public UsersController(UserService userService, SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
    }

    private List<User> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(user -> !sessionRegistry.getAllSessions(user, false).isEmpty())
                .map(user -> (User) user)
                .collect(Collectors.toList());
    }

    @GetMapping("/admin/users")
    public ModelAndView users() {
        ModelAndView users = new ModelAndView("admin/users");
        users.addObject("activeUsers", getUsersFromSessionRegistry());
        users.addObject("allUsers", userService.findAll());
        return users;
    }

    /**
     * get all users
     * @return
     */
    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    /**
     * get user by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    /**
     * save user
     * @param user
     * @return
     */
    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.OK);
    }

    /**
     * delete user by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/users/{id}/delete", method = RequestMethod.GET)
    public ResponseEntity deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
