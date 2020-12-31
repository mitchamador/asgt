package gbas.gtbch.util;

import javax.persistence.Column;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * truncate string based on 'length' field of @Column annotation
 */
public class JpaTruncator {
    public static String truncate(String string) {

        if (string == null) return null;

        try {
            final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            final Class<?> clazz = Class.forName(stackTraceElement.getClassName());
            final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            final String methodName = stackTraceElement.getMethodName();
            final PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (final PropertyDescriptor descriptor : props) {
                if (descriptor.getWriteMethod() != null) {
                    if (methodName.equals(descriptor.getWriteMethod().getName())) {
                        final Column annotation = clazz.getDeclaredField(descriptor.getDisplayName()).getAnnotation(Column.class);
                        if (annotation != null) {
                            final int size = annotation.length();
                            if (string.length() > size) {
                                string = string.substring(0, size);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (final IntrospectionException ignored) {
        } catch (final Exception ignored) {
        }

        return string;
    }
}
