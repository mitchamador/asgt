package gbas.gtbch.web;

import gbas.gtbch.model.ServerResponse;
import gbas.gtbch.util.CalcHandler;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalcController {

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
        response.setMessage(getCalcHandler().calc(data));

        return response;
    }


}