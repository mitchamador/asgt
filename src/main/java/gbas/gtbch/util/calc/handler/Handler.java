package gbas.gtbch.util.calc.handler;

import gbas.gtbch.util.calc.handler.impl.*;

/**
 * List of {@link ObjectHandler} implementations
 */
public enum Handler {
    NAKL(new NaklHandler()),
    VO(new VoHandler()),
    FDU92(new Fdu92Handler()),
    GU46(new Gu46Handler()),
    GU23(new Gu23Handler()),
    KEU16(new Keu16Handler()),
    ;

    private ObjectHandler objectHandler;

    Handler(ObjectHandler objectHandler) {
        this.objectHandler = objectHandler;
    }

    public static ObjectHandler getHandler(String xml) {
        for (Handler handler : values()) {
            if (handler.objectHandler.check(xml)) {
                return handler.objectHandler;
            }
        }
        return null;
    }

    public static boolean checkTags(String text, String start, String end) {

        try {
            int indexStart = text.toLowerCase().indexOf(start.toLowerCase());
            int indexEnd = text.toLowerCase().indexOf(end.toLowerCase());

            if (indexStart != -1 && indexEnd != -1 && indexEnd > indexStart) {
                return true;
            }
        } catch (Exception ignored) {
        }

        return false;
    }
}
