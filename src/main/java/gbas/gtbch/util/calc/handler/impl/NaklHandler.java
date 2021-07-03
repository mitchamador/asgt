package gbas.gtbch.util.calc.handler.impl;

import gbas.eds.gtbch.invoice.ConvertXmlToVagonOtprTransit;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.calc.CalcData;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.otpravka.object.VagonOtpr;
import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.PayTransportation;

import java.sql.Connection;

import static gbas.gtbch.util.calc.handler.Handler.checkTags;

/**
 * xml gt convertation and calculation
 */
public class NaklHandler implements ObjectHandler {

    @Override
    public boolean check(String xml) {
        return checkTags(xml, "<table name=\"nakl\"", "</table>");
    }

    @Override
    public void calc(CalcData data, Connection connection) throws Exception {
        if (data.getCalculationLog() != null) {
            data.getCalculationLog().setType(CalculationLog.Type.NAKL);
        }
        ConvertXmlToVagonOtprTransit convertXmlToVagonOtprTransit = ConvertXmlToVagonOtprTransit.createConvertXmlToVOT(connection);
        String string = convertXmlToVagonOtprTransit.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                ConstantsParameters.NO_SYSTEM, null);
        if (string != null) {
            data.setCalculationObject(convertXmlToVagonOtprTransit.getObject());
        }

        if (data.getCalculationObject() instanceof VagonOtprTransit) {
            fillResultData(data, new PayTransportation(connection).calcPlata((VagonOtprTransit) data.getCalculationObject()));
        }
    }

    static void fillResultData(CalcData data, CalcPlataData c) {
        if (c != null) {

            if (c.vo != null) {
                if (data.getCalculationLog() != null) {
                    data.getCalculationLog().setNumber(c.vo.n_otpr);
                    if (c.vo.oper == VagonOtpr.OPER_DEPARTURE) {
                        data.getCalculationLog().setStation(c.vo.k_st_otpr);
                    } else if (c.vo.oper == VagonOtpr.OPER_ARRIVAL) {
                        data.getCalculationLog().setStation(c.vo.k_st_nazn);
                    }
                }
            }

            data.setTextResult(c.s);
            data.setOutputXml(c.getXml());
        } else {
            data.setTextResult("Ошибка расчета");
        }

    }
}
