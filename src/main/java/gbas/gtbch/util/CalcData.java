package gbas.gtbch.util;

import gbas.gtbch.sapod.model.CalculationLog;

/**
 * class for input and output data
 */
public class CalcData {

    private int error;

    public CalcData(String inputXml, CalculationLog calculationLog) {
        this.inputXml = inputXml;
        this.calculationLog = calculationLog;
    }

    /**
     * input xml
     */
    private final String inputXml;

    /**
     * output xml
     */
    private String outputXml;

    /**
     * output text result
     */
    private String textResult;

    /**
     * CalculationLog
     * @see CalculationLog
     */
    private final CalculationLog calculationLog;


    public String getInputXml() {
        return inputXml;
    }

    public String getOutputXml() {
        return outputXml;
    }

    public void setOutputXml(String outputXml) {
        this.outputXml = outputXml;
    }

    public String getTextResult() {
        return textResult;
    }

    public void setTextResult(String textResult) {
        this.textResult = textResult;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getErrorCode() {
        return error;
    }

    public CalculationLog getCalculationLog() {
        return calculationLog;
    }

}
