package gbas.gtbch.config.jndi;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Websphere Application Server's {@link ObjectFactory} for resource environment entries. Create json from key-value pairs.
 */
public class WASJndiObjectFactory implements ObjectFactory {

    @Override
    public Object getObjectInstance(Object object, Name name, Context context, Hashtable<?,?> env) throws Exception {
        Map<String, Object> map = new HashMap<>();

        for (Enumeration e = ((Reference) object).getAll(); e.hasMoreElements();) {
            RefAddr addr = (RefAddr) e.nextElement();
            String key = addr.getType();
            Object value = addr.getContent();

            if (key != null && !key.isEmpty()) {
                map.put(key, value);
            }
        }

        return new ObjectMapper().writeValueAsString(map);
    }
}
