package gbas.gtbch.mq.jndi;

import javax.jms.ConnectionFactory;
import javax.naming.NamingException;

interface ConnectionFactoryLookup {
	ConnectionFactory getConnectionFactory(String connectionFactoryName) throws NamingException;
}
