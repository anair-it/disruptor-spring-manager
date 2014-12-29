package org.anair.disruptor.exception;

import org.junit.Test;

public class DisruptorExceptionHandlerTest {
	
	private DisruptorExceptionHandler exHandler;
	
	@Test(expected=RuntimeException.class)
	public void test_handleEventException_throw_RuntimeException() {
		exHandler = new DisruptorExceptionHandler("threadName");
		exHandler.handleEventException(new NullPointerException("disruptor error test"), 10, new String("disruptor error test"));
	}
	
	@Test
	public void test_handleStartupException() {
		exHandler = new DisruptorExceptionHandler("threadName");
		exHandler.handleOnStartException(new NullPointerException("disruptor startup error"));
	}
	
	@Test
	public void test_handleShutdownException() {
		exHandler = new DisruptorExceptionHandler("threadName");
		exHandler.handleOnShutdownException(new NullPointerException("disruptor shutdown error"));
	}

}
