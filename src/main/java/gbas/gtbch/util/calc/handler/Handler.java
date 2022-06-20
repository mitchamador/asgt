package gbas.gtbch.util.calc.handler;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * List of {@link ObjectHandler} implementations
 */
@Component
public class Handler {

    private final List<ObjectHandler> handlerList;

    public Handler(ObjectHandler naklHandler, ObjectHandler voHandler, ObjectHandler fdu92Handler, ObjectHandler gu46Handler, ObjectHandler gu23Handler, ObjectHandler keu16Handler) {
        handlerList = new ArrayList<>();
        handlerList.add(naklHandler);
        handlerList.add(voHandler);
        handlerList.add(fdu92Handler);
        handlerList.add(gu46Handler);
        handlerList.add(gu23Handler);
        handlerList.add(keu16Handler);
    }

    public ObjectHandler getObjectHandler(String xml) {
        for (ObjectHandler objectHandler : handlerList) {
            if (objectHandler.check(xml)) {
                return objectHandler;
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
