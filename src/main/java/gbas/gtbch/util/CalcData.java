package gbas.gtbch.util;

import gbas.gtbch.sapod.model.CalculationLog;

/**
 * class for input and output data
 */
public class CalcData {

    private int errorCode;

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

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public CalculationLog getCalculationLog() {
        return calculationLog;
    }

}
