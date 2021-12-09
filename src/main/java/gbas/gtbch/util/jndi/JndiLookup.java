package gbas.gtbch.util.jndi;

import org.springframework.jndi.JndiLocatorSupport;

import javax.naming.NamingException;

public class JndiLookup<T> extends JndiLocatorSupport {

    private Class<T> type;

    public JndiLookup(Class<T> type) {
        this.type = type;
        setResourceRef(true);
    }

    public T getResource(String name) throws NamingException {
        return lookup(name, type);
    }
}
