package gbas.gtbch.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbas.gtbch.model.ServerResponse;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.repository.CalculationLogListRepository;
import gbas.gtbch.sapod.service.CalculationLogService;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.CalcData;
import gbas.gtbch.util.CalcHandler;
import gbas.gtbch.web.request.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static gbas.gtbch.util.CropString.getCroppedString;

@RestController
public class ApiController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * inject bean with prototype scope to singleton bean
     */
    @Autowired
    private ObjectFactory<CalcHandler> calcHandlerObjectFactory;

    private CalcHandler getCalcHandler() {
        return calcHandlerObjectFactory.getObject();
    }

    @RequestMapping(value = "/api/calc", method = RequestMethod.POST)
    public ServerResponse calc(@RequestParam("data") String data) {

        ServerResponse response = new ServerResponse();
        response.setMessage(getCalcHandler().calc(new CalcData(data, CalculationLog.Source.REST)).getTextResult());

        logger.info(String.format("/api/calc response: \"%s\"", getCroppedString(response.getMessage())));

        return response;
    }

    @RequestMapping(value = "/api/calcxml", method = RequestMethod.POST)
    public ServerResponse calcxml(@RequestParam("data") String data) {

        ServerResponse response = new ServerResponse();
        response.setMessage(getCalcHandler().calc(new CalcData(data, CalculationLog.Source.REST)).getOutputXml());

        logger.info(String.format("/api/calcxml response: \"%s\"", getCroppedString(response.getMessage())));

        return response;
    }

    @Autowired
    private TpImportDateService tpImportDateService;

    @RequestMapping(value = "/api/tpdate", method = RequestMethod.GET)
    public ServerResponse getTpImportDate() {

        TpImportDate tpImportDate = tpImportDateService.getTpImportDate();

        ServerResponse response = new ServerResponse();
        response.setMessage(tpImportDate != null ? tpImportDate.getTpDateString() : "");

        return response;
    }

    @Autowired
    private CalculationLogListRepository calculationLogListRepository;

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
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd) {

        return new ResponseEntity<>(calculationLogListRepository.getList(params, dateBegin, dateEnd), HttpStatus.OK);
    }

    @Autowired
    private CalculationLogService calculationLogService;

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/calclog/{id}", method = RequestMethod.GET)
    public ResponseEntity<CalculationLog> getCalculationLog(
            @PathVariable int id) {
        return new ResponseEntity<>(calculationLogService.findById(id), HttpStatus.OK);
    }

    /**
     *
     * @param request
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/api/calclog/{id}/gz", method = RequestMethod.GET)
    public ResponseEntity getCalculationLogGzipped(HttpServletRequest request, @PathVariable int id) throws JsonProcessingException {
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