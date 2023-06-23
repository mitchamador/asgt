package gbas.gtbch.util.calc.handler.impl;

import gbas.eds.gtbch.keu16.calc.Keu16Calculator;
import gbas.eds.gtbch.keu16.convert.GtKeu16Converter;
import gbas.eds.soap.obj.DocEP;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.calc.GtCalcData;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.report.keu16.service.Keu16Data;
import org.springframework.stereotype.Component;

import java.sql.Connection;

import static gbas.gtbch.util.calc.handler.Handler.checkTags;

/**
 * Keu16 convertion and calculation
 */
@Component
public class Keu16Handler implements ObjectHandler {
    @Override
    public boolean check(String xml) {
        return checkTags(xml, "<table name=\"keu16\"", "</table>");
    }

    @Override
    public void calc(GtCalcData data, Connection connection) throws Exception {
        if (data.getCalculationLog() != null) {
            data.getCalculationLog().setType(CalculationLog.Type.KEU16);
        }
        DocEP docEC = new GtKeu16Converter();
        String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                ConstantsParameters.NO_SYSTEM, null);

        if(string != null) {
            data.setCalculationObject(docEC.getObject());
        }

        if (data.getCalculationObject() instanceof Keu16Data) {
            Keu16Data keu16Data = (Keu16Data) data.getCalculationObject();
            CalcPlataData cp = Keu16Calculator.calc(connection, keu16Data);
            data.setTextResult(cp.s);
            data.setOutputXml(cp.getXml());

            if (data.getCalculationLog() != null) {
                data.getCalculationLog().setNumber(keu16Data.getKeu16Number());
                data.getCalculationLog().setStation(keu16Data.getStanKod());
            }
        }
    }
}
