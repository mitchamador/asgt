package gbas.gtbch.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbas.gtbch.model.ServerResponse;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.CalculationLogService;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.calc.CalcData;
import gbas.gtbch.util.calc.CalcHandler;
import gbas.gtbch.util.UtilDate8;
import gbas.gtbch.util.XmlFormatter;
import gbas.gtbch.web.request.KeyValue;
import gbas.tvk.nsi.cash.Func;
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
    public ResponseEntity<String> calc(@RequestBody String data) {

        String response = calcHandler.calc(new CalcData(data, new CalculationLog(CalculationLog.Source.REST))).getTextResult();

        logger.info(String.format("/api/calc response: \"%s\"", getCroppedString(response)));

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/api/calcdata", method = RequestMethod.POST)
    public ResponseEntity<CalcData> calcdata(@RequestBody String data) {

        CalcData calcData = calcHandler.calc(new CalcData(data, new CalculationLog(CalculationLog.Source.REST)));
        if (calcData.getCalculationLog() != null) {
            calcData.getCalculationLog().setFileName(UtilDate8.getStringDate(calcData.getCalculationLog().getInboundTime(), "yyyyMMdd_HHmmss"));
        }

        logger.info(String.format("/api/calc response: \"%s\"", getCroppedString(calcData.getTextResult())));

        return ResponseEntity.ok(calcData);
    }

    @RequestMapping(value = "/api/calcxml", method = RequestMethod.POST)
    public ResponseEntity<String> calcxml(@RequestBody String data) {

        String response = calcHandler.calc(new CalcData(data, new CalculationLog(CalculationLog.Source.REST))).getOutputXml();

        logger.info(String.format("/api/calcxml response: \"%s\"", getCroppedString(response)));

        return ResponseEntity.ok(response);
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
                if (Func.isEmpty(log.getJmsCorrelationId())) {
                    log.setJmsCorrelationId(String.valueOf(log.getId()));
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
    public ResponseEntity getCalculationLog(
            HttpServletRequest request,
            @PathVariable int id,
            @RequestParam(value = "format", required = false) boolean prettyPrint) throws JsonProcessingException {
        CalculationLog log = calculationLogService.findById(id);
        if (log != null) {

            String inboundXml = log.getInboundXml();

            if (prettyPrint && inboundXml != null) {
                log.setInboundXml(XmlFormatter.formatXml(inboundXml));
            }

            log.setFileName(UtilDate8.getStringDate(log.getInboundTime(), "yyyyMMdd_HHmmss"));
        }
        return GzippedResponseEntity.getGzippedResponseEntity(request, new ObjectMapper().writeValueAsString(log));
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