package gbas.gtbch.util;

import gbas.gtbch.sapod.model.CalculationLog;

/**
 * class for input and output data
 */
public class CalcData {

    private int error;

    public CalcData(String inputXml, CalculationLog.Source source) {
        this.inputXml = inputXml;
        this.source = source;
    }

    /**
     * input xml
     */
    private String inputXml;

    /**
     * output xml
     */
    private String outputXml;

    /**
     * output text result
     */
    private String textResult;

    /**
     * source type
     * @see CalculationLog.Source
     */
    private CalculationLog.Source source;


    public String getInputXml() {
        return inputXml;
    }

    public void setInputXml(String inputXml) {
        this.inputXml = inputXml;
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

    public CalculationLog.Source getSource() {
        return source;
    }

    public void setSource(CalculationLog.Source source) {
        this.source = source;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getErrorCode() {
        return error;
    }
}
