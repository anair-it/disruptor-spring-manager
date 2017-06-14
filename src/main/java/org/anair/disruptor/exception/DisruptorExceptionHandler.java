package org.anair.disruptor.exception;


import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.ExceptionHandler;

/**
 * Disruptor exception handler.
 * 
 * @see ExceptionHandler
 * 
 * @author Anoop Nair
 *
 */
public class DisruptorExceptionHandler<T> implements ExceptionHandler<T> {
	private static final Logger LOG = LoggerFactory.getLogger(DisruptorExceptionHandler.class);
	protected String errorPrefix = "Ringbuffer Disruptor failed for thread: ";

	
	public DisruptorExceptionHandler(String threadName) {
		this.errorPrefix+= threadName + " | ";
	}

	@Override
	public void handleEventException(Throwable ex, long sequence, T event) {
		StringJoiner str = new StringJoiner(" | ");
		str.add(errorPrefix);
		str.add("Sequence: ");
		str.add(sequence+"");
		str.add("Event: ");
		str.add(event.toString());
		str.add("Exception message: ");
		str.add(ex.getMessage());
		LOG.error(str.toString(), ex);
		
		throw new RuntimeException(ex);
	}

	@Override
	public void handleOnStartException(Throwable ex) {
		LOG.error(errorPrefix + ex.getMessage(), ex);
	}

	@Override
	public void handleOnShutdownException(Throwable ex) {
		LOG.error(errorPrefix + ex.getMessage(), ex);
	}
}
