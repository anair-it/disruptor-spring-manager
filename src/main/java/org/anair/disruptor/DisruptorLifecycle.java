package org.anair.disruptor;

import com.lmax.disruptor.EventTranslator;

/**
 * This defines a Disruptor lifecycle.
 * 
 * @author Anoop Nair
 *
 * @param <T>
 */
public interface DisruptorLifecycle<T> {

	/**
	 * Create a LMAX Disruptor in a single thread with a specific {@code threadName}. 
	 * <p>
	 * This disruptor is configured using a ProducerType, Wait Strategy and creates a ring buffer with {@code ringBufferSize} slots  
	 * 
	 */
	void init();
	
	/**
	 * Publish an event to the ring buffer using event translator.
	 * 
	 * @param translator
	 */
	void publish(EventTranslator<T> eventTranslator);
	
	/**
	 * Shutdown Disruptor and Executor in a controlled manner after all ring buffer events are processed. 
	 */
	void controlledShutdown();
	
	/**
	 * Halt Disruptor and Executor. Do not wait for ring buffer events to be processed 
	 */
	void halt();
	
	/**
	 * Wait for events to finish for a few seconds and then shutdown
	 * 
	 * @param time
	 */
	void awaitAndShutdown(long time);
	
}
