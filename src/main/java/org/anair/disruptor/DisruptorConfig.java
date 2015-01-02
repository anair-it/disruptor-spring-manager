package org.anair.disruptor;

import com.lmax.disruptor.EventTranslator;

/**
 * Disruptor configuration settings.
 * 
 * @author Anoop Nair
 *
 * @param <T>
 */
public interface DisruptorConfig<T> {

	/**
	 * Publish an event to the ring buffer using event translator.
	 * 
	 * @param translator
	 */
	void publish(EventTranslator<T> eventTranslator);
	
	/**
	 * Design a Event Processor/Consumer definition. 
	 */
	void disruptorEventHandler();

	/**
	 * Handle Disruptor exceptions 
	 */	
	void disruptorExceptionHandler();
}
