package org.anair.disruptor.jmx;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.anair.disruptor.DisruptorConfig;
import org.apache.log4j.Logger;

/**
 * Exposes LMAX disruptor attributes and operations in a JMX MBean.
 * <p>Used by {@link JmxDisruptorManager} to resgiter this as a MBean.
 * 
 * <p>Exposed attributes and operations are accessed from {@link DisruptorConfig}. 
 * Operation and attribute descriptions are defined here. 
 * 
 * @author Anoop Nair
 *
 */
@SuppressWarnings("rawtypes")
public class JmxDisruptor extends StandardMBean implements JmxDisruptorMBean {
	private static final String DISRUPTOR_JMX_MBEAN_NAME = "disruptor-spring:type=disruptor,name=";

	private static final Logger LOG = Logger.getLogger(JmxDisruptor.class);
	
	private DisruptorConfig disruptorConfig;
	private ObjectName objectName;
	private String beanName;
	
	public JmxDisruptor(DisruptorConfig disruptorConfig, String beanName) throws NotCompliantMBeanException {
		super(JmxDisruptorMBean.class);
		this.disruptorConfig = disruptorConfig;
		this.beanName = beanName;
		objectName = createObjectName(beanName);
	}
	
	
	/* (non-Javadoc)
	 * @see javax.management.StandardMBean#getDescription(javax.management.MBeanInfo)
	 */
	protected String getDescription(MBeanInfo info) {
        return "Spring managed LMAX Disruptor bean: " + beanName;
    }
	
	
	/* (non-Javadoc)
	 * @see javax.management.StandardMBean#getDescription(javax.management.MBeanAttributeInfo)
	 */
	protected String getDescription(MBeanAttributeInfo attribute) {
		if (attribute.getName().equals("DisruptorConfiguration")) {
            return "Print Disruptor configuration.";
        }else if (attribute.getName().equals("EventProcessorGraph")) {
            return "Print Event processor graph depicting dependency barriers.";
        }else if (attribute.getName().equals("ThreadName")) {
        	return "Ring buffer thread name.";
        }else if (attribute.getName().equals("TotalCapacity")) {
        	return "Ring buffer capacity.";
        }else if (attribute.getName().equals("ProducerType")) {
        	return "Ring buffer producer type. Can be SINGLE or MULTI.";
        }else if (attribute.getName().equals("WaitStrategyType")) {
        	return "Ring buffer wait strategy. Can be one of BLOCKING, YIELDING, BUSY_SPIN etc.";
        }else if (attribute.getName().equals("CurrentLocation")) {
        	return "Current Ring buffer slot location ready to be consumed.";
        }else if (attribute.getName().equals("RemainingCapacity")) {
        	return "Remaining slots in the ring buffer.";
        }
        return null;
    }
	
	/* (non-Javadoc)
	 * @see javax.management.StandardMBean#getDescription(javax.management.MBeanOperationInfo)
	 */
	protected String getDescription(MBeanOperationInfo operation) {
        if (operation.getName().equals("controlledShutdown")) {
            return "Shutdown Disruptor and Executor in a controlled manner after all ring buffer events are processed.";
        }else if (operation.getName().equals("halt")) {
            return "Halt Disruptor and Executor. Do not wait for ring buffer events to be processed.";
        }else if (operation.getName().equals("awaitAndShutdown")) {
            return "Wait for events to finish for a few seconds and then shutdown.";
        }else if (operation.getName().equals("resetRingbuffer")) {
            return "Reset the ring buffer cursor to a specific value.";
        }else if (operation.getName().equals("publishToRingbuffer")) {
            return "Publish the specified sequence to the ring buffer.";
        }
        
        return null;
    }
	
	/* (non-Javadoc)
	 * @see javax.management.StandardMBean#getDescription(javax.management.MBeanOperationInfo, javax.management.MBeanParameterInfo, int)
	 */
	protected String getDescription(MBeanOperationInfo op, MBeanParameterInfo param, int sequence) {
        if (op.getName().equals("awaitAndShutdown")) {
            switch (sequence) {
                case 0: return "Time in seconds";
                default : return null;
            }
        }else if (op.getName().equals("resetRingbuffer")) {
            switch (sequence) {
	            case 0: return "Ring buffer sequence";
	            default : return null;
            }
        }else if (op.getName().equals("publishToRingbuffer")) {
            switch (sequence) {
	            case 0: return "Ring buffer sequence";
	            default : return null;
            }
        }
        return null;
    }

	private ObjectName createObjectName(String disruptorBeanName) {
        ObjectName objectName = null;
        try {
			objectName = new ObjectName(DISRUPTOR_JMX_MBEAN_NAME + disruptorBeanName);
			LOG.info(objectName.getCanonicalName() + " MBean defined.");
		} catch (Exception e) {
			LOG.error("Error creating Disruptor Bean ObjectName. ", e);
		}
        return objectName;
    }
	
	protected ObjectName getObjectName() {
		return objectName;
	}
	
	@Override
	public void controlledShutdown() {
		disruptorConfig.controlledShutdown();
	}

	@Override
	public void halt() {
		disruptorConfig.halt();
	}

	@Override
	public void awaitAndShutdown(long time) {
		disruptorConfig.awaitAndShutdown(time);
	}

	@Override
	public void resetRingbuffer(long sequence) {
		disruptorConfig.resetRingbuffer(sequence);
	}

	@Override
	public void publishToRingbuffer(long sequence) {
		disruptorConfig.publishToRingbuffer(sequence);
	}

	@Override
	public String getEventProcessorGraph() {
		return disruptorConfig.getEventProcessorGraph();
	}

	@Override
	public String getThreadName() {
		return disruptorConfig.getThreadName();
	}

	@Override
	public int getTotalCapacity() {
		return disruptorConfig.getRingBufferSize();
	}

	@Override
	public String getProducerType() {
		return disruptorConfig.getProducerType().name();
	}

	@Override
	public String getWaitStrategyType() {
		return disruptorConfig.getWaitStrategyType().name();
	}

	@Override
	public long getCurrentLocation() {
		return disruptorConfig.getCurrentLocation();
	}

	@Override
	public long getRemainingCapacity() {
		return disruptorConfig.getRemainingCapacity();
	}

	@Override
	public String getDisruptorConfiguration() {
		return disruptorConfig.getDisruptorConfiguration();
	}

}
