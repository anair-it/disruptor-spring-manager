package org.anair.disruptor;

import java.util.concurrent.ThreadFactory;

/**
 * Naming a daemon thread. The name will appear in logs.
 * 
 * @author Anoop Nair
 *
 */
public class NamedThreadFactory implements ThreadFactory {

	private String threadName;
	
	public NamedThreadFactory(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName(threadName);
		t.setDaemon(true);
		return t;
	}

}
