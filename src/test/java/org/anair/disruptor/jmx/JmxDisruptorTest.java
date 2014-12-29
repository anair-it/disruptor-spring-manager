package org.anair.disruptor.jmx;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.anair.disruptor.DisruptorConfig;
import org.anair.disruptor.WaitStrategyType;
import org.junit.Before;
import org.junit.Test;

import com.lmax.disruptor.dsl.ProducerType;

@SuppressWarnings("rawtypes")
public class JmxDisruptorTest {

	private JmxDisruptor jmxDisruptor;
	private DisruptorConfig mockDisruptorConfig;
	private MBeanInfo mockMBeanInfo;
	private MBeanAttributeInfo mockMBeanAttribute;
	private MBeanOperationInfo mockMBeanOperation;
	private MBeanParameterInfo mockMBeanParameterInfo;
	
	@Before
	public void setUp() throws Exception {
		mockDisruptorConfig = createMock(DisruptorConfig.class);
		mockMBeanInfo = createMock(MBeanInfo.class);
		mockMBeanAttribute = createMock(MBeanAttributeInfo.class);
		mockMBeanOperation = createMock(MBeanOperationInfo.class);
		mockMBeanParameterInfo = createMock(MBeanParameterInfo.class);
		
		jmxDisruptor = new JmxDisruptor(mockDisruptorConfig, "disruptorBean");
		
		assertEquals("disruptor-spring:name=disruptorBean,type=disruptor" , jmxDisruptor.getObjectName().getCanonicalName());
		assertNotNull(mockDisruptorConfig);
	}

	@Test
	public void test_getMBeanDescription() {
		replay(mockMBeanInfo);
		assertTrue(jmxDisruptor.getDescription(mockMBeanInfo).contains("disruptorBean"));
		verify(mockMBeanInfo);
	}
	
	@Test
	public void test_getMBeanAttributeDescription() {
		expect(mockMBeanAttribute.getName()).andReturn("DisruptorConfiguration");
		replay(mockMBeanAttribute);
		assertEquals("Print Disruptor configuration.", jmxDisruptor.getDescription(mockMBeanAttribute));
		verify(mockMBeanAttribute);
		reset(mockMBeanAttribute);
		
		expect(mockMBeanAttribute.getName()).andReturn("EventProcessorGraph").anyTimes();
		replay(mockMBeanAttribute);
		assertEquals("Print Event processor graph depicting dependency barriers.", jmxDisruptor.getDescription(mockMBeanAttribute));
		verify(mockMBeanAttribute);
		reset(mockMBeanAttribute);
		
		expect(mockMBeanAttribute.getName()).andReturn("ThreadName").anyTimes();
		replay(mockMBeanAttribute);
		assertEquals("Ring buffer thread name.", jmxDisruptor.getDescription(mockMBeanAttribute));
		verify(mockMBeanAttribute);
		reset(mockMBeanAttribute);
		
		expect(mockMBeanAttribute.getName()).andReturn("TotalCapacity").anyTimes();
		replay(mockMBeanAttribute);
		assertEquals("Ring buffer capacity.", jmxDisruptor.getDescription(mockMBeanAttribute));
		verify(mockMBeanAttribute);
		reset(mockMBeanAttribute);
		
		expect(mockMBeanAttribute.getName()).andReturn("ProducerType").anyTimes();
		replay(mockMBeanAttribute);
		assertEquals("Ring buffer producer type. Can be SINGLE or MULTI.", jmxDisruptor.getDescription(mockMBeanAttribute));
		verify(mockMBeanAttribute);
		reset(mockMBeanAttribute);
		
		expect(mockMBeanAttribute.getName()).andReturn("WaitStrategyType").anyTimes();
		replay(mockMBeanAttribute);
		assertEquals("Ring buffer wait strategy. Can be one of BLOCKING, YIELDING, BUSY_SPIN etc.", jmxDisruptor.getDescription(mockMBeanAttribute));
		verify(mockMBeanAttribute);
		reset(mockMBeanAttribute);
		
		expect(mockMBeanAttribute.getName()).andReturn("CurrentLocation").anyTimes();
		replay(mockMBeanAttribute);
		assertEquals("Current Ring buffer slot location ready to be consumed.", jmxDisruptor.getDescription(mockMBeanAttribute));
		verify(mockMBeanAttribute);
		reset(mockMBeanAttribute);
		
		expect(mockMBeanAttribute.getName()).andReturn("RemainingCapacity").anyTimes();
		replay(mockMBeanAttribute);
		assertEquals("Remaining slots in the ring buffer.", jmxDisruptor.getDescription(mockMBeanAttribute));
		verify(mockMBeanAttribute);
		reset(mockMBeanAttribute);
	}
	
	@Test
	public void test_getMBeanOperationDescription() {
		expect(mockMBeanOperation.getName()).andReturn("controlledShutdown");
		replay(mockMBeanOperation);
		assertEquals("Shutdown Disruptor and Executor in a controlled manner after all ring buffer events are processed.", jmxDisruptor.getDescription(mockMBeanOperation));
		verify(mockMBeanOperation);
		reset(mockMBeanOperation);
		
		expect(mockMBeanOperation.getName()).andReturn("halt").anyTimes();
		replay(mockMBeanOperation);
		assertEquals("Halt Disruptor and Executor. Do not wait for ring buffer events to be processed.", jmxDisruptor.getDescription(mockMBeanOperation));
		verify(mockMBeanOperation);
		reset(mockMBeanOperation);
		
		expect(mockMBeanOperation.getName()).andReturn("awaitAndShutdown").anyTimes();
		replay(mockMBeanOperation);
		assertEquals("Wait for events to finish for a few seconds and then shutdown.", jmxDisruptor.getDescription(mockMBeanOperation));
		verify(mockMBeanOperation);
		reset(mockMBeanOperation);
		
		expect(mockMBeanOperation.getName()).andReturn("resetRingbuffer").anyTimes();
		replay(mockMBeanOperation);
		assertEquals("Reset the ring buffer cursor to a specific value.", jmxDisruptor.getDescription(mockMBeanOperation));
		verify(mockMBeanOperation);
		reset(mockMBeanOperation);
		
		expect(mockMBeanOperation.getName()).andReturn("publishToRingbuffer").anyTimes();
		replay(mockMBeanOperation);
		assertEquals("Publish the specified sequence to the ring buffer.", jmxDisruptor.getDescription(mockMBeanOperation));
		verify(mockMBeanOperation);
		reset(mockMBeanOperation);
	}
	
	@Test
	public void test_getMBeanOperationParameterDescription() {
		expect(mockMBeanOperation.getName()).andReturn("awaitAndShutdown");
		replay(mockMBeanOperation);
		assertEquals("Time in seconds", jmxDisruptor.getDescription(mockMBeanOperation, mockMBeanParameterInfo, 0));
		verify(mockMBeanOperation);
		reset(mockMBeanOperation);
		
		expect(mockMBeanOperation.getName()).andReturn("resetRingbuffer").anyTimes();
		replay(mockMBeanOperation);
		assertEquals("Ring buffer sequence", jmxDisruptor.getDescription(mockMBeanOperation, mockMBeanParameterInfo, 0));
		verify(mockMBeanOperation);
		reset(mockMBeanOperation);
		
		expect(mockMBeanOperation.getName()).andReturn("publishToRingbuffer").anyTimes();
		replay(mockMBeanOperation);
		assertEquals("Ring buffer sequence", jmxDisruptor.getDescription(mockMBeanOperation, mockMBeanParameterInfo, 0));
		verify(mockMBeanOperation);
		reset(mockMBeanOperation);
	}
	
	@Test 
	public void test_controlledShutdown(){
		mockDisruptorConfig.controlledShutdown();
		replay(mockDisruptorConfig);
		
		jmxDisruptor.controlledShutdown();
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_halt(){
		mockDisruptorConfig.halt();
		replay(mockDisruptorConfig);
		
		jmxDisruptor.halt();
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_awaitAndShutdown(){
		int seconds = 1;
		mockDisruptorConfig.awaitAndShutdown(seconds);
		replay(mockDisruptorConfig);
		
		jmxDisruptor.awaitAndShutdown(seconds);
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_resetRingbuffer(){
		long seq = 1;
		mockDisruptorConfig.resetRingbuffer(seq);
		replay(mockDisruptorConfig);
		
		jmxDisruptor.resetRingbuffer(seq);
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_publishToRingbuffer(){
		long seq = 1;
		mockDisruptorConfig.publishToRingbuffer(seq);
		replay(mockDisruptorConfig);
		
		jmxDisruptor.publishToRingbuffer(seq);
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_getEventProcessorGraph(){
		String consumerDependency = "{A->B}";
		expect(mockDisruptorConfig.getEventProcessorGraph()).andReturn(consumerDependency);
		replay(mockDisruptorConfig);
		
		assertEquals(consumerDependency, jmxDisruptor.getEventProcessorGraph());
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_getThreadName(){
		String str = "name";
		expect(mockDisruptorConfig.getThreadName()).andReturn(str);
		replay(mockDisruptorConfig);
		
		assertEquals(str, jmxDisruptor.getThreadName());
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_getTotalCapacity(){
		int capacity = 8;
		expect(mockDisruptorConfig.getRingBufferSize()).andReturn(capacity);
		replay(mockDisruptorConfig);
		
		assertEquals(capacity, jmxDisruptor.getTotalCapacity());
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_getProducerType(){
		String str = "SINGLE";
		expect(mockDisruptorConfig.getProducerType()).andReturn(ProducerType.SINGLE);
		replay(mockDisruptorConfig);
		
		assertEquals(str, jmxDisruptor.getProducerType());
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_getWaitStrategyType(){
		String str = "BUSY_SPIN";
		expect(mockDisruptorConfig.getWaitStrategyType()).andReturn(WaitStrategyType.BUSY_SPIN);
		replay(mockDisruptorConfig);
		
		assertEquals(str, jmxDisruptor.getWaitStrategyType());
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_getCurrentLocation(){
		long l = 10;
		expect(mockDisruptorConfig.getCurrentLocation()).andReturn(l);
		replay(mockDisruptorConfig);
		
		assertEquals(l, jmxDisruptor.getCurrentLocation());
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_getRemainingCapacity(){
		long l = 10;
		expect(mockDisruptorConfig.getRemainingCapacity()).andReturn(l);
		replay(mockDisruptorConfig);
		
		assertEquals(l, jmxDisruptor.getRemainingCapacity());
		verify(mockDisruptorConfig);
	}
	
	@Test 
	public void test_getDisruptorConfiguration(){
		String str = "config";
		expect(mockDisruptorConfig.getDisruptorConfiguration()).andReturn(str);
		replay(mockDisruptorConfig);
		
		assertEquals(str, jmxDisruptor.getDisruptorConfiguration());
		verify(mockDisruptorConfig);
	}

}
