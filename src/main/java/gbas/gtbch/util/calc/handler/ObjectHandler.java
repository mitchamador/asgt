package gbas.gtbch.util.calc.handler;

import gbas.gtbch.util.calc.GtCalcData;

import java.sql.Connection;

/**
 * Interface for onject's conversion and calculation
 */
public interface ObjectHandler {

    boolean check(String xml);

    void calc(GtCalcData data, Connection connection) throws Exception;

}
