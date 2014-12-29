package org.anair.disruptor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;

@SuppressWarnings({"rawtypes","unchecked"})
public class DisruptorConfigTest {
	
	private DisruptorConfig disruptorConfig;
	private static final String THREAD_NAME = "namo";
	private static final int ringBufferSize = 16;
	
	@Before
	public void setup(){
		disruptorConfig = new DisruptorConfig();
		disruptorConfig.setRingBufferSize(ringBufferSize);
		disruptorConfig.setThreadName(THREAD_NAME);
		disruptorConfig.setEventFactory(new SampleEventFactory());
	}
	
	@After
	public void teardown(){
		disruptorConfig.controlledShutdown();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_EventHandlerChain_null() {
		disruptorConfig.init();
	}
	
	/**
	 * Publisher -> Ring buffer ---> Consumer A -> Consumer B1 -> Consumer D 
	 * Look at the graph that gets printed by log4j.
	 */
	@Test
	public void test_publish_simple_eventprocessor_topology() {
		ConsumerA consumerA = new ConsumerA();
		ConsumerB1 consumerB1 = new ConsumerB1();
		ConsumerD consumerD = new ConsumerD();
		
		EventHandlerChain<String> eventHandlerChain1 = new EventHandlerChain<String>(new EventHandler[]{consumerA}, new EventHandler[]{consumerB1});
		EventHandlerChain<String> eventHandlerChain2 = new EventHandlerChain<String>(new EventHandler[]{consumerB1}, new EventHandler[]{consumerD});
		
		disruptorConfig.setEventHandlerChain(new EventHandlerChain[]{eventHandlerChain1, eventHandlerChain2});
		disruptorConfig.init();
		
		disruptorConfig.publish(new EventTranslator<String>() {

			@Override
			public void translateTo(String event, long sequence) {
				event = "hi there";
			}
		});
	}
	
	/** 
	 *                                            Consumer B1  
	 *                                           /           \
	 * Publisher -> Ring buffer ---> Consumer A -             -> Consumer D 
	 *                                           \           /
	 *                                            Consumer B2
	 * 
	 * Look at the graph that gets printed by log4j.
	 */
	@Test
	public void test_publish_diamond_eventprocessor_topology() {
		ConsumerA consumerA = new ConsumerA();
		ConsumerB1 consumerB1 = new ConsumerB1();
		ConsumerB2 consumerB2 = new ConsumerB2();
		ConsumerD consumerD = new ConsumerD();
		
		EventHandlerChain<String> eventHandlerChain1 = new EventHandlerChain<String>(new EventHandler[]{consumerA}, new EventHandler[]{consumerB1, consumerB2});
		EventHandlerChain<String> eventHandlerChain2 = new EventHandlerChain<String>(new EventHandler[]{consumerB1, consumerB2}, new EventHandler[]{consumerD});
		
		disruptorConfig.setEventHandlerChain(new EventHandlerChain[]{eventHandlerChain1, eventHandlerChain2});
		disruptorConfig.init();
		
		disruptorConfig.publish(new EventTranslator<String>() {

			@Override
			public void translateTo(String event, long sequence) {
				event = "hi there";
			}
		});
	}
	
	/** 
	 *                                            Consumer B1 -> Consumer C1
	 *                                           /                          \
	 * Publisher -> Ring buffer ---> Consumer A -                            -> Consumer D 
	 *                                           \                          /
	 *                                            Consumer B2 -> Consumer C2
	 * 
	 * Look at the graph that gets printed by log4j.
	 */
	@Test
	public void test_publish_complicated_diamond_eventprocessor_topology() {
		ConsumerA consumerA = new ConsumerA();
		ConsumerB1 consumerB1 = new ConsumerB1();
		ConsumerB2 consumerB2 = new ConsumerB2();
		ConsumerC1 consumerC1 = new ConsumerC1();
		ConsumerC2 consumerC2 = new ConsumerC2();
		ConsumerD consumerD = new ConsumerD();
		
		EventHandlerChain<String> eventHandlerChain1 = new EventHandlerChain<String>(new EventHandler[]{consumerA}, new EventHandler[]{consumerB1, consumerB2});
		EventHandlerChain<String> eventHandlerChain2 = new EventHandlerChain<String>(new EventHandler[]{consumerB1}, new EventHandler[]{consumerC1});
		EventHandlerChain<String> eventHandlerChain3 = new EventHandlerChain<String>(new EventHandler[]{consumerB2}, new EventHandler[]{consumerC2});
		EventHandlerChain<String> eventHandlerChain4 = new EventHandlerChain<String>(new EventHandler[]{consumerC1, consumerC2}, new EventHandler[]{consumerD});
		
		disruptorConfig.setEventHandlerChain(new EventHandlerChain[]{eventHandlerChain1, eventHandlerChain2, eventHandlerChain3, eventHandlerChain4});
		disruptorConfig.init();
		
		disruptorConfig.publish(new EventTranslator<String>() {

			@Override
			public void translateTo(String event, long sequence) {
				event = "hi there";
			}
		});
	}
	
	
	
	private class ConsumerA implements EventHandler<String>{

		@Override
		public void onEvent(String event, long sequence, boolean endOfBatch)
				throws Exception {
			//Do something
		}
		
	}
	
	private class ConsumerB1 implements EventHandler<String>{

		@Override
		public void onEvent(String event, long sequence, boolean endOfBatch)
				throws Exception {
			//Do something
		}
		
	}
	
	private class ConsumerB2 implements EventHandler<String>{

		@Override
		public void onEvent(String event, long sequence, boolean endOfBatch)
				throws Exception {
			//Do something
		}
		
	}
	
	private class ConsumerC1 implements EventHandler<String>{

		@Override
		public void onEvent(String event, long sequence, boolean endOfBatch)
				throws Exception {
			//Do something
		}
		
	}
	
	private class ConsumerC2 implements EventHandler<String>{

		@Override
		public void onEvent(String event, long sequence, boolean endOfBatch)
				throws Exception {
			//Do something
		}
		
	}
	
	private class ConsumerD implements EventHandler<String>{

		@Override
		public void onEvent(String event, long sequence, boolean endOfBatch)
				throws Exception {
			//Do something
		}
		
	}
	
	
	
	private class SampleEventFactory implements EventFactory<String>{

		@Override
		public String newInstance() {
			return new String();
		}
		
	}

}
