package org.anair.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

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

	private static final Logger LOG = Logger.getLogger(AbstractDisruptorLifecycleManager.class);
	private Disruptor<T> disruptor;
	private ExecutorService executor;
	private String threadName;
	
	public abstract void init();
	
	@Override
	public void controlledShutdown() {
		LOG.debug("Disruptor and executor '" + getThreadName() + "' is going to shutdown.");
		disruptor.shutdown();
		executor.shutdown();
		LOG.info("Disruptor and executor '" + getThreadName() + "' has shutdown.");
	}

	@Override
	public void halt() {
		LOG.debug("Disruptor and executor '" + getThreadName() + "' is going to shutdown.");
		executor.shutdownNow();
		disruptor.halt();
		LOG.info("Disruptor and executor '" + getThreadName() + "' has halted.");
	}

	@Override
	public void awaitAndShutdown(long time) {
		try {
			LOG.debug("Disruptor and executor '" + getThreadName() + "' is going to shutdown in " + time + TimeUnit.SECONDS);
			disruptor.shutdown(time, TimeUnit.SECONDS);
			executor.awaitTermination(time, TimeUnit.SECONDS);
			LOG.info("Disruptor and executor '" + getThreadName() + "' has shutdown after " + time + " seconds.");
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (TimeoutException e) {
			LOG.error(e);
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

	protected ExecutorService getExecutor() {
		return executor;
	}

	protected void setDisruptor(Disruptor<T> disruptor) {
		this.disruptor = disruptor;
	}

	protected void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

}
