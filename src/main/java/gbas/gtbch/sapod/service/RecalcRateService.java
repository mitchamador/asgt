package gbas.gtbch.sapod.service;

import gbas.gtbch.sapod.model.RecalcRate;

import java.util.List;

public interface RecalcRateService {

    /**
     *
     * @return
     */
    List<RecalcRate> getRecalcRates();

    /**
     *
     * @param id
     * @return
     */
    RecalcRate findRecalcRateById(int id);

    /**
     *
     * @param recalcRate
     * @return
     */
    RecalcRate save(RecalcRate recalcRate);

    /**
     *
     * @param id
     */
    void delete(int id);
}