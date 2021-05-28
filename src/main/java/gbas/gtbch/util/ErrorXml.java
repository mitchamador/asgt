package gbas.gtbch.util;

import gbas.gtbch.util.calc.CalcError;

/**
 * create xml with error
 */
public class ErrorXml {

    private static String newLine = System.getProperty("line.separator");

    private ErrorXml() {
    }

    public static String getErrorXml(int errorCode) {
        return getErrorXml(CalcError.getCalcError(errorCode));
    }

    public static String getErrorXml(CalcError error) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newLine +
                "<error>" + newLine +
                "    <text>" + error.getName() + "</text>" + newLine +
                "</error>" + newLine;
    }
}
