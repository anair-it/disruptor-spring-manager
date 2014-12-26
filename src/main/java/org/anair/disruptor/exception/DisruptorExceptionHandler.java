package org.anair.disruptor.exception;

import org.apache.log4j.Logger;

import com.lmax.disruptor.ExceptionHandler;

/**
 * Disruptor exception handler.
 * 
 * @see ExceptionHandler
 * 
 * @author Anoop Nair
 *
 */
public class DisruptorExceptionHandler implements ExceptionHandler {
	private static final Logger LOG = Logger.getLogger(DisruptorExceptionHandler.class);
	String errorPrefix = "Ringbuffer Disruptor failed for thread: ";

	
	public DisruptorExceptionHandler(String threadName) {
		this.errorPrefix+= threadName;
	}

	@Override
	public void handleEventException(Throwable ex, long sequence, Object event) {
		StringBuilder str = new StringBuilder(errorPrefix);
		str.append("Sequence: ");
		str.append(sequence);
		str.append(" | ");
		str.append("Event: ");
		str.append(event);
		str.append(" | ");
		str.append("Exception message: ");
		str.append(ex.getMessage());
		LOG.error(str.toString(), ex);
	}

	@Override
	public void handleOnStartException(Throwable ex) {
		LOG.fatal(errorPrefix + ex.getMessage(), ex);
	}

	@Override
	public void handleOnShutdownException(Throwable ex) {
		LOG.error(errorPrefix + ex.getMessage(), ex);
	}
}
