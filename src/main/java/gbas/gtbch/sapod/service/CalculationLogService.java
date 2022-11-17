package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.CalculationLog;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    /**
     *
     * @param params
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    List<CalculationLog> getList(Map<String, String> params, Date dateBegin, Date dateEnd);

    /**
     *
     *
     * @param keepDateNakl
     * @param keepDateOther
     * @param maxRows
     * @return
     */
    int deleteRows(LocalDate keepDateNakl, LocalDate keepDateOther, int maxRows);

}
