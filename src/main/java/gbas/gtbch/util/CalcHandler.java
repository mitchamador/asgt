package gbas.gtbch.util;

import gbas.eds.gtbch.ConvertXmlToVagonOtprTransit;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.tvk.otpravka.object.VagonOtpr;
import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.PayTransportation;
import gbas.tvk.util.GZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Scope("prototype")
public class CalcHandler {

    private final static Logger logger = LoggerFactory.getLogger(CalcHandler.class.getName());

    private final PayTransportation payTransportation;

    private final DataSource sapodDataSource;

    public CalcHandler(PayTransportation payTransportation, @Qualifier("sapodDataSource") DataSource sapodDataSource) {
        this.payTransportation = payTransportation;
        this.sapodDataSource = sapodDataSource;
    }

    public String calc(String data) {

        logger.debug("calchandler process started...");

        String response = null;

        try {
            CalcPlataData c = null;

            Object obj = null;

/*
            1. xstream xml
            <gbas.tvk.otpravka.object.VagonOtprTransit>
            </gbas.tvk.otpravka.object.VagonOtprTransit>

            2. object2xml
            <?xml version="1.0" encoding="UTF-8"?>
            <CompleteDocument type="gbas.tvk.otpravka.object.VagonOtprTransit">
            </CompleteDocument>

            3. full ep xml
            <?xml version="1.0" encoding="UTF-8"?>
            <doc name="nakl" pns="0" kos="0" id="0" epd="0" oper="0" version="1">
            </doc>

            4. small ep xml
            <?xml version="1.0" encoding="UTF-8"?>
            <doc name="GT" version="1">
            </doc>
 */

            if (obj == null && checkTags(data, "<doc name=\"GT\"", "</doc>") || checkTags(data, "<doc name=\"nakl\"", "</doc>")) {
                ConvertXmlToVagonOtprTransit docEC = new ConvertXmlToVagonOtprTransit();
                String string = docEC.parse(data, ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);
                if (string != null) {
                    obj = docEC.getObject(VagonOtpr.OPER_DEPARTURE, sapodDataSource.getConnection());
                }
            }

            if (obj == null && checkTags(data, "<gbas.tvk.otpravka.object.VagonOtprTransit>", "</gbas.tvk.otpravka.object.VagonOtprTransit>")) {
                obj = GZipUtils.xml2Object(data);
            }

/*
            if (obj == null && checkTags(data, "<CompleteDocument type=\"gbas.tvk.otpravka.object.VagonOtprTransit\">", "</CompleteDocument>")) {
                obj = XML2Object.parse(new ByteArrayInputStream(data.getBytes()));
            }
*/

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

    private boolean checkTags(String text, String start, String end) {

        try {
            int indexStart = text.indexOf(start);
            int indexEnd = text.indexOf(end);

            if (indexStart != -1 && indexEnd != -1 && indexEnd > indexStart) {
                return true;
            }
        } catch (Exception ignored) {
        }

        return false;
    }
}
