package org.anair.disruptor.jmx;


/**
 * Disruptor JMX capabilities.
 * 
 * @author Anoop Nair
 *
 */
public interface JmxDisruptorMBean {

	void controlledShutdown();
	
	void halt();
	
	void awaitAndShutdown(long time);
	
	void resetRingbuffer(long sequence);
	
	void publishToRingbuffer(long sequence);
	
	String getDisruptorConfiguration();
	
	String getEventProcessorGraph();
	
	String getThreadName();
	
	int getTotalCapacity();
	
	String getProducerType();
	
	String getWaitStrategyType();
	
	long getCurrentLocation();
	
	long getRemainingCapacity();
	
}
