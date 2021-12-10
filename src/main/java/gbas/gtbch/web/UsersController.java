package gbas.gtbch.web;

import gbas.gtbch.sapod.model.users.Role;
import gbas.gtbch.sapod.model.users.User;
import gbas.gtbch.sapod.model.users.UserItem;
import gbas.gtbch.sapod.model.users.UserRole;
import gbas.gtbch.sapod.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
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
        return new ModelAndView("admin/users");
    }

    @GetMapping("/admin/user/{id:[\\d]+}/editor")
    public ModelAndView userEditor(@PathVariable int id) {
        ModelAndView userEditor = new ModelAndView("fragments/users :: editor");
        User u = id == 0 ? new User() : userService.findUserById(id);
        List<Role> roles = UserRole.getAllRoles();
        for (Role r : roles) {
            for (Role uR : u.getRoles()) {
                if (uR.getMnemo().equals(r.getMnemo())) {
                    r.setChecked(true);
                    break;
                }
            }
        }
        userEditor.addObject("user", u);
        userEditor.addObject("roles", roles);
        return userEditor;
    }

    /**
     * get all users
     * @return
     */
    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserItem>> getUsers(@RequestParam(required = false) boolean active) {
        return new ResponseEntity<>(getUserItemList(active ? getUsersFromSessionRegistry() : userService.findAll()), HttpStatus.OK);
    }

    private List<UserItem> getUserItemList(List<User> users) {
        if (users != null) {
            return users.stream().map(UserItem::new).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * get user by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/user/{id:[\\d]+}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable int id) {
        User u = userService.findUserById(id);
        if (u == null) {
            u = new User();
        } else {
            for (Role uRole : u.getRoles()) {
                uRole.setChecked(true);
            }
        }
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    /**
     * create user
     * @param user
     * @return
     */
    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    public ResponseEntity<Integer> createUser(@RequestBody User user) {
        user = userService.saveUser(user);
        return user != null ? ResponseEntity.created(URI.create("/api/user/" + user.getId())).body(user.getId()) : ResponseEntity.notFound().build();
    }

    /**
     * save user
     * @param user
     * @return
     */
    @RequestMapping(value = "/api/user/{id:[\\d]+}", method = RequestMethod.PUT)
    public ResponseEntity<Integer> saveUser(@PathVariable int id, @RequestBody User user) {
        user.setId(id);
        return userService.saveUser(user) != null ? ResponseEntity.ok().body(user.getId()) : ResponseEntity.notFound().build();
    }

    /**
     * delete user by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/user/{id:[\\d]+}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/user/roles", method = RequestMethod.GET)
    public List<Role> getUserRoles() {
        return UserRole.getAllRoles();
    }

}
