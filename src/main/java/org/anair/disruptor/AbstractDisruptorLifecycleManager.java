package org.anair.disruptor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * Manage lifecycle of a disruptor bean. 
 * 
 * @author Anoop Nair
 *
 * @param <T>
 */
public abstract class AbstractDisruptorLifecycleManager<T> implements DisruptorLifecycle<T>{

	private static final Logger LOG = LoggerFactory.getLogger(AbstractDisruptorLifecycleManager.class);
	private Disruptor<T> disruptor;
	private ThreadFactory threadFactory;
	private String threadName;
	
	public abstract void init();
	
	@Override
	public void controlledShutdown() {
		LOG.debug("Disruptor {} is going to shutdown.", getThreadName());
		disruptor.shutdown();
		LOG.info("Disruptor {} has shutdown.", getThreadName());
	}

	@Override
	public void halt() {
		LOG.debug("Disruptor {} is going to shutdown.", getThreadName());
		disruptor.halt();
		LOG.info("Disruptor {} has halted.", getThreadName());
	}

	@Override
	public void awaitAndShutdown(long time) {
		try {
			LOG.debug("Disruptor {} is going to shutdown in {} {}", getThreadName(), time, TimeUnit.SECONDS);
			disruptor.shutdown(time, TimeUnit.SECONDS);
			LOG.info("Disruptor {} has shutdown after {} {}.", getThreadName(), time, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			LOG.error(e.getMessage(),e);
		}
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	public String getThreadName() {
		return threadName;
	}
	
	protected Disruptor<T> getDisruptor() {
		return disruptor;
	}

	protected void setDisruptor(Disruptor<T> disruptor) {
		this.disruptor = disruptor;
	}

	protected ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	protected void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}


}
