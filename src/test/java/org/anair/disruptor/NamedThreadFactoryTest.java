package org.anair.disruptor;

import static org.junit.Assert.*;

import org.junit.Test;

public class NamedThreadFactoryTest {
	
	private NamedThreadFactory namedThreadFactory;
	
	@Test
	public void test_DisruptorThreadNaming() {
		final String THREAD_NAME = "thread name";
		namedThreadFactory = new NamedThreadFactory(THREAD_NAME);
		Thread thread = namedThreadFactory.newThread(new Runnable() {
			@Override
			public void run() {}
		});
		
		assertEquals(THREAD_NAME, thread.getName());
		assertTrue(thread.isDaemon());
	}

}
