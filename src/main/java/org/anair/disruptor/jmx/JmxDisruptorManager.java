package org.anair.disruptor.jmx;

import java.util.Map;

import javax.management.MBeanServer;

import org.anair.disruptor.DisruptorConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Identify all disruptor beans and register them as MBeans.
 * <p> Add this to your spring configuration file and pass in the mbeanserver.
 * <pre>{@code
 *  <bean class="org.anair.disruptor.jmx.JmxDisruptorManager"
		p:mBeanServer-ref="mbeanServer"/>
 * }</pre>
 * 
 * @author Anoop Nair
 *
 */
@SuppressWarnings("rawtypes")
public class JmxDisruptorManager implements ApplicationContextAware{
	private static final Logger LOG = Logger.getLogger(JmxDisruptorManager.class);
	
	private ApplicationContext applicationContext;
	private MBeanServer mBeanServer;
	
	private void registerDisruptorMBeans()  {
		Map<String, DisruptorConfig> disruptorsMBeanMap = getDisruptorMBeans();
		for(Map.Entry<String, DisruptorConfig> entry: disruptorsMBeanMap.entrySet()){
			try {
				JmxDisruptor jmxDisruptor = new JmxDisruptor(entry.getValue(), entry.getKey());
				mBeanServer.registerMBean(jmxDisruptor, jmxDisruptor.getObjectName());
				LOG.debug(entry.getKey() + " Disruptor bean is resgistered in the MBeanServer.");
			} catch (Exception e) {
				LOG.error("Error registering Disruptor MBean.", e);
			}
		}
		LOG.debug("All Disruptor beans regsitered in the MBeanServer");
	}

	private Map<String, DisruptorConfig> getDisruptorMBeans() {
		Map<String, DisruptorConfig> beans = this.applicationContext.getBeansOfType(DisruptorConfig.class);
		LOG.debug("Identified "+beans.size() + " Disruptor beans");
		return beans;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		registerDisruptorMBeans();
	}

	public void setmBeanServer(MBeanServer mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

}
