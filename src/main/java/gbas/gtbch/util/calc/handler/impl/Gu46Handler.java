package gbas.gtbch.util.calc.handler.impl;

import gbas.eds.rw.isc.typeexchange.Tm;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.calc.CalcData;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.report.gu46a.gtGu46.CountGu46;
import gbas.tvk.report.gu46a.gtGu46.bean.VedGu46;
import gbas.tvk.report.gu46a.gtGu46.convert.ConvertXmlGtToGu46;
import gbas.tvk.report.gu46a.gtGu46.parser.GTGu46WriteXml;

import java.sql.Connection;

import static gbas.gtbch.util.calc.handler.Handler.checkTags;

/**
 * Gu46 convertation and calculation
 */
public class Gu46Handler implements ObjectHandler {

    @Override
    public boolean check(String xml) {
        return checkTags(xml, "<table name=\"gu46\"", "</table>");
    }

    @Override
    public void calc(CalcData data, Connection connection) throws Exception {
        if (data.getCalculationLog() != null) {
            data.getCalculationLog().setType(CalculationLog.Type.GU46);
        }
        ConvertXmlGtToGu46 docEC = new ConvertXmlGtToGu46(connection);
        String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                ConstantsParameters.NO_SYSTEM, null);

        if(string != null) {
            data.setCalculationObject(docEC.getObject((Tm[]) docEC.getObject()));
        }

        if (data.getCalculationObject() instanceof VedGu46) {
            VedGu46 vedGu46 = new CountGu46(connection).calcVedGu46((VedGu46) data.getCalculationObject());
            data.setTextResult(vedGu46.toString());
            data.setOutputXml(GTGu46WriteXml.createXml(vedGu46));

            if (data.getCalculationLog() != null) {
                data.getCalculationLog().setNumber(vedGu46.numVed);
                data.getCalculationLog().setStation(vedGu46.stationCode);
            }
        }
    }
}
