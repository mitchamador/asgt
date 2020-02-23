package gbas.gtbch.mq.jndi;

import javax.jms.Queue;
import javax.naming.NamingException;

interface QueueLookup {
	Queue getQueue(String queueName) throws NamingException;
}
