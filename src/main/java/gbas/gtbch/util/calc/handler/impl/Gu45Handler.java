package gbas.gtbch.util.calc.handler.impl;

import gbas.eds.rw.isc.typeexchange.Tm;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.calc.GtCalcData;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.pamlist.gt.ConvertXmlGtToGu45;
import gbas.tvk.pamlist.gt.CountGu45;
import gbas.tvk.pamlist.gt.GTGu45WriteXml;
import gbas.tvk.pamlist.gt.PamGu45;
import org.springframework.stereotype.Component;

import java.sql.Connection;

import static gbas.gtbch.util.calc.handler.Handler.checkTags;

/**
 * Gu45 convertion and calculation
 */
@Component
public class Gu45Handler implements ObjectHandler {

    @Override
    public boolean check(String xml) {
        return checkTags(xml, "<table name=\"gu45\"", "</table>");
    }

    @Override
    public void calc(GtCalcData data, Connection connection) throws Exception {
        if (data.getCalculationLog() != null) {
            data.getCalculationLog().setType(CalculationLog.Type.GU45);
        }
        ConvertXmlGtToGu45 docEC = new ConvertXmlGtToGu45(connection);
        String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                ConstantsParameters.NO_SYSTEM, null);

        if(string != null) {
            data.setCalculationObject(docEC.getObject((Tm[]) docEC.getObject()));
        }

        if (data.getCalculationObject() instanceof PamGu45) {
            PamGu45 pamGu45 = new CountGu45(connection).calcPamGu45((PamGu45) data.getCalculationObject());
            data.setTextResult(pamGu45.toString());
            data.setOutputXml(GTGu45WriteXml.createXml(pamGu45));

            if (data.getCalculationLog() != null) {
                data.getCalculationLog().setNumber(pamGu45.num);
                data.getCalculationLog().setStation(pamGu45.stationCode);
            }
        }
    }
}
