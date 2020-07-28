package gbas.gtbch.websapod;

import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.sapod.bridge.constants.json.Key;
import gbas.sapod.bridge.controllers.CalcService;
import gbas.sapod.bridge.controllers.RouteException;
import gbas.sapod.bridge.controllers.RouteService;
import gbas.sapod.bridge.utilities.JsonBuilder;
import gbas.tvk.desktop.Version;
import gbas.tvk.util.UtilDate;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import static gbas.gtbch.config.WebConfig.getGzippedResponseEntity;

// add Access-Control-Allow-Origin: * to response headers
@CrossOrigin(maxAge = 3600)
@RestController
public class WebSapodController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AuthenticationManager authenticationManager;

    /**
     * websapod authentication
     *
     * @param authentication
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping(value = "/api/websapod/login", method = RequestMethod.GET)
    public Object login(Authentication authentication,
                        @RequestParam(value = "user", required = false) String userName,
                        @RequestParam(value = "password", required = false) String password) {
        if (authentication == null && userName != null && userName.equals("guest")) {
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                userName,
                                password,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"))
                        )
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (authentication != null && authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_GUEST"))) {
                    logger.debug("guest log in");
                }

            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }

        if (authentication != null && authentication.getName().equals("guest")) {
            String userString = String.valueOf(((long) (Math.random() * 10000000000L)));

            //"{\"USER\":[{\"UUID\":\"48e00858-96b8-40df-9b14-484dcb681916\"},{\"LOGIN\":\"webuser_2036096585\"},{\"FIO\":\"User #2036096585\"},{\"ROLE\":\"[ï¿½ï¿½ï¿½ï¿½ï¿½]\"}]}";
            return new ResponseEntity<>(new JsonBuilder(Key.USER)
                    .add(Key.UUID,  UUID.randomUUID().toString())
                    .add(Key.LOGIN, "webUser_" + userString)
                    .add(Key.FIO, "User #" + userString)
                    .add(Key.ROLE, Arrays.toString(new String[] {"guest"}))
                    .build()
                    , HttpStatus.OK);
        }

        return new ModelAndView("index");
    }

    @RequestMapping(value = "/api/websapod/version", method = RequestMethod.GET)
    public ResponseEntity<String> version(HttpServletRequest request) {
        gbas.tvk.desktop.Version Version = new Version();

        String version[] = Version.getVersion();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", version[0]);
        jsonObject.put("date", version[1]);

        return new ResponseEntity<>(
                jsonObject.toJSONString(),
                HttpStatus.OK
        );
    }

    @Autowired
    @Qualifier("sapodDataSource")
    DataSource sapodDataSource;

    @RequestMapping(value = "/api/websapod/route", method = RequestMethod.GET)
    public ResponseEntity<String> route(HttpServletRequest request) {

        try (Connection c = sapodDataSource.getConnection()) {
            return new ResponseEntity<>(
                    JsonBuilder.getJsonObject(
                            new RouteService(request).calc(new ServicesImpl(c))
                    ).toJSONString(),
                    HttpStatus.OK);
        } catch (RouteException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/api/websapod/calc", method = RequestMethod.GET)
    public ResponseEntity<String> calc(HttpServletRequest request) {

        try (Connection c = sapodDataSource.getConnection()) {
            return new ResponseEntity<>(
                    JsonBuilder.getJsonLabelValueArray(
                            new CalcService(request).calc(new ServicesImpl(c)).getLabelValueMap()
                    ).toJSONString(),
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/api/websapod/nsi", method = RequestMethod.GET)
    public ResponseEntity nsi(HttpServletRequest request, @RequestParam(name = "method") String method) {

        try (Connection c = sapodDataSource.getConnection()) {

            List<List<Object>> data = new ServicesImpl(c).getNsi(method);

            String jsonString;
            if (data != null && data.size() == 1) {
                List<Object> item = data.get(0);
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 0; i < item.size(); i += 2) {
                    map.put(String.valueOf(item.get(i)), item.get(i + 1));
                }
                jsonString = JsonBuilder.getJsonObject(map).toJSONString();
            } else {
                jsonString = JsonBuilder.getJsonArray(data).toJSONString();
            }

            //return new ResponseEntity<>(jsonString, HttpStatus.OK);
            return getGzippedResponseEntity(request, jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Autowired
    TpImportDateService tpImportDateService;


    @RequestMapping(value = "/api/websapod/tpdate", method = RequestMethod.GET)
    public ResponseEntity<String> route() {
        TpImportDate tpImportDate = tpImportDateService.getTpImportDate();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("computed", UtilDate.getStringDate(tpImportDate.getDateCreate(), "dd/MM/yy"));

        return new ResponseEntity<>(
                JsonBuilder.getJsonObject(
                        map
                ).toJSONString(),
                HttpStatus.OK);
    }

}
