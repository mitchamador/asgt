package gbas.gtbch.util;

/**
 * class for input and output data
 */
public class CalcData {

    public CalcData(String inputXml) {
        this.inputXml = inputXml;
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
}
