package org.anair.disruptor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Naming a daemon thread. The name will appear in logs.
 * 
 * @author Anoop Nair
 *
 */
public class NamedThreadFactory implements ThreadFactory {
	private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(0);
	private String threadName;
	
	public NamedThreadFactory(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName(getThreadName());
		t.setDaemon(true);
		return t;
	}
	
	private String getThreadName(){
		return threadName+"-"+THREAD_COUNTER.incrementAndGet();
	}

}
