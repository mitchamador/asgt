package gbas.gtbch.util.calc.handler.impl;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.calc.GtCalcData;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.PayTransportation;
import gbas.tvk.util.GZipUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;

import static gbas.gtbch.util.calc.handler.Handler.checkTags;
import static gbas.gtbch.util.calc.handler.impl.NaklHandler.fillResultData;

/**
 * Xstream serialized {@link VagonOtprTransit} and {@link CalcPlataData} convertation and calculation
 */
@Component
public class VoHandler implements ObjectHandler {
    @Override
    public boolean check(String xml) {
        return checkTags(xml, "<gbas.tvk.payment.CalcPlataData>", "</gbas.tvk.payment.CalcPlataData>")
                || checkTags(xml, "<gbas.tvk.otpravka.object.VagonOtprTransit>", "</gbas.tvk.otpravka.object.VagonOtprTransit>");
    }

    @Override
    public void calc(GtCalcData data, Connection connection) throws Exception {
        if (data.getCalculationLog() != null) {
            data.getCalculationLog().setType(CalculationLog.Type.VOT);
        }
        data.setCalculationObject(GZipUtils.xml2Object(data.getInputXml()));

        if (data.getCalculationObject() instanceof CalcPlataData || data.getCalculationObject() instanceof VagonOtprTransit) {

            CalcPlataData c;
            PayTransportation payTransportation = new PayTransportation(connection);

            if (data.getCalculationObject() instanceof CalcPlataData) {
                c = payTransportation.calcPlata((CalcPlataData) data.getCalculationObject());
                c.setXml(GZipUtils.object2Xml(c));
            } else {
                c = payTransportation.calcPlata((VagonOtprTransit) data.getCalculationObject());
            }

            fillResultData(data, c);
        }
    }
}
