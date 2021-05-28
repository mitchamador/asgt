package gbas.gtbch.util.calc.handler;

import gbas.gtbch.util.calc.CalcData;

import java.sql.Connection;

/**
 * Interface for convertation and calculation of object
 */
public interface ObjectHandler {

    boolean check(String xml);

    void calc(CalcData data, Connection connection) throws Exception;

}
