package org.anair.disruptor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.ProducerType;

@SuppressWarnings({"rawtypes","unchecked"})
public class BaseDisruptorConfiguratorTest {
	private BaseDisruptorConfig disruptorConfigurator;
	private static final String THREAD_NAME = "namo";
	private static final int ringBufferSize = 16;
	
	@Before
	public void setup(){
		disruptorConfigurator = new BaseDisruptorConfig() {

			@Override
			public void disruptorExceptionHandler() {}

			@Override
			public void disruptorEventHandler() {}

			@Override
			public void publish(EventTranslator eventTranslator) {}
		};
		
		disruptorConfigurator.setThreadName(THREAD_NAME);
		disruptorConfigurator.setProducerType(ProducerType.SINGLE);
		disruptorConfigurator.setRingBufferSize(ringBufferSize);
		disruptorConfigurator.setWaitStrategyType(WaitStrategyType.BLOCKING);
		disruptorConfigurator.setEventFactory(new SampleEventFactory());
		
		disruptorConfigurator.init();
	}
	
	@After
	public void teardown(){
		disruptorConfigurator.controlledShutdown();
	}
	
	@Test
	public void test_RingBuffer_CurrentLocation() {
		assertEquals(-1, disruptorConfigurator.getCurrentLocation());
	}

	@Test
	public void test_RingBuffer_RemainingCapacity() {
		assertEquals(ringBufferSize, disruptorConfigurator.getRemainingCapacity());
	}

	@Test
	public void test_RingBuffer_Reset() {
		disruptorConfigurator.resetRingbuffer(0);
	}
	
	@Test
	public void test_RingBuffer_Publisg() {
		int seq = 2;
		disruptorConfigurator.publishToRingbuffer(seq);
		assertEquals(seq, disruptorConfigurator.getCurrentLocation());
	}
	
	private class SampleEventFactory implements EventFactory<String>{

		@Override
		public String newInstance() {
			return new String();
		}
		
	}

}
