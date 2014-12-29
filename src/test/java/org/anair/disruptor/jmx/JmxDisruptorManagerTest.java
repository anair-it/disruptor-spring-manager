package org.anair.disruptor.jmx;

import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.anair.disruptor.DisruptorConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("rawtypes")
public class JmxDisruptorManagerTest {

	private JmxDisruptorManager manager;
	private ApplicationContext mockApplicationContext;
	private MBeanServer mockMBeanServer;
	private ObjectInstance mockObjectInstance;
	
	@Before
	public void setUp() throws Exception {
		mockApplicationContext = createMock(ApplicationContext.class);
		mockMBeanServer = createMock(MBeanServer.class);
		mockObjectInstance = createMock(ObjectInstance.class);
		
		manager = new JmxDisruptorManager();
		manager.setmBeanServer(mockMBeanServer);
	}

	@Test
	public void test_registerDisruptorMBeans_no_disruptorBeans_found() {
		expect(mockApplicationContext.getBeansOfType(DisruptorConfig.class)).andReturn(new HashMap<String, DisruptorConfig>());
		replay(mockApplicationContext);
		manager.setApplicationContext(mockApplicationContext);
		
		verify(mockApplicationContext);
		reset(mockApplicationContext);
		
		expect(mockApplicationContext.getBeansOfType(DisruptorConfig.class)).andReturn(null);
		replay(mockApplicationContext);
		manager.setApplicationContext(mockApplicationContext);
		
		verify(mockApplicationContext);
	}
	
	@Test
	public void test_registerDisruptorMBeans_exception() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		Map<String, DisruptorConfig> disruptorBeanMap = new HashMap<String, DisruptorConfig>();
		disruptorBeanMap.put("bean1", new DisruptorConfig());
		expect(mockApplicationContext.getBeansOfType(DisruptorConfig.class)).andReturn(disruptorBeanMap);
		
		expect(mockMBeanServer.registerMBean(isA(JmxDisruptor.class), isA(ObjectName.class)))
			.andThrow(new MBeanRegistrationException(new Exception()));
		
		replay(mockApplicationContext, mockMBeanServer);
		manager.setApplicationContext(mockApplicationContext);
		
		verify(mockApplicationContext, mockMBeanServer);
	}
	
	@Test
	public void test_registerDisruptorMBeans_disruptorBeans_found() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		Map<String, DisruptorConfig> disruptorBeanMap = new HashMap<String, DisruptorConfig>();
		disruptorBeanMap.put("bean1", new DisruptorConfig());
		disruptorBeanMap.put("bean2", new DisruptorConfig());
		expect(mockApplicationContext.getBeansOfType(DisruptorConfig.class)).andReturn(disruptorBeanMap);
		
		expect(mockMBeanServer.registerMBean(isA(JmxDisruptor.class), isA(ObjectName.class)))
			.andStubReturn(mockObjectInstance);
		expect(mockMBeanServer.registerMBean(isA(JmxDisruptor.class), isA(ObjectName.class)))
			.andStubReturn(mockObjectInstance);
		
		replay(mockApplicationContext, mockMBeanServer);
		manager.setApplicationContext(mockApplicationContext);
		
		verify(mockApplicationContext, mockMBeanServer);
	}

}
