package gbas.gtbch.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbas.gtbch.config.settings.SettingsProperties;
import gbas.gtbch.model.ServerResponse;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.model.users.User;
import gbas.gtbch.sapod.service.CalculationLogService;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.Syncronizer;
import gbas.gtbch.util.SystemInfoProperties;
import gbas.gtbch.util.UtilDate8;
import gbas.gtbch.util.XmlFormatter;
import gbas.gtbch.util.calc.CalcData;
import gbas.gtbch.util.calc.CalcHandler;
import gbas.gtbch.web.request.KeyValue;
import gbas.gtbch.web.response.Response;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.util.synchronizator.entity.SyncFileData;
import gbas.tvk.util.synchronizator.entity.SyncGroupsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static gbas.gtbch.util.CropString.getCroppedString;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "/api")
public class ApiController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private final CalcHandler calcHandler;
    private final TpImportDateService tpImportDateService;
    private final CalculationLogService calculationLogService;
    private final ObjectMapper mapper;
    private final SystemInfoProperties systemInfoProperties;
    private final SettingsProperties settingsProperties;
    private final Syncronizer syncronizer;

    public ApiController(CalcHandler calcHandler, TpImportDateService tpImportDateService, CalculationLogService calculationLogService, ObjectMapper mapper, SystemInfoProperties systemInfoProperties, SettingsProperties settingsProperties, Syncronizer syncronizer) {
        this.calcHandler = calcHandler;
        this.tpImportDateService = tpImportDateService;
        this.calculationLogService = calculationLogService;
        this.mapper = mapper;
        this.systemInfoProperties = systemInfoProperties;
        this.settingsProperties = settingsProperties;
        this.syncronizer = syncronizer;
    }

    private Map<String, String> addUserPrincipal(Principal principal, Map<String, String> params) {
        if (principal instanceof UsernamePasswordAuthenticationToken && ((UsernamePasswordAuthenticationToken) principal).getPrincipal() instanceof User) {
            params.put("web_user", ((User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getFio());
        }
        return params;
    }

    private CalculationLog createCalculationLog(Map<String, String> params) {
        CalculationLog calculationLog;
        if (params != null && params.containsKey("sapod_user")) {
            calculationLog = new CalculationLog(CalculationLog.Source.SAPOD);
            calculationLog.setUser(params.get("sapod_user"));
        } else if (params != null && params.containsKey("web_user")) {
            calculationLog = new CalculationLog(CalculationLog.Source.WEBUI);
            calculationLog.setUser(params.get("web_user"));
        } else {
            calculationLog = new CalculationLog(CalculationLog.Source.REST);
        }
        return calculationLog;
    }

    @RequestMapping(value = "/calc", method = RequestMethod.POST)
    public ResponseEntity<String> calc(Principal principal, @RequestParam(required = false) Map<String,String> params, @RequestBody String data) {

        String response = calcHandler.calc(new CalcData(data, createCalculationLog(addUserPrincipal(principal, params)))).getTextResult();

        logger.info(String.format("/api/calc response: \"%s\"", getCroppedString(response)));

        return ok(response);
    }

    @RequestMapping(value = "/calcdata", method = RequestMethod.POST)
    public ResponseEntity<CalcData> calcdata(Principal principal, @RequestParam(required = false) Map<String, String> params, @RequestBody String data) {

        CalcData calcData = calcHandler.calc(new CalcData(data, createCalculationLog(addUserPrincipal(principal, params))));
        if (calcData.getCalculationLog() != null) {
            calcData.setFileName(UtilDate8.getStringDate(calcData.getCalculationLog().getInboundTime(), "yyyyMMdd_HHmmss"));
        }

        logger.info(String.format("/api/calc response: \"%s\"", getCroppedString(calcData.getTextResult())));

        return ok(calcData);
    }

    @RequestMapping(value = "/calcxml", method = RequestMethod.POST)
    public ResponseEntity<String> calcxml(Principal principal, @RequestParam(required = false) Map<String,String> params, @RequestBody String data) {

        String response = calcHandler.calc(new CalcData(data, createCalculationLog(addUserPrincipal(principal, params)))).getOutputXml();

        logger.info(String.format("/api/calcxml response: \"%s\"", getCroppedString(response)));

        return ok(response);
    }

    @RequestMapping(value = "/tpdate", method = RequestMethod.GET)
    public ServerResponse getTpImportDate() {

        TpImportDate tpImportDate = tpImportDateService.getTpImportDate();

        ServerResponse response = new ServerResponse();
        response.setMessage(tpImportDate != null ? tpImportDate.getTpDateString() : "");

        return response;
    }

    /**
     * get CalculationLog
     * <p>params:</p>
     * <ul>
     *     <li>type - {@link CalculationLog.Type}</li>
     *     <li>source - {@link CalculationLog.Source}</li>
     *     <li>station - station code</li>
     *     <li>date_begin - start period (dd.MM.yyyy)</li>
     *     <li>date_end - end period (dd.MM.yyyy)</li>
     * </ul>
     * @param params
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    @RequestMapping(value = "/calclog", method = RequestMethod.GET)
    public ResponseEntity getCalculationLogList(
            HttpServletRequest request,
            @RequestParam Map<String,String> params,
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") Date dateEnd) throws JsonProcessingException{

        List<CalculationLog> calculationLogs = calculationLogService.getList(params, dateBegin, dateEnd);

        if (calculationLogs != null) {
            for (CalculationLog log : calculationLogs) {
                if (log.getInboundTime() != null && log.getOutboundTime() != null) {
                    log.setDurationText(String.format("%.1f c", (double) (log.getOutboundTime().getTime() - log.getInboundTime().getTime()) / TimeUnit.SECONDS.toMillis(1)));
                }
                if (Func.isEmpty(log.getJmsCorrelationId())) {
                    log.setJmsCorrelationId(String.valueOf(log.getId()));
                }
            }
        }

        return GzippedResponseEntity.getGzippedResponseEntity(request, mapper.writeValueAsString(calculationLogs));
    }

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/calclog/{id}", method = RequestMethod.GET)
    public ResponseEntity getCalculationLog(
            HttpServletRequest request,
            @PathVariable int id,
            @RequestParam(value = "format", required = false) boolean prettyPrint) throws JsonProcessingException {
        CalculationLog log = calculationLogService.findById(id);
        if (log != null) {

            String inboundXml = log.getInboundXml();

            if (prettyPrint && inboundXml != null) {
                String formattedXml = XmlFormatter.formatXml(inboundXml);
                if (formattedXml != null && !formattedXml.trim().isEmpty()) {
                    log.setInboundXml(formattedXml);
                }
            }

            log.setFileName(UtilDate8.getStringDate(log.getInboundTime(), "yyyyMMdd_HHmmss"));
        }
        return GzippedResponseEntity.getGzippedResponseEntity(request, mapper.writeValueAsString(log));
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/calclog/sources", method = RequestMethod.GET)
    public ResponseEntity<List<KeyValue>> getCalculationLogSources() {
        List<KeyValue> list = new ArrayList<>();
        list.add(new KeyValue(null, "Все"));
        for (CalculationLog.Source source : CalculationLog.Source.values()) {
            list.add(new KeyValue(source.name(), source.getName()));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/calclog/types", method = RequestMethod.GET)
    public ResponseEntity<List<KeyValue>> getCalculationLogTypes() {
        List<KeyValue> list = new ArrayList<>();
        list.add(new KeyValue(null, "Все"));
        for (CalculationLog.Type type : CalculationLog.Type.values()) {
            list.add(new KeyValue(type.name(), type.getName()));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/sysinfo", method = RequestMethod.GET)
    public ResponseEntity<List<KeyValue>> getSystemInfo() {
        return ok(systemInfoProperties.getSystemProperties());
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public ResponseEntity<SettingsProperties> getGtSettings() {
        return new ResponseEntity<>(settingsProperties, HttpStatus.OK);
    }

    @RequestMapping(value = "/sync/create/{set}", method = RequestMethod.GET)
    public ResponseEntity createSyncData(@PathVariable String set) {

        try {
            SyncFileData syncFileData = syncronizer.create(SyncGroupsSet.valueOf(set.toUpperCase()));
            if (syncFileData == null) {
                throw new Exception("SyncronizerDataCreator.create() result is null");
            }
            if (syncFileData.getData() == null) {
                throw new Exception("SyncronizerDataCreator.create() data is null");
            }
            ByteArrayResource resource = new ByteArrayResource(syncFileData.getData());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + syncFileData.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(e.getMessage()), HttpStatus.OK);
        }
    }
}