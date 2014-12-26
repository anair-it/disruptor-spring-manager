package org.anair.disruptor;

import org.anair.disruptor.exception.DisruptorExceptionHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Create and configure a LMAX Disruptor spring bean.
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
	<bean id="billingDisruptor" class="org.anair.disruptor.DisruptorConfig"
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
public class DisruptorConfig<T> extends DisruptorLifecycleManager<T>{
	private static final Logger LOG = Logger.getLogger(DisruptorConfig.class);

	private int ringBufferSize = 1024;
	private ProducerType producerType = ProducerType.SINGLE;
	private WaitStrategyType waitStrategyType = WaitStrategyType.BLOCKING;
	
	private EventFactory<T> eventFactory;
	private EventHandlerChain<T>[] eventHandlerChain;

	@Override
	public void configureDisruptor() {
		Validate.notNull(this.eventFactory);
		Validate.notEmpty(eventHandlerChain);
		
		createAndConfigureDisruptor();
		
		disruptorExceptionHandler();
		
		disruptorEventHandlerChain();
		
		LOG.debug("Ringbuffer is configured with "+ getTotalCapacity() +" slots and is ready to use.");
	}
	
	@Override
	public void publish(EventTranslator<T> eventTranslator){
		getRingBuffer().publishEvent(eventTranslator);
		LOG.debug("Published " + eventTranslator.getClass().getSimpleName()  +" event to sequence: " + getCurrentLocation());
	}
	
	private void createAndConfigureDisruptor() {
		String disruptorConfigString = getDisruptorConfiguration();
		LOG.debug("Going to create a LMAX disruptor "+ disruptorConfigString);
		
		setDisruptor(new Disruptor<T>(
				this.eventFactory,
				getTotalCapacity(),
				getExecutor(),
				this.producerType,
				this.waitStrategyType.instance()
		));
		
		LOG.info("Created and configured LMAX disruptor "+ disruptorConfigString);
	}

	public String getDisruptorConfiguration() {
		StringBuilder str = new StringBuilder();
		str.append("{");
		str.append("Thread Name: ");
		str.append(getThreadName());
		str.append(" | ");
		str.append("Ringbuffer slot size: ");
		str.append(getTotalCapacity());
		str.append(" | ");
		str.append("Producer type: ");
		str.append(getProducerType());
		str.append(" | ");
		str.append("Wait strategy: ");
		str.append(getWaitStrategyType());
		str.append("}");
		return str.toString();
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

	private void disruptorExceptionHandler() {
		getDisruptor().handleExceptionsWith(new DisruptorExceptionHandler(getThreadName()));
	}

	public void setEventFactory(EventFactory<T> eventFactory) {
		this.eventFactory = eventFactory;
	}

	public void setEventHandlerChain(EventHandlerChain<T>[] eventHandlerChain) {
		this.eventHandlerChain = eventHandlerChain;
	}
	
	public void setProducerType(ProducerType producerType) {
		this.producerType = producerType;
	}

	public void setWaitStrategyType(WaitStrategyType waitStrategyType) {
		this.waitStrategyType = waitStrategyType;
	}

	public void setRingBufferSize(int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
	}
	
	private RingBuffer<T> getRingBuffer(){
		return getDisruptor().getRingBuffer();
	}
	
	public int getTotalCapacity() {
		return ringBufferSize;
	}

	public String getProducerType() {
		return producerType.name();
	}

	public String getWaitStrategyType() {
		return waitStrategyType.name();
	}
	
	public long getCurrentLocation() {
		return getDisruptor().getCursor();
	}
	
	public long getRemainingCapacity() {
		return getRingBuffer().remainingCapacity();
	}
	
	public void resetRingbuffer(long sequence) {
		getRingBuffer().resetTo(sequence);
	}
	
	public void publishToRingbuffer(long sequence) {
		getRingBuffer().publish(sequence);;
	}

}
