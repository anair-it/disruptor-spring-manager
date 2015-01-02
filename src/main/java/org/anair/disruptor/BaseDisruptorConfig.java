package org.anair.disruptor;

import java.util.concurrent.Executors;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Base class to configure a Disruptor and Ring buffer.
 * 
 * @author Anoop Nair
 *
 * @param <T>
 */
public abstract class BaseDisruptorConfig<T> extends AbstractDisruptorLifecycleManager<T> implements DisruptorConfig<T>  {

	private static final Logger LOG = Logger.getLogger(BaseDisruptorConfig.class);
	private int ringBufferSize = 1024;
	private ProducerType producerType = ProducerType.SINGLE;
	private WaitStrategyType waitStrategyType = WaitStrategyType.BLOCKING;
	
	private EventFactory<T> eventFactory;

	@Override
	public void init(){
		Validate.notNull(getThreadName());
		Validate.notNull(getEventFactory());
		
		createThreadExecutor();
		configureDisruptor();
		
		disruptorExceptionHandler();
		disruptorEventHandler();
		
		getDisruptor().start();
	}
	
	private void configureDisruptor(){
		String disruptorConfigString = getDisruptorConfiguration();
		LOG.debug("Going to create a LMAX disruptor "+ disruptorConfigString);
		
		setDisruptor(new Disruptor<T>(
				getEventFactory(),
				getRingBufferSize(),
				getExecutor(),
				getProducerType(),
				getWaitStrategyType().instance()
		));
		
		LOG.info("Created and configured LMAX disruptor "+ disruptorConfigString);
	}
	
	@Override
	public abstract void disruptorExceptionHandler();
	
	@Override
	public abstract void disruptorEventHandler();
	
	@Override
	public abstract void publish(EventTranslator<T> eventTranslator); 
	
	private void createThreadExecutor() {
		super.setExecutor(Executors.newCachedThreadPool(new NamedThreadFactory(getThreadName())));
		LOG.debug("Created a cache thread pool based disruptor with name: " + getThreadName());
	}
	
	public String getDisruptorConfiguration() {
		StringBuilder str = new StringBuilder();
		str.append("{");
		str.append("Thread Name: ");
		str.append(getThreadName());
		str.append(" | ");
		str.append("Ringbuffer slot size: ");
		str.append(getRingBufferSize());
		str.append(" | ");
		str.append("Producer type: ");
		str.append(getProducerType());
		str.append(" | ");
		str.append("Wait strategy: ");
		str.append(getWaitStrategyType());
		str.append("}");
		return str.toString();
	}
	
	protected RingBuffer<T> getRingBuffer(){
		return getDisruptor().getRingBuffer();
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
	
	public int getRingBufferSize() {
		return ringBufferSize;
	}

	public void setRingBufferSize(int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
	}

	public ProducerType getProducerType() {
		return producerType;
	}

	public void setProducerType(ProducerType producerType) {
		this.producerType = producerType;
	}

	public WaitStrategyType getWaitStrategyType() {
		return waitStrategyType;
	}

	public void setWaitStrategyType(WaitStrategyType waitStrategyType) {
		this.waitStrategyType = waitStrategyType;
	}

	protected EventFactory<T> getEventFactory() {
		return eventFactory;
	}

	public void setEventFactory(EventFactory<T> eventFactory) {
		this.eventFactory = eventFactory;
	}
}
