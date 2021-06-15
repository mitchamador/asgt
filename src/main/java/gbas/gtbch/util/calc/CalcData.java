package gbas.gtbch.util.calc;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
     * name of file
     */
    private String fileName;

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

    public Object getCalculationObject() {
        return calculationObject;
    }

    public void setCalculationObject(Object calculationObject) {
        this.calculationObject = calculationObject;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
