package gbas.gtbch.sapod.repository;

import gbas.gtbch.sapod.model.CalculationLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CalculationLogListRepository {
    List<CalculationLog> getList(Map<String, String> params, Date dateBegin, Date dateEnd);
}
