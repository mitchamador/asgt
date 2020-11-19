package gbas.gtbch.web;

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

    @RequestMapping(value = "/api/calclog", method = RequestMethod.GET)
    public ResponseEntity<List<CalculationLog>> getCalculationLogList(
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "station", required = false) String station,
            @RequestParam(value = "number", required = false) String number,
            @RequestParam(value = "date_begin", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateBegin,
            @RequestParam(value = "date_end", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateEnd) {

        CalculationLog filter = new CalculationLog();

        if (source != null) {
            filter.setSource(CalculationLog.Source.getSource(source));
        }
        if (type != null) {
            filter.setType(CalculationLog.Type.getType(source));
        }
        if (station != null) {
            filter.setStation(station);
        }
        if (number != null) {
            filter.setNumber(number);
        }

        return new ResponseEntity<>(calculationLogListRepository.getList(null, dateBegin, dateEnd), HttpStatus.OK);
    }

    @Autowired
    private CalculationLogService calculationLogService;

    @RequestMapping(value = "/api/calclog/{id}", method = RequestMethod.GET)
    public ResponseEntity<CalculationLog> getCalculationLog(
            @PathVariable int id) {
        return new ResponseEntity<>(calculationLogService.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/calclog/sources", method = RequestMethod.GET)
    public ResponseEntity<List<KeyValue>> getCalculationLogSources() {
        List<KeyValue> list = new ArrayList<>();
        list.add(new KeyValue(null, "Все"));
        for (CalculationLog.Source source : CalculationLog.Source.values()) {
            list.add(new KeyValue(source.name(), source.getName()));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

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