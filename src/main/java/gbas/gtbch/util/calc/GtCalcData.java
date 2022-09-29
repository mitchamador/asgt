package gbas.gtbch.util.calc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gbas.gtbch.sapod.model.CalculationLog;
import gbas.tvk.service.asgt.CalcData;

/**
 * class for input and output data
 */
public class GtCalcData extends CalcData {

    public GtCalcData(String inputXml, CalculationLog calculationLog) {
        this.calculationLog = calculationLog;
        this.inputXml = inputXml;
    }

    /**
     * CalculationLog
     * @see CalculationLog
     */
    @JsonIgnore
    private final CalculationLog calculationLog;

    /**
     * calculation object
     */
    @JsonIgnore
    private Object calculationObject;

    public CalculationLog getCalculationLog() {
        return calculationLog;
    }

    public Object getCalculationObject() {
        return calculationObject;
    }

    public void setCalculationObject(Object calculationObject) {
        this.calculationObject = calculationObject;
    }
}
