package gbas.gtbch.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class for {@link Enumeration}
 */
public class EnumerationUtil {

    /**
     * get {@link Enumeration} as {@link Stream}
     * @param enumeration source enumeration
     * @param <T> enumeration type
     * @return stream
     */
    public static <T> Stream<T> enumerationAsStream(Enumeration<T> enumeration) {
        List<T> list = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list.stream();
    }

}
