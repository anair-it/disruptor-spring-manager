package org.anair.disruptor.publisher;

import org.anair.disruptor.DisruptorConfig;

/**
 * Ring buffer publisher should use this interface.
 * 
 * @author Anoop Nair
 *
 * @param <T>
 */
public interface EventPublisher<T> {

	/**
	 * Publish to the ring buffer. Use a Event Translator.
	 * 
	 * @param t
	 */
	void publish(T t);
	
	/**
	 * Set the DisruptorConfig spring bean.
	 * 
	 * @param disruptorConfig
	 */
	void setDisruptorConfig(DisruptorConfig<T> disruptorConfig);
}
