package gbas.gtbch.util;

import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.PayTransportation;
import gbas.tvk.util.GZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CalcHandler {

    private final static Logger logger = LoggerFactory.getLogger(CalcHandler.class.getName());

    private final PayTransportation payTransportation;

    public CalcHandler(PayTransportation payTransportation) {
        this.payTransportation = payTransportation;
    }

    public String calc(String data) {

        logger.debug("calchandler process started...");

        String response = null;

        try {
            CalcPlataData c = null;

            Object obj = GZipUtils.xml2Object(data);

            if (obj instanceof CalcPlataData) {
                c = (CalcPlataData) obj;

                switch (c.mode) {
                    case 1:
                        c = payTransportation.countPlata(c);
                        break;
                    case 2:
                        c = payTransportation.countPlata(c, c.strIsklTar, c.ssCurs);
                        break;
                    case 3:
                        c = payTransportation.countPlataMarshrut(c);
                        break;
                    case 4:
                        c = payTransportation.countPlataMarshrut(c, c.strIsklTar);
                        break;
                }
            } else if (obj instanceof VagonOtprTransit) {
                c = payTransportation.calcPlata((VagonOtprTransit) obj);
            } else {
                c = new CalcPlataData();
                c.s = String.format("Неверный объект расчета: \"%s\"", data);
            }

            response = c != null ? c.s : null;

        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        } finally {
            if (payTransportation != null) {
                payTransportation.close();
            }
        }

        logger.debug("calchandler process finished");

        return response != null ? response : "Ошибка";
    }
}
