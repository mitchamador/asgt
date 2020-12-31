package gbas.gtbch.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbas.gtbch.model.ServerResponse;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.CalculationLogService;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.CalcData;
import gbas.gtbch.util.CalcHandler;
import gbas.gtbch.web.request.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static gbas.gtbch.util.CropString.getCroppedString;

@RestController
public class ApiController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final CalcHandler calcHandler;
    private final TpImportDateService tpImportDateService;
    private final CalculationLogService calculationLogService;

    public ApiController(CalcHandler calcHandler, TpImportDateService tpImportDateService, CalculationLogService calculationLogService) {
        this.calcHandler = calcHandler;
        this.tpImportDateService = tpImportDateService;
        this.calculationLogService = calculationLogService;
    }

    @RequestMapping(value = "/api/calc", method = RequestMethod.POST)
    public ServerResponse calc(@RequestParam("data") String data) {

        ServerResponse response = new ServerResponse();
        response.setMessage(calcHandler.calc(new CalcData(data, new CalculationLog(CalculationLog.Source.REST))).getTextResult());

        logger.info(String.format("/api/calc response: \"%s\"", getCroppedString(response.getMessage())));

        return response;
    }

    @RequestMapping(value = "/api/calcxml", method = RequestMethod.POST)
    public ServerResponse calcxml(@RequestParam("data") String data) {

        ServerResponse response = new ServerResponse();
        response.setMessage(calcHandler.calc(new CalcData(data, new CalculationLog(CalculationLog.Source.REST))).getOutputXml());

        logger.info(String.format("/api/calcxml response: \"%s\"", getCroppedString(response.getMessage())));

        return response;
    }

    @RequestMapping(value = "/api/tpdate", method = RequestMethod.GET)
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
    @RequestMapping(value = "/api/calclog", method = RequestMethod.GET)
    public ResponseEntity<List<CalculationLog>> getCalculationLogList(
            @RequestParam Map<String,String> params,
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm") Date dateEnd) {

        List<CalculationLog> calculationLogs = calculationLogService.getList(params, dateBegin, dateEnd);

        if (calculationLogs != null) {
            for (CalculationLog log : calculationLogs) {
                if (log.getInboundTime() != null && log.getOutboundTime() != null) {
                    log.setDurationText(String.format("%.1f c", (double) (log.getOutboundTime().getTime() - log.getInboundTime().getTime()) / TimeUnit.SECONDS.toMillis(1)));
                }
            }
        }

        return new ResponseEntity<>(calculationLogs, HttpStatus.OK);
    }

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/calclog/{id}", method = RequestMethod.GET)
    public ResponseEntity getCalculationLog(HttpServletRequest request, @PathVariable int id) throws JsonProcessingException {
            return GzippedResponseEntity.getGzippedResponseEntity(request, new ObjectMapper().writeValueAsString(calculationLogService.findById(id)));
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/api/calclog/sources", method = RequestMethod.GET)
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
    @RequestMapping(value = "/api/calclog/types", method = RequestMethod.GET)
    public ResponseEntity<List<KeyValue>> getCalculationLogTypes() {
        List<KeyValue> list = new ArrayList<>();
        list.add(new KeyValue(null, "Все"));
        for (CalculationLog.Type type : CalculationLog.Type.values()) {
            list.add(new KeyValue(type.name(), type.getName()));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}