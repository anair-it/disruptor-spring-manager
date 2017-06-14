package org.anair.disruptor;

import java.util.StringJoiner;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory.getLogger(BaseDisruptorConfig.class);
	private int ringBufferSize = 1024;
	private ProducerType producerType = ProducerType.SINGLE;
	private WaitStrategyType waitStrategyType = WaitStrategyType.BLOCKING;
	
	private EventFactory<T> eventFactory;

	@Override
	public void init(){
		Validate.notNull(getThreadName());
		Validate.notNull(getEventFactory());
		
		createThreadFactory();
		configureDisruptor();
		
		disruptorExceptionHandler();
		disruptorEventHandler();
		
		getDisruptor().start();
	}
	
	private void configureDisruptor(){
		String disruptorConfigString = getDisruptorConfiguration();
		LOG.info("Going to create a LMAX disruptor "+ disruptorConfigString);
		
		setDisruptor(new Disruptor<T>(
				getEventFactory(),
				getRingBufferSize(),
				getThreadFactory(),
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
	
	private void createThreadFactory() {
		super.setThreadFactory(new NamedThreadFactory(getThreadName()));
	}
	
	public String getDisruptorConfiguration() {
		StringJoiner str = new StringJoiner(" | ", "{", "}");
		str.add("Thread Name: " + getThreadName());
		str.add("Ringbuffer slot size: " + getRingBufferSize());
		str.add("Producer type: " + getProducerType().name());
		str.add("Wait strategy: " + getWaitStrategyType().name());
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
