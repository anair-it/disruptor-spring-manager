package org.anair.disruptor;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;

@SuppressWarnings({"rawtypes","unchecked"})
public class AbstractDisruptorLifecycleManagerTest {
	
	private AbstractDisruptorLifecycleManager<String> disruptorLifecycleManager;
	private Disruptor mockDisruptor;
	private ThreadFactory mockThreadFactory;
	private static final String THREAD_NAME = "namo";
	
	@Before
	public void setup(){
		mockDisruptor = createStrictMock(Disruptor.class);
		mockThreadFactory = createStrictMock(ThreadFactory.class);
		disruptorLifecycleManager = new AbstractDisruptorLifecycleManager<String>() {
			
			@Override
			public void init() {
			}
		};
		
		disruptorLifecycleManager.setDisruptor(mockDisruptor);
		disruptorLifecycleManager.setThreadFactory(mockThreadFactory);
		disruptorLifecycleManager.setThreadName(THREAD_NAME);
		
		assertNotNull(disruptorLifecycleManager.getDisruptor());
		assertNotNull(disruptorLifecycleManager.getThreadFactory());
		assertNotNull(disruptorLifecycleManager.getThreadName());
	}
	
	@Test
	public void test_controlledShutdown() {
		mockDisruptor.shutdown();
		replay(mockDisruptor);
		
		disruptorLifecycleManager.controlledShutdown();
		verify(mockDisruptor);
	}
	
	@Test
	public void test_halt() {
		mockDisruptor.halt();
		
		replay(mockDisruptor);
		
		disruptorLifecycleManager.halt();
		verify(mockDisruptor);
	}
	
	@Test
	public void test_awaitAndShutdown() throws TimeoutException, InterruptedException {
		mockDisruptor.shutdown(1, TimeUnit.SECONDS);
		
		replay(mockDisruptor);
		
		disruptorLifecycleManager.awaitAndShutdown(1);
		verify(mockDisruptor);
	}
	
	@Test
	public void test_awaitAndShutdown_InterruptedException() throws TimeoutException, InterruptedException {
		mockDisruptor.shutdown(1, TimeUnit.SECONDS);
		
		replay(mockDisruptor);
		
		disruptorLifecycleManager.awaitAndShutdown(1);
		verify(mockDisruptor);
	}
	
	@Test
	public void test_awaitAndShutdown_TimeoutException() throws TimeoutException, InterruptedException {
		mockDisruptor.shutdown(1, TimeUnit.SECONDS);
		expectLastCall().andThrow(TimeoutException.INSTANCE);
		replay(mockDisruptor);
		
		disruptorLifecycleManager.awaitAndShutdown(1);
		verify(mockDisruptor);
	}

}
