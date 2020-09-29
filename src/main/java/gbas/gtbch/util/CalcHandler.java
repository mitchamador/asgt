package gbas.gtbch.util;

import gbas.eds.gtbch.ConvertXmlToVagonOtprTransit;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.tvk.otpravka.object.VagonOtpr;
import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.PayTransportation;
import gbas.tvk.service.SQLUtils;
import gbas.tvk.util.GZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
public class CalcHandler {

    private final static Logger logger = LoggerFactory.getLogger(CalcHandler.class.getName());

    private Connection connection;

    public CalcHandler(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public String calc(String data) {

        logger.debug("calchandler process started...");

        String response;
        PayTransportation payTransportation;
        ConvertXmlToVagonOtprTransit convertXmlToVagonOtprTransit;

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
                convertXmlToVagonOtprTransit = new ConvertXmlToVagonOtprTransit(connection);
                String string = convertXmlToVagonOtprTransit.parse(data, ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);
                if (string != null) {
                    obj = convertXmlToVagonOtprTransit.getObject(VagonOtpr.OPER_DEPARTURE);
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

            if (obj instanceof CalcPlataData || obj instanceof VagonOtprTransit) {

                payTransportation = new PayTransportation(connection);

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
                } else {
                    c = payTransportation.calcPlata((VagonOtprTransit) obj);
                }

            } else {
                c = new CalcPlataData();
                c.s = String.format("Неверный объект расчета: \"%s\"", data);
            }

            response = c != null ? c.s : null;

        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        } finally {

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

    /**
     * default destroy method
     */
    public void close() {
        logger.debug("calchandler destroy");
        SQLUtils.close(connection);
    }
}
