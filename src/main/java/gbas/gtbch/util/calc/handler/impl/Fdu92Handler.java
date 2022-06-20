package gbas.gtbch.util.calc.handler.impl;

import gbas.eds.gtbch.fdu92.GtFdu92Converter;
import gbas.eds.gtbch.fdu92.calc.CardCalculator;
import gbas.eds.soap.obj.DocEP;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.calc.CalcData;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.card.service.ContractCardData;
import gbas.tvk.payment.CalcPlataData;
import org.springframework.stereotype.Component;

import java.sql.Connection;

import static gbas.gtbch.util.calc.handler.Handler.checkTags;

/**
 * Fdu92 convertation and calculation
 */
@Component
public class Fdu92Handler implements ObjectHandler {
    @Override
    public boolean check(String xml) {
        return checkTags(xml, "<table name=\"fdu92\"", "</table>");
    }

    @Override
    public void calc(CalcData data, Connection connection) throws Exception {
        if (data.getCalculationLog() != null) {
            data.getCalculationLog().setType(CalculationLog.Type.CARD);
        }
        DocEP docEC = new GtFdu92Converter();
        String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                ConstantsParameters.NO_SYSTEM, null);

        if(string != null) {
            data.setCalculationObject(docEC.getObject());
        }

        if (data.getCalculationObject() instanceof ContractCardData) {
            ContractCardData ccd = (ContractCardData) data.getCalculationObject();
            CalcPlataData cp = CardCalculator.calc(connection, ccd);
            data.setTextResult(cp.s);
            data.setOutputXml(cp.getXml());

            if (data.getCalculationLog() != null) {
                data.getCalculationLog().setNumber(ccd.n_contract);
                data.getCalculationLog().setStation(ccd.code_station);
            }
        }
    }
}
