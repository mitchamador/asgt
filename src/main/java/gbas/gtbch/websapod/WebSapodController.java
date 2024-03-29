package gbas.gtbch.websapod;

import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.UtilDate8;
import gbas.sapod.bridge.constants.json.Errors;
import gbas.sapod.bridge.constants.json.Key;
import gbas.sapod.bridge.constants.json.KeyValue;
import gbas.sapod.bridge.controllers.CalcException;
import gbas.sapod.bridge.controllers.CalcService;
import gbas.sapod.bridge.controllers.RouteException;
import gbas.sapod.bridge.controllers.RouteService;
import gbas.sapod.bridge.utilities.JsonBuilder;
import gbas.tvk.desktop.Version;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static gbas.gtbch.web.GzippedResponseEntity.getGzippedResponseEntity;
import static gbas.sapod.bridge.utilities.JsonBuilder.*;

@RestController
public class WebSapodController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationManager authenticationManager;

    public WebSapodController(@Qualifier("authenticationManager") AuthenticationManager authenticationManager, @Qualifier("sapodDataSource") DataSource sapodDataSource, TpImportDateService tpImportDateService) {
        this.authenticationManager = authenticationManager;
        this.sapodDataSource = sapodDataSource;
        this.tpImportDateService = tpImportDateService;
    }

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
        Version Version = new Version();

        String version[] = Version.getVersion();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", version[0]);
        jsonObject.put("date", version[1]);

        return new ResponseEntity<>(
                jsonObject.toJSONString(),
                HttpStatus.OK
        );
    }

    final
    DataSource sapodDataSource;

    @RequestMapping(value = "/api/websapod/route", method = RequestMethod.GET)
    public ResponseEntity<String> route(HttpServletRequest request) {

        try (Connection c = sapodDataSource.getConnection()) {
            return new ResponseEntity<>(
                    getJsonObject(
                            new RouteService(request).calc(new ServicesImpl(c))
                    ).toJSONString(),
                    HttpStatus.OK);
        } catch (RouteException e) {
            return new ResponseEntity<>(
                    getRouteError(e.getMessage()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    getRouteError("Error calculating route"),
                    HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/api/websapod/calc", method = RequestMethod.GET)
    public ResponseEntity<String> calc(HttpServletRequest request) {

        try (Connection c = sapodDataSource.getConnection()) {
            return new ResponseEntity<>(
                    getJsonLabelValueArray(
                            new CalcService(request).calc(new ServicesImpl(c)).getLabelValueList()
                    ).toJSONString(),
                    HttpStatus.OK);
        } catch (CalcException e) {
            return new ResponseEntity<>(
                    getError(e.getCode()),
                    HttpStatus.OK);
        } catch (RouteException e) {
            return new ResponseEntity<>(
                    getError(e.getCode()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    getError(Errors.GENERAL),
                    HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/api/websapod/nsi", method = RequestMethod.GET)
    public ResponseEntity nsi(HttpServletRequest request, @RequestParam(name = "method") String method) {

        try (Connection c = sapodDataSource.getConnection()) {
            return getGzippedResponseEntity(request, getJsonArray(new ServicesImpl(c).getNsi(request.getParameter("method"))).toJSONString());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    getError(Errors.GENERAL),
                    HttpStatus.OK);
        }
    }

    final
    TpImportDateService tpImportDateService;


    @RequestMapping(value = "/api/websapod/tpdate", method = RequestMethod.GET)
    public ResponseEntity<String> route() {
        TpImportDate tpImportDate = tpImportDateService.getTpImportDate();

        return new ResponseEntity<>(
                getJsonObject(Collections.singletonList(new KeyValue("computed", UtilDate8.getStringDate(tpImportDate.getDateCreate(), "dd/MM/yy")))).toJSONString(),
                HttpStatus.OK);
    }

}
