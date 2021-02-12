package gbas.gtbch.util;

import gbas.eds.gtbch.fdu92.GtFdu92Converter;
import gbas.eds.gtbch.fdu92.calc.CardCalculator;
import gbas.eds.gtbch.gu23.convert.ConvertXmlGtToGu23Gt;
import gbas.eds.gtbch.gu23.obj.Gu23ObjGt;
import gbas.eds.gtbch.gu23.parser.GtGu23Xml;
import gbas.eds.gtbch.gu23.server.PayGu23;
import gbas.eds.gtbch.invoice.ConvertXmlToVagonOtprTransit;
import gbas.eds.rw.isc.typeexchange.Tm;
import gbas.eds.soap.obj.DocEP;
import gbas.eds.soap.obj.nakl.constants.ConstantsParameters;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.service.CalculationLogService;
import gbas.tvk.card.service.ContractCardData;
import gbas.tvk.nsi.cash.Func;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 */
@Component
public class CalcHandler {

    private final static Logger logger = LoggerFactory.getLogger(CalcHandler.class.getName());

    /**
     *
     */
    private final DataSource dataSource;

    /**
     *
     */
    private CalculationLogService calculationLogService;

    public CalcHandler(@Qualifier("sapodDataSource") DataSource dataSource, CalculationLogService calculationLogService) throws SQLException {
        this.dataSource = dataSource;
        this.calculationLogService = calculationLogService;
    }

    /**
     * perform calculation
     * @param data
     * @return
     */
    public CalcData calc(CalcData data) {

        assert data != null;

        logger.debug("calchandler process started...");

        Connection connection = null;

        CalculationLog calculationLog = data.getCalculationLog();

        try {

            connection = dataSource.getConnection();

            if (calculationLog != null) {
                calculationLog.setInboundTime(new Date());
                calculationLog.setInboundXml(data.getInputXml());
                calculationLog.setType(CalculationLog.Type.UNKNOWN);

                calculationLog = calculationLogService.save(calculationLog);
            }


            Object obj = null;

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"nakl\"", "</table>")) {
                if (calculationLog != null) {
                    calculationLog.setType(CalculationLog.Type.NAKL);
                }
                ConvertXmlToVagonOtprTransit convertXmlToVagonOtprTransit = ConvertXmlToVagonOtprTransit.createConvertXmlToVOT(connection);
                String string = convertXmlToVagonOtprTransit.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);
                if (string != null) {
                    obj = convertXmlToVagonOtprTransit.getObject(VagonOtpr.OPER_DEPARTURE);
                }
            }

            if (obj == null && (checkTags(data.getInputXml(), "<gbas.tvk.payment.CalcPlataData>", "</gbas.tvk.payment.CalcPlataData>")
                    || checkTags(data.getInputXml(), "<gbas.tvk.otpravka.object.VagonOtprTransit>", "</gbas.tvk.otpravka.object.VagonOtprTransit>"))) {
                if (calculationLog != null) {
                    calculationLog.setType(CalculationLog.Type.VOT);
                }
                obj = GZipUtils.xml2Object(data.getInputXml());
            }

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"gu46\"", "</table>")) {
                if (calculationLog != null) {
                    calculationLog.setType(CalculationLog.Type.GU46);
                }
                ConvertXmlGtToGu46 docEC = new ConvertXmlGtToGu46(connection);
                String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);

                if(string != null) {
                    obj = docEC.getObject((Tm[]) docEC.getObject());
                }
            }

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"fdu92\"", "</table>")) {
                if (calculationLog != null) {
                    calculationLog.setType(CalculationLog.Type.CARD);
                }
                DocEP docEC = new GtFdu92Converter();
                String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);

                if(string != null) {
                    obj = docEC.getObject();
                }
            }

            if (obj == null && checkTags(data.getInputXml(), "<table name=\"gu23\"", "</table>")) {
                if (calculationLog != null) {
                    calculationLog.setType(CalculationLog.Type.GU23);
                }
                ConvertXmlGtToGu23Gt docEC = new ConvertXmlGtToGu23Gt(connection);
                String string = docEC.parse(data.getInputXml(), ConstantsParameters.VERIFY,
                        ConstantsParameters.NO_SYSTEM, null);

                if (string != null) {
                    obj = docEC.getObject();
                }
            }

            if (obj instanceof CalcPlataData || obj instanceof VagonOtprTransit) {

                CalcPlataData c;
                PayTransportation payTransportation = new PayTransportation(connection);

                if (obj instanceof CalcPlataData) {
                    c = payTransportation.calcPlata((CalcPlataData) obj);
                    c.setXml(GZipUtils.object2Xml(c));
                } else {
                    c = payTransportation.calcPlata((VagonOtprTransit) obj);
                }

                if (c != null) {

                    if (c.vo != null) {
                        if (calculationLog != null) {
                            calculationLog.setNumber(c.vo.numberOtpr);
                            if (c.vo.oper == VagonOtpr.OPER_DEPARTURE) {
                                calculationLog.setStation(c.vo.k_st_otpr);
                            } else if (c.vo.oper == VagonOtpr.OPER_ARRIVAL) {
                                calculationLog.setStation(c.vo.k_st_nazn);
                            }
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

                if (calculationLog != null) {
                    calculationLog.setNumber(vedGu46.numVed);
                    calculationLog.setStation(vedGu46.stationCode);
                }

            } else if (obj instanceof ContractCardData) {
                ContractCardData ccd = (ContractCardData) obj;
                CalcPlataData cp = CardCalculator.calc(connection, ccd);
                data.setTextResult(cp.s);
                data.setOutputXml(cp.getXml());

                if (calculationLog != null) {
                    calculationLog.setNumber(ccd.n_contract);
                    calculationLog.setStation(ccd.code_station);
                }
            } else if (obj instanceof Gu23ObjGt) {
                Gu23ObjGt aktGu23 = new PayGu23(connection).calcGu23((Gu23ObjGt) obj);
                data.setTextResult(aktGu23.toString());
                data.setOutputXml(GtGu23Xml.getXml(aktGu23));

                if (calculationLog != null) {
                    calculationLog.setNumber(aktGu23.getNomAkt());
                    calculationLog.setStation(aktGu23.getStationtc());
                }
            } else if (Func.isEmpty(data.getInputXml())) {
                data.setTextResult("Пустой объект");
                data.setErrorCode(CalcError.NULL.getCode());
            } else {
                data.setTextResult(String.format("Неверный объект расчета: \"%s\"", data.getInputXml()));
                data.setErrorCode(CalcError.UNKNOWN_OBJECT.getCode());
            }


        } catch (Exception e) {
            e.printStackTrace();
            data.setTextResult(e.getMessage());
            data.setErrorCode(CalcError.EXCEPTION.getCode());
        } finally {
            SQLUtils.close(connection);

            if (Func.isEmpty(data.getOutputXml())) {
                data.setOutputXml(ErrorXml.getErrorXml(data.getErrorCode()));
            }

            if (calculationLog != null) {
                calculationLog.setOutboundTime(new Date());
                calculationLog.setOutboundXml(data.getOutputXml());
                calculationLog.setOutboundText(data.getTextResult());
                calculationLog.setErrorCode(data.getErrorCode());
                calculationLogService.save(calculationLog);
            } else {
                logger.info("Calculation log is null");
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

}
