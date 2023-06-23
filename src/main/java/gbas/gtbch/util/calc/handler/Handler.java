package gbas.gtbch.util.calc.handler;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * List of {@link ObjectHandler} implementations
 */
@Component
public class Handler {

    private final List<ObjectHandler> handlerList = new ArrayList<>();

    public Handler() {
    }

    /**
     * initialization of {@link Handler#handlerList} with handlers (all classes implements {@link ObjectHandler})
     * @param applicationContext
     */
    public void init(ConfigurableApplicationContext applicationContext) {
        handlerList.addAll(applicationContext.getBeansOfType(ObjectHandler.class).values());
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
