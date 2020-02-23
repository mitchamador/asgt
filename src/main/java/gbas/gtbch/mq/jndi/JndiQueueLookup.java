/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gbas.gtbch.mq.jndi;

import org.springframework.jndi.JndiLocatorSupport;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.NamingException;

/**
 * JNDI-based {@link ConnectionFactory} implementation.
 *
 * <p>For specific JNDI configuration, it is recommended to configure
 * the "jndiEnvironment"/"jndiTemplate" properties.
 *
 */
public class JndiQueueLookup extends JndiLocatorSupport implements QueueLookup {

	public JndiQueueLookup() {
		setResourceRef(true);
	}

	@Override
	public Queue getQueue(String queueName) throws NamingException {
		return lookup(queueName, Queue.class);
	}

}

