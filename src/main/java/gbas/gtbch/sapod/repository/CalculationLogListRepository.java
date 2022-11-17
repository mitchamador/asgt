package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.CalculationLog;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CalculationLogListRepository {
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
     * @param keepDateNakl
     * @param keepDateOther
     * @param maxRows
     * @return
     */
    int deleteRows(LocalDate keepDateNakl, LocalDate keepDateOther, int maxRows);
}
