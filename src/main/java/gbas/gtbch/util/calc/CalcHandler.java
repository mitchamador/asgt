package gbas.gtbch.util.calc;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.service.CalculationLogService;
import gbas.gtbch.util.ErrorXml;
import gbas.gtbch.util.calc.handler.Handler;
import gbas.gtbch.util.calc.handler.ObjectHandler;
import gbas.tvk.nsi.cash.Func;
import gbas.tvk.service.SQLUtils;
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
                ObjectHandler objectHandler = Handler.getHandler(data.getInputXml());
                if (objectHandler != null) {
                    objectHandler.calc(data, connection);
                }
                if (data.getCalculationObject() == null) {
                    data.setTextResult(String.format("Неверный объект расчета: \"%s\"", data.getInputXml()));
                    data.setErrorCode(CalcError.UNKNOWN_OBJECT.getCode());
                }
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
