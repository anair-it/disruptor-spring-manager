package org.anair.disruptor;

import com.lmax.disruptor.EventTranslator;

/**
 * Disruptor configuration settings.
 * 
 * @author itaxn01
 *
 * @param <T>
 */
public interface DisruptorConfig<T> {

	void publish(EventTranslator<T> eventTranslator);
	
	void disruptorEventHandler();

	void disruptorExceptionHandler();
}
