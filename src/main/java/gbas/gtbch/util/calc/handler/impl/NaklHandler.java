package gbas.gtbch.util.calc.handler.impl;

import gbas.eds.gtbch.invoice.ConvertXmlToVagonOtprTransit;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.util.cache.AppCacheManager;
import gbas.gtbch.util.calc.GtCalcData;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.otpravka.object.VagonOtpr;
import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.PayTransportation;
import org.springframework.stereotype.Component;

import java.sql.Connection;

import static gbas.gtbch.util.calc.handler.Handler.checkTags;

/**
 * Nakl convertion and calculation
 */
@Component
public class NaklHandler implements ObjectHandler {

    private final AppCacheManager cacheManager;

    public NaklHandler(AppCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean check(String xml) {
        return checkTags(xml, "<table name=\"nakl\"", "</table>");
    }

    @Override
    public void calc(GtCalcData data, Connection connection) throws Exception {
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
            fillResultData(data, new PayTransportation(connection, cacheManager.getDbPaymentQueryCache()).calcPlata((VagonOtprTransit) data.getCalculationObject()));
        }
    }

    static void fillResultData(GtCalcData data, CalcPlataData c) {
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
