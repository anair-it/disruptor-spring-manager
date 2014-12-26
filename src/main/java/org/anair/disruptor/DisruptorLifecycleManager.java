package org.anair.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * Manage lifecycle of a disruptor bean. 
 * 
 * @author Anoop Nair
 *
 * @param <T>
 */
public abstract class DisruptorLifecycleManager<T> implements DisruptorLifecycle<T>{

	private static final Logger LOG = Logger.getLogger(DisruptorLifecycleManager.class);
	private Disruptor<T> disruptor;
	private ExecutorService executor;
	private String threadName;
	
	@Override
	public void init(){
		Validate.notNull(getThreadName());
		createThreadExecutor();
		configureDisruptor();
		disruptor.start();
	}
	
	protected abstract void configureDisruptor();

	@Override
	public abstract void publish(EventTranslator<T> eventTranslator);
	
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
		LOG.info("Disruptor and executor '" + getThreadName() + "' has shutdown.");
	}

	@Override
	public void awaitAndShutdown(long time) {
		try {
			LOG.debug("Disruptor and executor '" + getThreadName() + "' is going to shutdown in " + time + TimeUnit.SECONDS);
			disruptor.shutdown(time, TimeUnit.SECONDS);
			executor.awaitTermination(time, TimeUnit.SECONDS);
			LOG.debug("Disruptor and executor '" + getThreadName() + "' has shutdown.");
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (TimeoutException e) {
			LOG.error(e);
		}
	}

	private void createThreadExecutor() {
		this.executor = Executors.newCachedThreadPool(new NamedThreadFactory(getThreadName()));
		LOG.debug("Created a cache thread pool based disruptor with name: " + getThreadName());
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
