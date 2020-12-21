package gbas.gtbch.util;

import gbas.eds.gtbch.fdu92.GtFdu92Converter;
import gbas.eds.gtbch.fdu92.calc.CardCalculator;
import gbas.eds.gtbch.invoice.ConvertXmlToVagonOtprTransit;
import gbas.eds.rw.isc.typeexchange.Tm;
import gbas.eds.soap.obj.DocEP;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.service.CalculationLogService;
import gbas.tvk.card.service.ContractCardData;
import gbas.tvk.otpravka.object.VagonOtpr;
import gbas.tvk.otpravka.object.VagonOtprTransit;
import gbas.tvk.payment.CalcPlataData;
import gbas.tvk.payment.PayTransportation;
import gbas.tvk.report.gu46a.gtGu46.CountGu46;
import gbas.tvk.report.gu46a.gtGu46.bean.VedGu46;
import gbas.tvk.report.gu46a.gtGu46.convert.ConvertXmlGtToGu46;
import gbas.tvk.report.gu46a.gtGu46.parser.GTGu46WriteXml;
import gbas.tvk.service.SQLUtils;
import gbas.tvk.util.GZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 */
public class CalcHandler {

    private final static Logger logger = LoggerFactory.getLogger(CalcHandler.class.getName());

    /**
     *
     */
    private Connection connection;

    /**
     *
     */
    private CalculationLogService calculationLogService;

    public CalcHandler(Connection connection, CalculationLogService calculationLogService) throws SQLException {
        this.connection = connection;
        this.calculationLogService = calculationLogService;
    }

    public CalcData calc(CalcData data) {

        logger.debug("calchandler process started...");

        CalculationLog calculationLog = null;

        try {

            calculationLog = new CalculationLog();
            calculationLog.setInboundTime(new Date());
            calculationLog.setInboundXml(data.getInputXml());
            calculationLog.setSource(data.getSource());

            calculationLog = calculationLogService.save(calculationLog);

            calculationLog.setType(CalculationLog.Type.UNKNOWN);

            Object obj = null;

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"nakl\"", "</table>")) {
                calculationLog.setType(CalculationLog.Type.NAKL);
                ConvertXmlToVagonOtprTransit convertXmlToVagonOtprTransit = ConvertXmlToVagonOtprTransit.createConvertXmlToVOT(connection);
                String string = convertXmlToVagonOtprTransit.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);
                if (string != null) {
                    obj = convertXmlToVagonOtprTransit.getObject(VagonOtpr.OPER_DEPARTURE);
                }
            }

            if (obj == null && checkTags(data.getInputXml(), "<gbas.tvk.otpravka.object.VagonOtprTransit>", "</gbas.tvk.otpravka.object.VagonOtprTransit>")) {
                calculationLog.setType(CalculationLog.Type.VOT);
                obj = GZipUtils.xml2Object(data.getInputXml());
            }

/*
            if (obj == null && checkTags(data, "<CompleteDocument type=\"gbas.tvk.otpravka.object.VagonOtprTransit\">", "</CompleteDocument>")) {
                obj = XML2Object.parse(new ByteArrayInputStream(data.getBytes()));
            }
*/

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"gu46\"", "</table>")) {
                calculationLog.setType(CalculationLog.Type.GU46);
                ConvertXmlGtToGu46 docEC = new ConvertXmlGtToGu46(connection);
                String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);

                if(string != null) {
                    obj = docEC.getObject((Tm[]) docEC.getObject());
                }
            }

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"fdu92\"", "</table>")) {
                calculationLog.setType(CalculationLog.Type.CARD);
                DocEP docEC = new GtFdu92Converter();
                String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);

                if(string != null) {
                    obj = docEC.getObject();
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

                    if (c.vo != null) {
                        calculationLog.setNumber(c.vo.numberOtpr);
                        if (c.vo.oper == VagonOtpr.OPER_DEPARTURE) {
                            calculationLog.setStation(c.vo.k_st_otpr);
                        } else if (c.vo.oper == VagonOtpr.OPER_ARRIVAL) {
                            calculationLog.setStation(c.vo.k_st_nazn);
                        }
                    }

                    data.setTextResult(c.s);
                    data.setOutputXml(c.getXml());
                } else {
                    data.setTextResult("Ошибка расчета");
                }


            } else if (obj instanceof VedGu46) {
                VedGu46 vedGu46 = new CountGu46(connection).calcVedGu46((VedGu46) obj);
                data.setTextResult(vedGu46.toString());
                data.setOutputXml(GTGu46WriteXml.createXml(vedGu46));

                calculationLog.setNumber(vedGu46.numVed);
                calculationLog.setStation(vedGu46.stationCode);

            } else if (obj instanceof ContractCardData) {
                ContractCardData ccd = (ContractCardData) obj;
                CalcPlataData cp = CardCalculator.calc(connection, ccd);
                data.setTextResult(cp.s);
                data.setOutputXml(cp.getXml());

                calculationLog.setNumber(ccd.n_contract);
                calculationLog.setStation(ccd.code_station);

            } else {
                data.setTextResult(String.format("Неверный объект расчета: \"%s\"", data.getInputXml()));
                data.setError(CalcError.UNKNOWN_OBJECT.getCode());
            }


        } catch (Exception e) {
            e.printStackTrace();
            data.setTextResult(e.getMessage());
            data.setError(CalcError.EXCEPTION.getCode());
        } finally {

            if (calculationLog != null) {
                calculationLog.setOutboundTime(new Date());
                calculationLog.setOutboundXml(data != null ? data.getOutputXml() : null);
                calculationLog.setOutboundText(data != null ? data.getTextResult() : null);
                calculationLog.setErrorCode(data != null ? data.getErrorCode() : CalcError.NO_ERROR.getCode());
                calculationLogService.save(calculationLog);
            }

        }

        logger.debug("calchandler process finished");

        return data;
    }

    private boolean checkTags(String text, String start, String end) {

        try {
            int indexStart = text.toLowerCase().indexOf(start.toLowerCase());
            int indexEnd = text.toLowerCase().indexOf(end.toLowerCase());

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
