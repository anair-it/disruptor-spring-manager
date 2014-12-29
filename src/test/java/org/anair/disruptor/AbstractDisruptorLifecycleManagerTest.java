package org.anair.disruptor;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;

@SuppressWarnings({"rawtypes","unchecked"})
public class AbstractDisruptorLifecycleManagerTest {
	
	private AbstractDisruptorLifecycleManager<String> disruptorLifecycleManager;
	private Disruptor mockDisruptor;
	private ExecutorService mockExecutor;
	private static final String THREAD_NAME = "namo";
	
	@Before
	public void setup(){
		mockDisruptor = createStrictMock(Disruptor.class);
		mockExecutor = createStrictMock(ExecutorService.class);
		
		disruptorLifecycleManager = new AbstractDisruptorLifecycleManager<String>() {
			
			@Override
			public void init() {
			}
		};
		
		disruptorLifecycleManager.setDisruptor(mockDisruptor);
		disruptorLifecycleManager.setExecutor(mockExecutor);
		disruptorLifecycleManager.setThreadName(THREAD_NAME);
		
		assertNotNull(disruptorLifecycleManager.getDisruptor());
		assertNotNull(disruptorLifecycleManager.getExecutor());
		assertNotNull(disruptorLifecycleManager.getThreadName());
	}
	
	@Test
	public void test_controlledShutdown() {
		mockDisruptor.shutdown();
		mockExecutor.shutdown();
		
		replay(mockDisruptor, mockExecutor);
		
		disruptorLifecycleManager.controlledShutdown();
		verify(mockDisruptor, mockExecutor);
	}
	
	@Test
	public void test_halt() {
		expect(mockExecutor.shutdownNow()).andReturn(new ArrayList<Runnable>());
		mockDisruptor.halt();
		
		replay(mockDisruptor, mockExecutor);
		
		disruptorLifecycleManager.halt();
		verify(mockDisruptor, mockExecutor);
	}
	
	@Test
	public void test_awaitAndShutdown() throws TimeoutException, InterruptedException {
		mockDisruptor.shutdown(1, TimeUnit.SECONDS);
		expect(mockExecutor.awaitTermination(1, TimeUnit.SECONDS)).andReturn(true);
		
		replay(mockDisruptor, mockExecutor);
		
		disruptorLifecycleManager.awaitAndShutdown(1);
		verify(mockDisruptor, mockExecutor);
	}

}
