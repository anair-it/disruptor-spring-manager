package org.anair.disruptor;

import org.anair.disruptor.exception.DisruptorExceptionHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Create and configure a default LMAX Disruptor spring bean.
 * 
 * <p>Required values:
 * <ul>
 * 	<li>Executor Thread name: This will help in debugging disruptor logs by looking for the thread name.</li>
 * 	<li>EventFactory: Factory wrapper around the model object. This is used to prepare the ring buffer with the model type objects. </li>
 * 	<li>EventHandlerChain: Design the dependency barrier and event processor/consumer dependencies in a chained manner.</li>
 * </ul>
 * 
 * <p>Default values, if not provided through Spring configuration:
 * <ul>
 * 	<li>Ring buffer size: 1024</li>
 *  <li>Producer Type: {@link ProducerType.SINGLE}</li>
 *  <li>Wait Strategy Type: {@link WaitStrategyType.BLOCKING}</li>
 * </ul>
 * 
 * <p>
 * Sample Spring configuration:
 * <pre>{@code
	<bean id="billingDisruptor" class="org.anair.disruptor.DefaultDisruptorConfig"
		init-method="init" destroy-method="controlledShutdown">

		<property name="threadName" value="billingThread" />
		<property name="eventFactory">
			<bean
				class="org.anair.disruptor.eventfactory.BillingEvent" />
		</property>
		<property name="eventHandlerChain">
			<array>
				<bean class="org.anair.disruptor.EventHandlerChain" scope="prototype">
					<constructor-arg name="currentEventHandlers">
						<array value-type="com.lmax.disruptor.EventHandler">
							<ref bean="journalBillingEventProcessor" />
							<ref bean="billingValidationEventProcessor" />
						</array>
					</constructor-arg>
					<constructor-arg name="nextEventHandlers">
						<array value-type="com.lmax.disruptor.EventHandler">
							<ref bean="billingBusinessEventProcessor" />
							<ref bean="corporateBillingBusinessEventProcessor" />
							<ref bean="customerSpecificBillingBusinessEventProcessor" />
						</array>
					</constructor-arg>
				</bean>
			</array>
		</property>
	</bean>

 * }</pre>
 * 
 * @see <a href="https://github.com/LMAX-Exchange/disruptor/wiki/Getting-Started">LMAX Disruptor</a> 
 * @author Anoop Nair
 *
 * @param <T>
 */
public class DefaultDisruptorConfig<T> extends BaseDisruptorConfig<T>{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultDisruptorConfig.class);

	private EventHandlerChain<T>[] eventHandlerChain;

	@Override
	public void publish(EventTranslator<T> eventTranslator){
		getRingBuffer().publishEvent(eventTranslator);
		LOG.debug("Published " + eventTranslator.getClass().getSimpleName()  +" event to sequence: " + getCurrentLocation());
	}
	
	@Override
	public void disruptorEventHandler() {
		Validate.notEmpty(eventHandlerChain, "Define a Event Handler Chain.");
		disruptorEventHandlerChain();
	}

	@Override
	public void disruptorExceptionHandler() {
		getDisruptor().setDefaultExceptionHandler(new DisruptorExceptionHandler<T>(getThreadName()));
	}
	
	private void disruptorEventHandlerChain() {
		for(int i=0;i<eventHandlerChain.length;i++){
			EventHandlerChain<T> eventHandlersChain = eventHandlerChain[i];
			EventHandlerGroup<T> eventHandlerGroup = null;
			if(i == 0){
				eventHandlerGroup = getDisruptor().handleEventsWith(eventHandlersChain.getCurrentEventHandlers());
			}else{
				eventHandlerGroup = getDisruptor().after(eventHandlersChain.getCurrentEventHandlers());
			}
			
			if(! ArrayUtils.isEmpty(eventHandlersChain.getNextEventHandlers())){
				eventHandlerGroup.then(eventHandlersChain.getNextEventHandlers());
			}
		}
		
		getEventProcessorGraph();
	}
	
	public String getEventProcessorGraph(){
		StringBuilder str = new StringBuilder();
		for(int i=0;i<eventHandlerChain.length;i++){
			str.append("\n");
			str.append(eventHandlerChain[i].printDependencyGraph());
		}
		LOG.info(str.toString());
		
		return str.toString();
	}

	public void setEventHandlerChain(EventHandlerChain<T>[] eventHandlerChain) {
		this.eventHandlerChain = eventHandlerChain;
	}

}
