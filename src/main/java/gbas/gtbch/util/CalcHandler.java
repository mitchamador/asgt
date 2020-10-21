package gbas.gtbch.util;

import gbas.eds.gtbch.ConvertXmlToVagonOtprTransit;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.tvk.otpravka.object.VagonOtpr;
import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.PayTransportation;
import gbas.tvk.report.gu46a.gtGu46.CountGu46;
import gbas.tvk.report.gu46a.gtGu46.bean.VedGu46;
import gbas.tvk.report.gu46a.gtGu46.convert.ConvertXmlGtToGu46;
import gbas.tvk.report.gu46a.gtGu46.extend.Tm;
import gbas.tvk.report.gu46a.gtGu46.parser.GTGu46WriteXml;
import gbas.tvk.service.SQLUtils;
import gbas.tvk.util.GZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

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

    public CalcData calc(CalcData data) {

        logger.debug("calchandler process started...");

        try {

            Object obj = null;

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"nakl\"", "</table>")) {
                ConvertXmlToVagonOtprTransit convertXmlToVagonOtprTransit = new ConvertXmlToVagonOtprTransit(connection);
                String string = convertXmlToVagonOtprTransit.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);
                if (string != null) {
                    obj = convertXmlToVagonOtprTransit.getObject(VagonOtpr.OPER_DEPARTURE);
                }
            }

            if (obj == null && checkTags(data.getInputXml(), "<gbas.tvk.otpravka.object.VagonOtprTransit>", "</gbas.tvk.otpravka.object.VagonOtprTransit>")) {
                obj = GZipUtils.xml2Object(data.getInputXml());
            }

/*
            if (obj == null && checkTags(data, "<CompleteDocument type=\"gbas.tvk.otpravka.object.VagonOtprTransit\">", "</CompleteDocument>")) {
                obj = XML2Object.parse(new ByteArrayInputStream(data.getBytes()));
            }
*/

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"gu46\"", "</table>")) {
                ConvertXmlGtToGu46 docEC = new ConvertXmlGtToGu46(connection);
                String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);

                if(string != null) {
                    obj = docEC.getObject((Tm[]) docEC.getObject());
                }
            }

            if (obj instanceof CalcPlataData || obj instanceof VagonOtprTransit) {

                CalcPlataData c;
                PayTransportation payTransportation = new PayTransportation(connection);

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

                if (c != null) {
                    data.setTextResult(c.s);
                    data.setOutputXml(c.getXml());
                } else {
                    data.setTextResult("Ошибка расчета");
                }

            } else if (obj instanceof VedGu46) {
                VedGu46 vedGu46 = new CountGu46(connection).calcVedGu46((VedGu46) obj);
                data.setTextResult(vedGu46.toString());
                data.setOutputXml(GTGu46WriteXml.createXml(vedGu46));
            } else {
                data.setTextResult(String.format("Неверный объект расчета: \"%s\"", data));
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.setTextResult(e.getMessage());
        }

        logger.debug("calchandler process finished");

        return data;
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
