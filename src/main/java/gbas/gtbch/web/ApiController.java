package gbas.gtbch.web;

import gbas.gtbch.model.ServerResponse;
import gbas.gtbch.sapod.model.TpImportDate;
import gbas.gtbch.sapod.service.TpImportDateService;
import gbas.gtbch.util.CalcData;
import gbas.gtbch.util.CalcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        response.setMessage(getCalcHandler().calc(new CalcData(data)).getTextResult());

        logger.info(String.format("/api/calc response: \"%s\"", getCroppedString(response.getMessage())));

        return response;
    }

    @RequestMapping(value = "/api/calcxml", method = RequestMethod.POST)
    public ServerResponse calcxml(@RequestParam("data") String data) {

        ServerResponse response = new ServerResponse();
        response.setMessage(getCalcHandler().calc(new CalcData(data)).getOutputXml());

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

}