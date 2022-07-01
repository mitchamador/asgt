package gbas.gtbch.util.calc;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.service.CalculationLogService;
import gbas.gtbch.util.ErrorXml;
import gbas.gtbch.util.Syncronizer;
import gbas.gtbch.util.calc.handler.Handler;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.nsi.cash.Func;
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

    private final Handler handler;

    /**
     *
     */
    private CalculationLogService calculationLogService;

    private final Syncronizer syncronizer;

    public CalcHandler(@Qualifier("sapodDataSource") DataSource dataSource, Handler handler, CalculationLogService calculationLogService, Syncronizer syncronizer) throws SQLException {
        this.dataSource = dataSource;
        this.handler = handler;
        this.calculationLogService = calculationLogService;
        this.syncronizer = syncronizer;
    }

    /**
     * perform calculation
     * @param data
     * @return
     */
    public CalcData calc(CalcData data) {

        assert data != null;

        logger.debug("calchandler process started...");

        CalculationLog calculationLog = data.getCalculationLog();

        try {

            if (calculationLog != null) {
                calculationLog.setInboundTime(new Date());
                calculationLog.setInboundXml(data.getInputXml());
                calculationLog.setType(CalculationLog.Type.UNKNOWN);

                try {
                    calculationLog = calculationLogService.save(calculationLog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Func.isEmpty(data.getInputXml())) {
                data.setTextResult("Пустой объект");
                data.setErrorCode(CalcError.NULL.getCode());
            } else {
                ObjectHandler objectHandler = handler.getObjectHandler(data.getInputXml());
                if (objectHandler != null) {
                    boolean syncronizerAcquired = false;
                    try {
                        do {
                            if (syncronizerAcquired = syncronizer.acquire()) {
                                try (Connection connection = dataSource.getConnection()) {
                                    objectHandler.calc(data, connection);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (syncronizer.isRunning()) {
                                    data.setErrorCode(CalcError.SYNC_RUNNING.getCode());
                                    data.setTextResult(CalcError.getCalcError(data.getErrorCode()).getName());
                                }
                            }
                        } while (!syncronizerAcquired && data.getErrorCode() != CalcError.SYNC_RUNNING.getCode());
                    } finally {
                        if (syncronizerAcquired) {
                            syncronizer.release();
                        }
                    }
                }
                if (data.getCalculationObject() == null && data.getErrorCode() == CalcError.NO_ERROR.getCode()) {
                    data.setTextResult(String.format("Неверный объект расчета: \"%s\"", data.getInputXml()));
                    data.setErrorCode(CalcError.UNKNOWN_OBJECT.getCode());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.setTextResult(e.toString());
            data.setErrorCode(CalcError.EXCEPTION.getCode());
        } finally {

            if (Func.isEmpty(data.getTextResult())) {
                data.setErrorCode(CalcError.EMPTY_RESULT.getCode());
            }

            if (Func.isEmpty(data.getOutputXml())) {
                data.setOutputXml(ErrorXml.getErrorXml(data.getErrorCode()));
            }

            if (calculationLog != null) {
                calculationLog.setOutboundTime(new Date());
                calculationLog.setOutboundXml(data.getOutputXml());
                calculationLog.setOutboundText(data.getTextResult());
                calculationLog.setErrorCode(data.getErrorCode());

                try {
                    calculationLogService.save(calculationLog);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                logger.info("calculation log is null");
            }

        }

        logger.debug("calchandler process finished");

        return data;
    }

}
