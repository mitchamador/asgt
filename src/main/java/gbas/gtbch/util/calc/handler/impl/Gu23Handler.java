package gbas.gtbch.util.calc.handler.impl;

import gbas.eds.gtbch.gu23.convert.ConvertXmlGtToGu23Gt;
import gbas.eds.gtbch.gu23.obj.Gu23ObjGt;
import gbas.eds.gtbch.gu23.parser.GtGu23Xml;
import gbas.eds.gtbch.gu23.server.PayGu23;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.calc.GtCalcData;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import org.springframework.stereotype.Component;

import java.sql.Connection;

import static gbas.gtbch.util.calc.handler.Handler.checkTags;

/**
 * Gu23 convertion and calculation
 */
@Component
public class Gu23Handler implements ObjectHandler {
    @Override
    public boolean check(String xml) {
        return checkTags(xml, "<table name=\"gu23\"", "</table>");
    }

    @Override
    public void calc(GtCalcData data, Connection connection) throws Exception {
        if (data.getCalculationLog() != null) {
            data.getCalculationLog().setType(CalculationLog.Type.GU23);
        }
        ConvertXmlGtToGu23Gt docEC = new ConvertXmlGtToGu23Gt(connection);
        String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                ConstantsParameters.NO_SYSTEM, null);

        if (string != null) {
            data.setCalculationObject(docEC.getObject());
        }

        if (data.getCalculationObject() instanceof Gu23ObjGt) {
            Gu23ObjGt aktGu23 = new PayGu23(connection).calcGu23((Gu23ObjGt) data.getCalculationObject());
            data.setTextResult(aktGu23.toString());
            data.setOutputXml(GtGu23Xml.getXml(aktGu23));

            if (data.getCalculationLog() != null) {
                data.getCalculationLog().setNumber(aktGu23.getNomAkt());
                data.getCalculationLog().setStation(aktGu23.getStationtc());
            }
        }
    }
}
