package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CalculationLog;
import gbas.gtbch.sapod.model.Currency;

import java.util.Date;
import java.util.List;

/**
 *
 */
public interface CalculationLogService {

    /**
     *
     * @param type
     * @param station
     * @param startDate
     * @param endDate
     * @return
     */
    List<CalculationLog> find(CalculationLog.Type type, String station, Date startDate, Date endDate);

    /**
     * save {@link CalculationLog} object to database
     * @param log
     * @return
     */
    CalculationLog save(CalculationLog log);

    /**
     * get {@link CalculationLog} object from database
     * @param id identity
     * @return
     */
    CalculationLog findById(int id);
}